package com.pamplona.turnos.aplicacion.seguridad;

import java.time.Instant;
import java.util.List;

/** Quien esta usando el sistema: sus datos salen del JWT que emitio el authcore. */
public record UsuarioAutenticado(String usuario, List<String> roles, Instant expira, String token) {

    public static final String ROL_ADMIN = "ADMIN";

    public boolean esAdmin() {
        return roles.contains(ROL_ADMIN);
    }

    public String rolesTexto() {
        return roles.isEmpty() ? "sin rol" : String.join(", ", roles);
    }
}
