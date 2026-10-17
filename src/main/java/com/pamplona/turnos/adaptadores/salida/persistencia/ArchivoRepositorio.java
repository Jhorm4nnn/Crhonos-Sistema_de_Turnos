package com.pamplona.turnos.adaptadores.salida.persistencia;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Clase base de los repositorios en archivo: guarda una lista de objetos en un archivo de texto
 * (una linea por objeto). Los hijos solo dicen como convertir un objeto a linea y una linea a objeto.
 */
abstract class ArchivoRepositorio<T> {

    protected final List<T> items = new ArrayList<>();
    private final Path archivo;
    private final ToIntFunction<T> idDe;

    protected ArchivoRepositorio(Path carpeta, String nombreArchivo, ToIntFunction<T> idDe) {
        this.archivo = carpeta.resolve(nombreArchivo);
        this.idDe = idDe;
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

    public int siguienteId() {
        return items.stream().mapToInt(idDe).max().orElse(0) + 1;
    }

    public void guardar(T item) {
        items.add(item);
        persistir();
    }

    public List<T> listar() {
        return new ArrayList<>(items);
    }
}
