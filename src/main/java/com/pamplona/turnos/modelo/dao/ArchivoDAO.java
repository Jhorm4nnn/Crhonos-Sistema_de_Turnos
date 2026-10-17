package com.pamplona.turnos.modelo.dao;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase base de los DAO: guarda una lista de objetos en un archivo de texto (una linea por objeto).
 * Los DAO hijos solo dicen como convertir un objeto a linea y una linea a objeto.
 */
abstract class ArchivoDAO<T> {

    protected final List<T> items = new ArrayList<>();
    private final Path archivo;

    protected ArchivoDAO(Path carpeta, String nombreArchivo) {
        this.archivo = carpeta.resolve(nombreArchivo);
    }

    protected abstract String aLinea(T item);

    protected abstract T desdeLinea(String linea);

    /** Los hijos la llaman al final de su constructor, cuando ya tienen listos sus campos. */
    protected void cargar() {
        try {
            if (Files.exists(archivo)) {
                for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                    if (!linea.isBlank()) {
                        items.add(desdeLinea(linea));
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        }
    }

    protected void persistir() {
        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
            List<String> lineas = new ArrayList<>();
            for (T item : items) {
                lineas.add(aLinea(item));
            }
            Files.write(archivo, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo escribir " + archivo, e);
        }
    }

    protected int siguienteId(java.util.function.ToIntFunction<T> id) {
        return items.stream().mapToInt(id).max().orElse(0) + 1;
    }

    public List<T> listar() {
        return new ArrayList<>(items);
    }
}
