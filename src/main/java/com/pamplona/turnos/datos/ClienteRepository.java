package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    int siguienteId();

    void guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(int id);

    Optional<Cliente> buscarPorDocumento(String documento);

    List<Cliente> listar();
}
