package com.pamplona.turnos.adaptadores.salida.seguridad;

import com.pamplona.turnos.aplicacion.puertos.salida.ValidadorTokenPort;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.excepciones.AutenticacionException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adaptador de salida: valida un JWT firmado con HS256 usando la misma clave compartida del authcore
 * (sin llamar al authcore en cada peticion). Comprueba firma, algoritmo y vigencia, y lee usuario y roles.
 * Los roles pueden venir como lista ("roles":["ADMIN"]) o como un solo texto ("role":"ROLE_ADMIN").
 */
public class JwtHs256Validador implements ValidadorTokenPort {

    private static final Pattern ALG = Pattern.compile("\"alg\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern SUB = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");
    private static final Pattern ROLES = Pattern.compile("\"roles\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern ROL = Pattern.compile("\"role\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern ITEM = Pattern.compile("\"([^\"]+)\"");

    private final byte[] secreto;
    private final Clock reloj;

    public JwtHs256Validador(String secreto, Clock reloj) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.reloj = reloj;
    }

    @Override
    public UsuarioAutenticado validar(String token) {
        return analizar(token, true);
    }

    @Override
    public UsuarioAutenticado leer(String token) {
        return analizar(token, false);
    }

    private UsuarioAutenticado analizar(String token, boolean verificarFirma) {
        if (token == null || token.isBlank()) {
            throw new AutenticacionException("Falta el token de autenticacion.");
        }
        String[] partes = token.trim().split("\\.");
        if (partes.length != 3) {
            throw invalido();
        }
        String cabecera;
        String contenido;
        byte[] firma;
        try {
            cabecera = new String(Base64.getUrlDecoder().decode(partes[0]), StandardCharsets.UTF_8);
            contenido = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            firma = Base64.getUrlDecoder().decode(partes[2]);
        } catch (IllegalArgumentException e) {
            throw invalido();
        }
        if (verificarFirma) {
            Matcher alg = ALG.matcher(cabecera);
            if (!alg.find() || !"HS256".equals(alg.group(1))) {
                throw invalido();
            }
            if (!MessageDigest.isEqual(firmar(partes[0] + "." + partes[1]), firma)) {
                throw new AutenticacionException("El token no es autentico (firma incorrecta).");
            }
        }

        Matcher sub = SUB.matcher(contenido);
        Matcher exp = EXP.matcher(contenido);
        if (!sub.find() || !exp.find()) {
            throw invalido();
        }
        Instant expira = Instant.ofEpochSecond(Long.parseLong(exp.group(1)));
        if (!Instant.now(reloj).isBefore(expira)) {
            throw new AutenticacionException("El token expiro. Inicie sesion de nuevo.");
        }
        return new UsuarioAutenticado(sub.group(1), leerRoles(contenido), expira, token.trim());
    }

    private List<String> leerRoles(String contenido) {
        List<String> roles = new ArrayList<>();
        Matcher lista = ROLES.matcher(contenido);
        if (lista.find()) {
            Matcher item = ITEM.matcher(lista.group(1));
            while (item.find()) {
                roles.add(normalizar(item.group(1)));
            }
        }
        Matcher unico = ROL.matcher(contenido);
        if (roles.isEmpty() && unico.find()) {
            for (String rol : unico.group(1).split("[,\\s]+")) {
                roles.add(normalizar(rol));
            }
        }
        return roles;
    }

    private String normalizar(String rol) {
        return rol.trim().replaceFirst("^ROLE_", "");
    }

    private byte[] firmar(String datos) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            return mac.doFinal(datos.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular la firma", e);
        }
    }

    private AutenticacionException invalido() {
        return new AutenticacionException("El token de autenticacion es invalido.");
    }
}
