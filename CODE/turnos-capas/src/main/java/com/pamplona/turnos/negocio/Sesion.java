package com.pamplona.turnos.negocio;

import java.time.Clock;
import java.time.Instant;

/** Sesion del usuario autenticado. Las reglas de acceso (rol, expiracion) viven en la capa de negocio. */
public class Sesion {

    private final Clock reloj;
    private UsuarioAutenticado actual;

    public Sesion(Clock reloj) {
        this.reloj = reloj;
    }

    void iniciar(UsuarioAutenticado usuario) {
        this.actual = usuario;
    }

    public void cerrar() {
        this.actual = null;
    }

    public UsuarioAutenticado getUsuario() {
        return actual;
    }

    public void exigirAutenticado() {
        if (actual == null) {
            throw new ReglaNegocioException("Debe iniciar sesion para continuar.");
        }
        if (!Instant.now(reloj).isBefore(actual.expira())) {
            actual = null;
            throw new ReglaNegocioException("Su sesion expiro. Cierre la aplicacion e inicie sesion de nuevo.");
        }
    }

    public void exigirAdmin() {
        exigirAutenticado();
        if (!actual.esAdmin()) {
            throw new ReglaNegocioException("No tiene permisos para esta operacion (se requiere rol ADMIN).");
        }
    }
}
