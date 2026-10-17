package com.pamplona.turnos.adaptadores.entrada.swing.controlador;

import com.pamplona.turnos.excepciones.DatosInvalidosException;
import com.pamplona.turnos.excepciones.TurnosException;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.Mensajes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Ejecuta una accion y convierte los errores en mensajes claros (HU-8); convierte texto en fechas y horas. */
abstract class ControladorBase {

    protected void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (TurnosException e) {
            Mensajes.error(e.getMessage());
        } catch (RuntimeException e) {
            // No se exponen detalles tecnicos al usuario.
            Mensajes.error("Ocurrio un problema inesperado. Intente de nuevo.");
        }
    }

    protected LocalDate fecha(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new DatosInvalidosException(campo + " es obligatoria.");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException(campo + " es invalida. Use el formato AAAA-MM-DD (ej. 2026-10-25).");
        }
    }

    protected LocalDate fechaOpcional(String texto, String campo) {
        return (texto == null || texto.isBlank()) ? null : fecha(texto, campo);
    }

    protected LocalTime hora(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new DatosInvalidosException(campo + " es obligatoria.");
        }
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException(campo + " es invalida. Use el formato HH:mm (ej. 14:30).");
        }
    }
}
