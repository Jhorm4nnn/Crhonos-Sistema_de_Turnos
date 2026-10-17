package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Genero;
import com.pamplona.turnos.excepciones.DuplicadoException;

import java.util.List;

/** HU-1: clientes. */
public class ClienteService {

    private final ClienteRepository repo;
    private final Sesion sesion;

    public ClienteService(ClienteRepository repo, Sesion sesion) {
        this.repo = repo;
        this.sesion = sesion;
    }

    public Cliente registrar(String nombre, String documento, String telefono, String correo, Genero genero) {
        sesion.exigirAdmin();
        // El constructor de Cliente valida los datos (vacios y formatos).
        Cliente nuevo = new Cliente(repo.siguienteId(), nombre, documento, telefono, correo, genero);
        if (repo.buscarPorDocumento(nuevo.getDocumento()).isPresent()) {
            throw new DuplicadoException("Ya existe un cliente con el documento " + nuevo.getDocumento() + ".");
        }
        repo.guardar(nuevo);
        return nuevo;
    }

    public List<Cliente> listar() {
        sesion.exigirAutenticado();
        return repo.listar();
    }
}
