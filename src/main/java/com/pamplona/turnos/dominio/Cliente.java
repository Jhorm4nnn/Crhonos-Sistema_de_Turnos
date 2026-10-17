package com.pamplona.turnos.dominio;

/** HU-1: el cliente es un actor externo; el sistema solo guarda sus datos. */
public class Cliente extends Persona {

    public Cliente(int id, String nombreCompleto, String documento, String telefono, String correo, Genero genero) {
        super(id, nombreCompleto, documento, telefono, correo, genero);
    }

    @Override
    public String rol() {
        return "Cliente";
    }
}
