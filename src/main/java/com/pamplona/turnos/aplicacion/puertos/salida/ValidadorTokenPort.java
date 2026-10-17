package com.pamplona.turnos.aplicacion.puertos.salida;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;

/** Comprueba que un token es autentico (firma) y vigente, y devuelve quien es. */
public interface ValidadorTokenPort {

    /**
     * Comprueba la FIRMA, el algoritmo y la vigencia. Se usa cuando el token llega de afuera (por ejemplo a la API).
     * @throws com.pamplona.turnos.excepciones.AutenticacionException si el token es invalido o expiro.
     */
    UsuarioAutenticado validar(String token);

    /**
     * Solo lee el contenido (usuario, roles, vigencia) SIN comprobar la firma. Se usa justo despues de pedirle
     * el token al authcore por una conexion directa, donde el token no pasa por manos de terceros.
     * @throws com.pamplona.turnos.excepciones.AutenticacionException si el token no se puede leer o expiro.
     */
    UsuarioAutenticado leer(String token);
}
