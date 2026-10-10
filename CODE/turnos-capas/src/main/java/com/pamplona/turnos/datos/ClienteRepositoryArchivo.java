package com.pamplona.turnos.datos;



import com.pamplona.turnos.dominio.Cliente;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryArchivo implements ClienteRepository {

    private final Path archivo;
    private final List<Cliente> clientes = new ArrayList<>();

    public ClienteRepositoryArchivo(Path carpetaDatos) {
        this.archivo = carpetaDatos.resolve("clientes.txt");
        for (String linea : ArchivoUtil.leer(archivo)) {
            String[] p = linea.split(";", -1);
            clientes.add(new Cliente(Integer.parseInt(p[0]), p[1], p[2]));
        }
    }

    @Override
    public Cliente guardar(String nombre, String contacto) {
        int id = clientes.stream().mapToInt(Cliente::getId).max().orElse(0) + 1;
        Cliente c = new Cliente(id, nombre, contacto);
        clientes.add(c);
        persistir();
        return c;
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return clientes.stream().filter(c -> c.getId() == id).findFirst();
    }

    @Override
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    private void persistir() {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) {
            lineas.add(c.getId() + ";" + c.getNombre() + ";" + c.getContacto());
        }
        ArchivoUtil.escribir(archivo, lineas);
    }
}
