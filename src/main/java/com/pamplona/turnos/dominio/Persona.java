package com.pamplona.turnos.dominio;

import com.pamplona.turnos.excepciones.DatosInvalidosException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Clase base ABSTRACTA de las personas del dominio (cliente y profesional).
 * Encapsulamiento: atributos privados, sin setters, validados al construir.
 */
public abstract class Persona {

    private static final Pattern DOCUMENTO = Pattern.compile("\\d{5,15}");
    private static final Pattern TELEFONO = Pattern.compile("\\+?\\d{7,15}");
    private static final Pattern CORREO = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");

    private final int id;
    private final String nombreCompleto;
    private final String documento;
    private final String telefono;
    private final String correo;
    private final Genero genero;

    protected Persona(int id, String nombreCompleto, String documento, String telefono, String correo, Genero genero) {
        this.id = id;
        this.nombreCompleto = validarNombre(nombreCompleto);
        this.documento = validarFormato(documento, DOCUMENTO, "El documento", "solo numeros (5 a 15 digitos)");
        this.telefono = validarFormato(quitarEspacios(telefono), TELEFONO, "El telefono", "solo numeros (7 a 15 digitos)");
        this.correo = validarFormato(correo, CORREO, "El correo", "un correo valido, por ejemplo nombre@dominio.com");
        if (genero == null) {
            throw new DatosInvalidosException("El genero es obligatorio.");
        }
        this.genero = genero;
    }

    /** Metodo abstracto: cada subclase dice que rol cumple (polimorfismo). */
    public abstract String rol();

    public int getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    public Genero getGenero() { return genero; }

    private static String validarNombre(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException("El nombre completo es obligatorio.");
        }
        String limpio = valor.trim().replaceAll("\\s+", " ");
        if (limpio.contains(";")) {
            throw new DatosInvalidosException("El nombre no puede contener el caracter ';'.");
        }
        if (limpio.split(" ").length < 2) {
            throw new DatosInvalidosException("Ingrese el nombre completo (nombre y apellido).");
        }
        return limpio;
    }

    private static String validarFormato(String valor, Pattern patron, String campo, String formato) {
        if (valor == null || valor.isBlank()) {
            throw new DatosInvalidosException(campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (!patron.matcher(limpio).matches()) {
            throw new DatosInvalidosException(campo + " es invalido: debe contener " + formato + ".");
        }
        return limpio;
    }

    private static String quitarEspacios(String valor) {
        return valor == null ? null : valor.replace(" ", "");
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        return documento.equals(((Persona) otro).documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), documento);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nombreCompleto + " | doc " + documento + " | tel " + telefono
                + " | " + correo + " | " + genero;
    }
}
