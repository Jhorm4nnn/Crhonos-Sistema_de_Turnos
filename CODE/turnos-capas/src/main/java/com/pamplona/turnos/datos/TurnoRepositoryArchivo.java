package com.pamplona.turnos.datos;



import com.pamplona.turnos.dominio.Turno;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TurnoRepositoryArchivo implements TurnoRepository {

    private final Path archivo;
    private final List<Turno> turnos = new ArrayList<>();

    public TurnoRepositoryArchivo(Path carpetaDatos) {
        this.archivo = carpetaDatos.resolve("turnos.txt");
        for (String linea : ArchivoUtil.leer(archivo)) {
            String[] p = linea.split(";", -1);
            turnos.add(new Turno(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                    LocalDate.parse(p[3]), LocalTime.parse(p[4]), Turno.Estado.valueOf(p[5])));
        }
    }

    @Override
    public Turno guardar(int clienteId, int profesionalId, LocalDate fecha, LocalTime hora) {
        int id = turnos.stream().mapToInt(Turno::getId).max().orElse(0) + 1;
        Turno t = new Turno(id, clienteId, profesionalId, fecha, hora, Turno.Estado.AGENDADO);
        turnos.add(t);
        persistir();
        return t;
    }

    /** Persiste los cambios hechos a un turno existente (cancelar / reprogramar). */
    @Override
    public void actualizar(Turno turno) {
        persistir();
    }

    @Override
    public Optional<Turno> buscarPorId(int id) {
        return turnos.stream().filter(t -> t.getId() == id).findFirst();
    }

    @Override
    public List<Turno> listar() {
        return new ArrayList<>(turnos);
    }

    private void persistir() {
        List<String> lineas = new ArrayList<>();
        for (Turno t : turnos) {
            lineas.add(t.getId() + ";" + t.getClienteId() + ";" + t.getProfesionalId() + ";"
                    + t.getFecha() + ";" + t.getHora() + ";" + t.getEstado());
        }
        ArchivoUtil.escribir(archivo, lineas);
    }
}
