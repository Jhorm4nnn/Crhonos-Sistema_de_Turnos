package com.pamplona.turnos.adaptadores.entrada.rest;

import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.aplicacion.puertos.salida.ValidadorTokenPort;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.excepciones.AccesoDenegadoException;
import com.pamplona.turnos.excepciones.AutenticacionException;
import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.DuplicadoException;
import com.pamplona.turnos.excepciones.NoEncontradoException;
import com.pamplona.turnos.excepciones.ReglaTurnoException;
import com.pamplona.turnos.excepciones.TurnosException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de entrada REST (HU-7). Traduce HTTP a casos de uso y los resultados a JSON.
 * No tiene reglas de negocio: solo valida el token, convierte datos y elige el codigo HTTP.
 * Cada respuesta lleva la cabecera X-Tiempo-Ms (HU-9).
 *
 * GET  /api/salud                          (sin token)
 * POST /api/clientes        GET /api/clientes
 * POST /api/profesionales   GET /api/profesionales
 * POST /api/turnos          GET /api/turnos?profesionalId=&clienteId=&desde=&hasta=
 * POST /api/turnos/{id}/cancelar      PUT /api/turnos/{id}   (reprogramar)
 */
public class ApiRest {

    private static final Pattern TURNO_CANCELAR = Pattern.compile("/api/turnos/(\\d+)/cancelar");
    private static final Pattern TURNO_ID = Pattern.compile("/api/turnos/(\\d+)");
    private static final int MAX_CUERPO = 64 * 1024;

    private record Respuesta(int estado, String cuerpo) { }

    private final HttpServer servidor;
    private final CasosDeUso casos;
    private final ValidadorTokenPort validador;

    public ApiRest(int puerto, CasosDeUso casos, ValidadorTokenPort validador) throws IOException {
        this.casos = casos;
        this.validador = validador;
        this.servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        this.servidor.createContext("/", this::atender);
        this.servidor.setExecutor(Executors.newFixedThreadPool(4));
    }

    public void iniciar() {
        servidor.start();
    }

    public void detener() {
        servidor.stop(0);
    }

    public int puerto() {
        return servidor.getAddress().getPort();
    }

    private void atender(HttpExchange ex) throws IOException {
        long inicio = System.nanoTime();
        Respuesta r;
        try {
            r = enrutar(ex);
        } catch (TurnosException e) {
            r = new Respuesta(codigo(e), Representaciones.error(e.getMessage()));
        } catch (RuntimeException e) {
            r = new Respuesta(500, Representaciones.error("Ocurrio un problema inesperado. Intente de nuevo."));
        }
        byte[] bytes = r.cuerpo().getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        ex.getResponseHeaders().set("X-Tiempo-Ms", String.format("%.2f", (System.nanoTime() - inicio) / 1_000_000.0));
        ex.sendResponseHeaders(r.estado(), bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    private Respuesta enrutar(HttpExchange ex) throws IOException {
        String metodo = ex.getRequestMethod();
        String ruta = ex.getRequestURI().getPath();

        if (ruta.equals("/api/salud") && metodo.equals("GET")) {
            return new Respuesta(200, "{\"estado\":\"ok\"}");
        }
        if (!ruta.startsWith("/api/")) {
            return new Respuesta(404, Representaciones.error("Ruta no encontrada."));
        }
        UsuarioAutenticado usuario = autenticar(ex);

        switch (ruta) {
            case "/api/clientes":
                if (metodo.equals("POST")) {
                    return new Respuesta(201, Representaciones.cliente(casos.registrarCliente().ejecutar(usuario, comandoCliente(cuerpo(ex)))));
                }
                if (metodo.equals("GET")) {
                    return new Respuesta(200, Representaciones.lista(casos.listarClientes().ejecutar(usuario), Representaciones::cliente));
                }
                return noPermitido();
            case "/api/profesionales":
                if (metodo.equals("POST")) {
                    return new Respuesta(201, Representaciones.profesional(casos.registrarProfesional().ejecutar(usuario, comandoProfesional(cuerpo(ex)))));
                }
                if (metodo.equals("GET")) {
                    return new Respuesta(200, Representaciones.lista(casos.listarProfesionales().ejecutar(usuario), Representaciones::profesional));
                }
                return noPermitido();
            case "/api/turnos":
                if (metodo.equals("POST")) {
                    Map<String, String> d = Json.leerObjeto(cuerpo(ex));
                    var comando = new Comandos.CrearTurno(entero(d, "clienteId"), entero(d, "profesionalId"),
                            fecha(d, "fecha"), hora(d, "hora"));
                    return new Respuesta(201, Representaciones.turno(casos.crearTurno().ejecutar(usuario, comando)));
                }
                if (metodo.equals("GET")) {
                    Map<String, String> q = consulta(ex);
                    var filtro = new Comandos.FiltroTurnos(entero(q, "profesionalId"), entero(q, "clienteId"),
                            fecha(q, "desde"), fecha(q, "hasta"));
                    return new Respuesta(200, Representaciones.lista(casos.consultarTurnos().ejecutar(usuario, filtro), Representaciones::turno));
                }
                return noPermitido();
            default:
                break;
        }

        Matcher cancelar = TURNO_CANCELAR.matcher(ruta);
        if (cancelar.matches()) {
            if (!metodo.equals("POST")) {
                return noPermitido();
            }
            return new Respuesta(200, Representaciones.turno(
                    casos.cancelarTurno().ejecutar(usuario, Integer.parseInt(cancelar.group(1)))));
        }
        Matcher porId = TURNO_ID.matcher(ruta);
        if (porId.matches()) {
            if (!metodo.equals("PUT")) {
                return noPermitido();
            }
            Map<String, String> d = Json.leerObjeto(cuerpo(ex));
            var comando = new Comandos.ReprogramarTurno(Integer.parseInt(porId.group(1)), fecha(d, "fecha"), hora(d, "hora"));
            return new Respuesta(200, Representaciones.turno(casos.reprogramarTurno().ejecutar(usuario, comando)));
        }
        return new Respuesta(404, Representaciones.error("Ruta no encontrada."));
    }

    // ---- seguridad ----
    private UsuarioAutenticado autenticar(HttpExchange ex) {
        String cabecera = ex.getRequestHeaders().getFirst("Authorization");
        if (cabecera == null || !cabecera.regionMatches(true, 0, "Bearer ", 0, 7)) {
            throw new AutenticacionException("Falta el token. Envie la cabecera Authorization: Bearer <token>.");
        }
        return validador.validar(cabecera.substring(7).trim());
    }

    private int codigo(TurnosException e) {
        if (e instanceof AutenticacionException) {
            return 401;
        }
        if (e instanceof AccesoDenegadoException) {
            return 403;
        }
        if (e instanceof NoEncontradoException) {
            return 404;
        }
        if (e instanceof DuplicadoException || e instanceof ReglaTurnoException) {
            return 409;
        }
        return 400; // DatosInvalidosException y cualquier otro error de datos
    }

    private Respuesta noPermitido() {
        return new Respuesta(405, Representaciones.error("Metodo no permitido para esta ruta."));
    }

    // ---- conversion de datos ----
    private Comandos.RegistrarCliente comandoCliente(String json) {
        Map<String, String> d = Json.leerObjeto(json);
        return new Comandos.RegistrarCliente(d.get("nombre"), d.get("documento"), d.get("telefono"), d.get("correo"),
                Genero.desde(d.get("genero")));
    }

    private Comandos.RegistrarProfesional comandoProfesional(String json) {
        Map<String, String> d = Json.leerObjeto(json);
        return new Comandos.RegistrarProfesional(d.get("nombre"), d.get("documento"), d.get("telefono"), d.get("correo"),
                Genero.desde(d.get("genero")), hora(d, "horaInicio"), hora(d, "horaFin"));
    }

    private String cuerpo(HttpExchange ex) throws IOException {
        byte[] bytes = ex.getRequestBody().readNBytes(MAX_CUERPO + 1);
        if (bytes.length > MAX_CUERPO) {
            throw new DatosInvalidosException("El cuerpo de la peticion es demasiado grande.");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Map<String, String> consulta(HttpExchange ex) {
        Map<String, String> mapa = new HashMap<>();
        String q = ex.getRequestURI().getRawQuery();
        if (q != null && !q.isBlank()) {
            for (String par : q.split("&")) {
                String[] kv = par.split("=", 2);
                String valor = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
                if (!valor.isBlank()) {
                    mapa.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), valor);
                }
            }
        }
        return mapa;
    }

    private Integer entero(Map<String, String> d, String campo) {
        String v = d.get(campo);
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(v.trim());
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("'" + campo + "' debe ser un numero.");
        }
    }

    private LocalDate fecha(Map<String, String> d, String campo) {
        String v = d.get(campo);
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(v.trim());
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("'" + campo + "' es invalida. Use el formato AAAA-MM-DD.");
        }
    }

    private LocalTime hora(Map<String, String> d, String campo) {
        String v = d.get(campo);
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(v.trim());
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("'" + campo + "' es invalida. Use el formato HH:mm.");
        }
    }
}
