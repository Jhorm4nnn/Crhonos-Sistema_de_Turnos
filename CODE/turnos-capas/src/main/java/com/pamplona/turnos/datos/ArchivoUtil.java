package com.pamplona.turnos.datos;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Utilidad de la capa de datos: lee y escribe lineas de texto (una por registro). */
final class ArchivoUtil {

    private ArchivoUtil() { }

    static List<String> leer(Path archivo) {
        try {
            if (!Files.exists(archivo)) {
                return new ArrayList<>();
            }
            List<String> lineas = new ArrayList<>();
            for (String l : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                if (!l.isBlank()) {
                    lineas.add(l);
                }
            }
            return lineas;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        }
    }

    static void escribir(Path archivo, List<String> lineas) {
        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + archivo, e);
        }
    }
}
