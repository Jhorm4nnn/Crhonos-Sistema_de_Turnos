package com.pamplona.turnos.modelo.dao;

import com.pamplona.turnos.modelo.Cliente;
import com.pamplona.turnos.modelo.Profesional;
import com.pamplona.turnos.modelo.Turno;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class TurnoDAO extends ArchivoDAO<Turno> {

    private final ClienteDAO clientes;
    private final ProfesionalDAO profesionales;

    public TurnoDAO(Path carpeta, ClienteDAO clientes, ProfesionalDAO profesionales) {
        super(carpeta, "turnos.txt");
        this.clientes = clientes;
        this.profesionales = profesionales;
        cargar();
    }

    public Turno crear(Cliente cliente, Profesional profesional, LocalDate fecha, LocalTime hora) {
        Turno t = new Turno(siguienteId(Turno::getId), cliente, profesional, fecha, hora);
        items.add(t);
        persistir();
        return t;
    }

    /** Guarda los cambios hechos a un turno existente (cancelar / reprogramar). */
    public void actualizar(Turno turno) {
        persistir();
    }

    public Optional<Turno> buscarPorId(int id) {
        return items.stream().filter(t -> t.getId() == id).findFirst();
    }

    @Override
    protected String aLinea(Turno t) {
        return String.join(";", String.valueOf(t.getId()), String.valueOf(t.getCliente().getId()),
                String.valueOf(t.getProfesional().getId()), t.getFecha().toString(), t.getHora().toString(),
                t.getEstado().name());
    }

    @Override
    protected Turno desdeLinea(String linea) {
        String[] p = linea.split(";", -1);
        Cliente c = clientes.buscarPorId(Integer.parseInt(p[1])).orElseThrow();
        Profesional pr = profesionales.buscarPorId(Integer.parseInt(p[2])).orElseThrow();
        Turno t = new Turno(Integer.parseInt(p[0]), c, pr, LocalDate.parse(p[3]), LocalTime.parse(p[4]));
        if (Turno.Estado.valueOf(p[5]) == Turno.Estado.CANCELADO) {
            t.cancelar();
        }
        return t;
    }
}
