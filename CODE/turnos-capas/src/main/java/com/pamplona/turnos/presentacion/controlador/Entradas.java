package com.pamplona.turnos.presentacion.controlador;

import com.pamplona.turnos.negocio.ReglaNegocioException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Convierte el texto de los formularios en tipos Java. No contiene reglas de negocio. */
final class Entradas {

    private Entradas() { }

    static LocalDate fecha(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException(campo + " es obligatoria.");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException(campo + " es invalida. Use el formato AAAA-MM-DD (ej. 2026-10-15).");
        }
    }

    static LocalDate fechaOpcional(String texto, String campo) {
        return (texto == null || texto.isBlank()) ? null : fecha(texto, campo);
    }

    static LocalTime hora(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ReglaNegocioException(campo + " es obligatoria.");
        }
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new ReglaNegocioException(campo + " es invalida. Use el formato HH:mm (ej. 14:30).");
        }
    }
}
