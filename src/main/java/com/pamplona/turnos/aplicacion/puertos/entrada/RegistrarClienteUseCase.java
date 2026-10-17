package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Cliente;

/** HU-1. Solo el administrador. */
public interface RegistrarClienteUseCase {

    Cliente ejecutar(UsuarioAutenticado usuario, Comandos.RegistrarCliente comando);
}
