package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Cliente;

import java.util.List;

public interface ListarClientesUseCase {

    List<Cliente> ejecutar(UsuarioAutenticado usuario);
}
