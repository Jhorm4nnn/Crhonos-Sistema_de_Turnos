package com.pamplona.turnos.presentacion.vista;

import javax.swing.JOptionPane;
import java.util.function.BiConsumer;

/** Dialogos de informacion y error. La salida es reemplazable (util para pruebas). */
public final class Mensajes {

    private static BiConsumer<String, Boolean> salida = (mensaje, error) -> JOptionPane.showMessageDialog(
            null, mensaje, error ? "Error" : "Informacion",
            error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

    private Mensajes() { }

    public static void info(String mensaje) {
        salida.accept(mensaje, false);
    }

    public static void error(String mensaje) {
        salida.accept(mensaje, true);
    }

    public static void setSalida(BiConsumer<String, Boolean> nuevaSalida) {
        salida = nuevaSalida;
    }
}
