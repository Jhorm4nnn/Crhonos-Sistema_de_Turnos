package com.pamplona.turnos.negocio;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lee los claims del JWT del authcore (sub, roles, exp).
 * No valida la firma: el token llega directo del authcore por HTTP; la validacion de firma
 * pasara al domain-service cuando exista su API REST (fase hexagonal).
 */
final class TokenJwt {

    private static final Pattern SUB = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EXP = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");
    private static final Pattern ROLES = Pattern.compile("\"roles\"\\s*:\\s*\\[(.*?)\\]");
    private static final Pattern ITEM = Pattern.compile("\"([^\"]+)\"");

    private TokenJwt() { }

    static UsuarioAutenticado leer(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                throw new IllegalArgumentException("formato");
            }
            String json = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);

            Matcher sub = SUB.matcher(json);
            Matcher exp = EXP.matcher(json);
            if (!sub.find() || !exp.find()) {
                throw new IllegalArgumentException("claims");
            }
            List<String> roles = new ArrayList<>();
            Matcher r = ROLES.matcher(json);
            if (r.find()) {
                Matcher item = ITEM.matcher(r.group(1));
                while (item.find()) {
                    roles.add(item.group(1).replaceFirst("^ROLE_", ""));
                }
            }
            return new UsuarioAutenticado(sub.group(1), roles, Instant.ofEpochSecond(Long.parseLong(exp.group(1))), token);
        } catch (IllegalArgumentException e) {
            throw new ReglaNegocioException("El servicio de autenticacion devolvio un token que no se puede interpretar.");
        }
    }
}
