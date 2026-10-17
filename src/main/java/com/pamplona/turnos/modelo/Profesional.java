package com.pamplona.turnos.modelo;

/** HU-2: el profesional es un actor externo; el sistema guarda sus datos y su horario. */
public class Profesional extends Persona {

    private final HorarioDisponibilidad horario;

    public Profesional(int id, String nombreCompleto, String documento, String telefono, String correo,
                       Genero genero, HorarioDisponibilidad horario) {
        super(id, nombreCompleto, documento, telefono, correo, genero);
        if (horario == null) {
            throw new com.pamplona.turnos.excepciones.DatosInvalidosException("El horario de disponibilidad es obligatorio.");
        }
        this.horario = horario;
    }

    public HorarioDisponibilidad getHorario() { return horario; }

    @Override
    public String rol() {
        return "Profesional";
    }

    @Override
    public String toString() {
        return super.toString() + " | horario " + horario;
    }
}
