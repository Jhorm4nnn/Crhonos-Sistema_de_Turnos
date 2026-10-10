package com.pamplona.turnos.negocio;

/** Validacion de texto compartida por los servicios. */
final class Textos {

    private Textos() { }

    static String obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException(campo + " es obligatorio.");
        }
        valor = valor.trim();
        if (valor.contains(";")) {
            throw new ReglaNegocioException(campo + " no puede contener el caracter ';'.");
        }
        return valor;
    }
}
