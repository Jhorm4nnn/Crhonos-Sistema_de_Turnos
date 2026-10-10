package com.pamplona.turnos.negocio;

/** Error de regla de negocio con un mensaje claro para el usuario (RNF-3 / HU-8). */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
