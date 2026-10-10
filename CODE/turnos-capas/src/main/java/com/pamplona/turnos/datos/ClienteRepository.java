package com.pamplona.turnos.datos;

import com.pamplona.turnos.dominio.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    Cliente guardar(String nombre, String contacto);

    Optional<Cliente> buscarPorId(int id);

    List<Cliente> listar();
}
