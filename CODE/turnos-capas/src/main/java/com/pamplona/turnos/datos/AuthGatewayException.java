package com.pamplona.turnos.datos;

/** Fallo al hablar con el servicio de autenticacion. */
public class AuthGatewayException extends RuntimeException {

    public enum Tipo { CREDENCIALES_INVALIDAS, SERVICIO_NO_DISPONIBLE, RESPUESTA_INVALIDA }

    private final Tipo tipo;

    public AuthGatewayException(Tipo tipo, String mensaje) {
        super(mensaje);
        this.tipo = tipo;
    }

    public Tipo getTipo() {
        return tipo;
    }
}
