package com.pamplona.turnos.aplicacion.puertos.salida;

import com.pamplona.turnos.dominio.Cliente;

import java.util.List;
import java.util.Optional;

/** Lo que la aplicacion necesita para guardar clientes. Quien lo implemente (archivo, MySQL...) es un adaptador. */
public interface ClienteRepositoryPort {

    int siguienteId();

    void guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(int id);

    Optional<Cliente> buscarPorDocumento(String documento);

    List<Cliente> listar();
}
