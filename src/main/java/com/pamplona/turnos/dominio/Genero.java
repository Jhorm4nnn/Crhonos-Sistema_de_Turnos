package com.pamplona.turnos.dominio;

import com.pamplona.turnos.excepciones.DatosInvalidosException;

public enum Genero {
    MASCULINO("M"), FEMENINO("F"), OTRO("O");

    private final String codigo;

    Genero(String codigo) {
        this.codigo = codigo;
    }

    /** Acepta M, F, O o el nombre completo. */
    public static Genero desde(String texto) {
        if (texto != null) {
            String t = texto.trim();
            for (Genero g : values()) {
                if (g.codigo.equalsIgnoreCase(t) || g.name().equalsIgnoreCase(t)) {
                    return g;
                }
            }
        }
        throw new DatosInvalidosException("El genero es invalido. Use M, F u O.");
    }
}
