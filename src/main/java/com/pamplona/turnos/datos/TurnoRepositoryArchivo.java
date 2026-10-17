package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class TurnoRepositoryArchivo extends ArchivoRepositorio<Turno> implements TurnoRepository {

    private final ClienteRepository clientes;
    private final ProfesionalRepository profesionales;

    public TurnoRepositoryArchivo(Path carpeta, ClienteRepository clientes, ProfesionalRepository profesionales) {
        super(carpeta, "turnos.txt", Turno::getId);
        this.clientes = clientes;
        this.profesionales = profesionales;
        cargar();
    }

    @Override
    public void actualizar(Turno turno) {
        persistir();
    }

    @Override
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
