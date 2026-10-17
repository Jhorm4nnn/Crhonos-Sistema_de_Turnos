package com.pamplona.turnos.adaptadores.salida.persistencia;

import com.pamplona.turnos.aplicacion.puertos.salida.ClienteRepositoryPort;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Genero;

import java.nio.file.Path;
import java.util.Optional;

public class ClienteRepositoryArchivo extends ArchivoRepositorio<Cliente> implements ClienteRepositoryPort {

    public ClienteRepositoryArchivo(Path carpeta) {
        super(carpeta, "clientes.txt", Cliente::getId);
        cargar();
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return items.stream().filter(c -> c.getId() == id).findFirst();
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        return items.stream().filter(c -> c.getDocumento().equals(documento)).findFirst();
    }

    @Override
    protected String aLinea(Cliente c) {
        return String.join(";", String.valueOf(c.getId()), c.getNombreCompleto(), c.getDocumento(),
                c.getTelefono(), c.getCorreo(), c.getGenero().name());
    }

    @Override
    protected Cliente desdeLinea(String linea) {
        String[] p = linea.split(";", -1);
        return new Cliente(Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], Genero.valueOf(p[5]));
    }
}
