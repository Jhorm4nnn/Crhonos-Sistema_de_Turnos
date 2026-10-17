package com.pamplona.turnos.adaptadores.salida.seguridad;

import com.pamplona.turnos.aplicacion.puertos.salida.AutenticadorPort;
import com.pamplona.turnos.excepciones.AutenticacionException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de salida hacia el authcore-service.
 * Contrato: POST {url}/api/auth/login  {"username","password"}  ->  200 {"token":"<JWT>",...}  |  401 {"error":"..."}
 */
public class AuthCoreHttpClient implements AutenticadorPort {

    private static final Pattern TOKEN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");

    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public AuthCoreHttpClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String login(String usuario, String clave) {
        String cuerpo = "{\"username\":\"" + escapar(usuario) + "\",\"password\":\"" + escapar(clave) + "\"}";
        HttpResponse<String> respuesta;
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(baseUrl + "/api/auth/login"))
                    .timeout(Duration.ofSeconds(8))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                    .build();
            respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | IllegalArgumentException e) {
            throw noDisponible();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw noDisponible();
        }

        int estado = respuesta.statusCode();
        if (estado == 200) {
            Matcher m = TOKEN.matcher(respuesta.body());
            if (m.find()) {
                return m.group(1);
            }
            throw new AutenticacionException("El servicio de autenticacion respondio algo inesperado.");
        }
        if (estado == 400 || estado == 401 || estado == 403) {
            throw new AutenticacionException("Usuario o clave incorrectos.");
        }
        throw noDisponible();
    }

    private AutenticacionException noDisponible() {
        return new AutenticacionException(
                "No se pudo conectar con el servicio de autenticacion. Verifique que este en ejecucion.");
    }

    private String escapar(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
