package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Turno;

import java.util.List;

/** HU-6. Cualquier usuario autenticado. */
public interface ConsultarTurnosUseCase {

    List<Turno> ejecutar(UsuarioAutenticado usuario, Comandos.FiltroTurnos filtro);
}
