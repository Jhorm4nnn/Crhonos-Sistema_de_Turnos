package com.pamplona.turnos.aplicacion.seguridad;

import com.pamplona.turnos.excepciones.AccesoDenegadoException;
import com.pamplona.turnos.excepciones.AutenticacionException;

import java.time.Clock;
import java.time.Instant;

/** Reglas de acceso de la aplicacion: cada caso de uso las exige, sin importar por que adaptador llegue la peticion. */
public final class Autorizacion {

    private Autorizacion() { }

    public static void exigirAutenticado(UsuarioAutenticado usuario, Clock reloj) {
        if (usuario == null) {
            throw new AutenticacionException("Debe iniciar sesion para continuar.");
        }
        if (!Instant.now(reloj).isBefore(usuario.expira())) {
            throw new AutenticacionException("Su sesion expiro. Inicie sesion de nuevo.");
        }
    }

    public static void exigirAdmin(UsuarioAutenticado usuario, Clock reloj) {
        exigirAutenticado(usuario, reloj);
        if (!usuario.esAdmin()) {
            throw new AccesoDenegadoException("No tiene permisos para esta operacion (se requiere rol ADMIN).");
        }
    }
}
