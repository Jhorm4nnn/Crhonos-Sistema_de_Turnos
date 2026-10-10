package com.pamplona.turnos.datos;



import com.pamplona.turnos.dominio.Profesional;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfesionalRepositoryArchivo implements ProfesionalRepository {

    private final Path archivo;
    private final List<Profesional> profesionales = new ArrayList<>();

    public ProfesionalRepositoryArchivo(Path carpetaDatos) {
        this.archivo = carpetaDatos.resolve("profesionales.txt");
        for (String linea : ArchivoUtil.leer(archivo)) {
            String[] p = linea.split(";", -1);
            profesionales.add(new Profesional(Integer.parseInt(p[0]), p[1],
                    LocalTime.parse(p[2]), LocalTime.parse(p[3])));
        }
    }

    @Override
    public Profesional guardar(String nombre, LocalTime inicio, LocalTime fin) {
        int id = profesionales.stream().mapToInt(Profesional::getId).max().orElse(0) + 1;
        Profesional p = new Profesional(id, nombre, inicio, fin);
        profesionales.add(p);
        persistir();
        return p;
    }

    @Override
    public Optional<Profesional> buscarPorId(int id) {
        return profesionales.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Profesional> listar() {
        return new ArrayList<>(profesionales);
    }

    private void persistir() {
        List<String> lineas = new ArrayList<>();
        for (Profesional p : profesionales) {
            lineas.add(p.getId() + ";" + p.getNombre() + ";" + p.getHoraInicio() + ";" + p.getHoraFin());
        }
        ArchivoUtil.escribir(archivo, lineas);
    }
}
