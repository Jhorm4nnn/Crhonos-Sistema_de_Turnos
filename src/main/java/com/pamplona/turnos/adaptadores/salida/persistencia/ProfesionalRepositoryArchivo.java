package com.pamplona.turnos.adaptadores.salida.persistencia;

import com.pamplona.turnos.aplicacion.puertos.salida.ProfesionalRepositoryPort;

import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.dominio.HorarioDisponibilidad;
import com.pamplona.turnos.dominio.Profesional;

import java.nio.file.Path;
import java.time.LocalTime;
import java.util.Optional;

public class ProfesionalRepositoryArchivo extends ArchivoRepositorio<Profesional> implements ProfesionalRepositoryPort {

    public ProfesionalRepositoryArchivo(Path carpeta) {
        super(carpeta, "profesionales.txt", Profesional::getId);
        cargar();
    }

    @Override
    public Optional<Profesional> buscarPorId(int id) {
        return items.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
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
