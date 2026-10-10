package com.pamplona.turnos.negocio;

import com.pamplona.turnos.datos.ClienteRepository;
import com.pamplona.turnos.dominio.Cliente;

import java.util.List;

/** HU-1 / RF-1: clientes. */
public class ClienteService {

    private final ClienteRepository repo;
    private final Sesion sesion;

    public ClienteService(ClienteRepository repo, Sesion sesion) {
        this.repo = repo;
        this.sesion = sesion;
    }

    public Cliente registrar(String nombre, String contacto) {
        sesion.exigirAdmin();
        nombre = Textos.obligatorio(nombre, "El nombre");
        contacto = Textos.obligatorio(contacto, "El contacto");
        for (Cliente c : repo.listar()) {
            if (c.getNombre().equalsIgnoreCase(nombre) && c.getContacto().equalsIgnoreCase(contacto)) {
                throw new ReglaNegocioException("Ya existe un cliente con ese nombre y contacto.");
            }
        }
        return repo.guardar(nombre, contacto);
    }

    public List<Cliente> listar() {
        sesion.exigirAutenticado();
        return repo.listar();
    }
}
