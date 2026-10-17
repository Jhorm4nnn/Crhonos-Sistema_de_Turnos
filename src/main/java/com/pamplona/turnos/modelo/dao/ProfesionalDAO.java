package com.pamplona.turnos.modelo.dao;

import com.pamplona.turnos.modelo.Genero;
import com.pamplona.turnos.modelo.HorarioDisponibilidad;
import com.pamplona.turnos.modelo.Profesional;

import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Optional;

public class ProfesionalDAO extends ArchivoDAO<Profesional> {

    public ProfesionalDAO(Path carpeta) {
        super(carpeta, "profesionales.txt");
        cargar();
    }

    public Profesional crear(String nombre, String documento, String telefono, String correo, Genero genero,
                             HorarioDisponibilidad horario) {
        Profesional p = new Profesional(siguienteId(Profesional::getId), nombre, documento, telefono, correo,
                genero, horario);
        items.add(p);
        persistir();
        return p;
    }

    public Optional<Profesional> buscarPorId(int id) {
        return items.stream().filter(p -> p.getId() == id).findFirst();
    }

    public Optional<Profesional> buscarPorDocumento(String documento) {
        return items.stream().filter(p -> p.getDocumento().equals(documento)).findFirst();
    }

    @Override
    protected String aLinea(Profesional p) {
        return String.join(";", String.valueOf(p.getId()), p.getNombreCompleto(), p.getDocumento(),
                p.getTelefono(), p.getCorreo(), p.getGenero().name(),
                p.getHorario().getInicio().toString(), p.getHorario().getFin().toString());
    }

    @Override
    protected Profesional desdeLinea(String linea) {
        String[] p = linea.split(";", -1);
        return new Profesional(Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], Genero.valueOf(p[5]),
                new HorarioDisponibilidad(LocalTime.parse(p[6]), LocalTime.parse(p[7])));
    }
}
