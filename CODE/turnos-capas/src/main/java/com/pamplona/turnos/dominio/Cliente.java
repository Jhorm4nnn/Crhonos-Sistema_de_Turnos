package com.pamplona.turnos.dominio;

public class Cliente {

    private final int id;
    private final String nombre;
    private final String contacto;

    public Cliente(int id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " - " + contacto;
    }
}
