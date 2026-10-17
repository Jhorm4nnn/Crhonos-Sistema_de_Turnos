package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Turno;

/** HU-3 y HU-4. Solo el administrador. */
public interface CrearTurnoUseCase {

    Turno ejecutar(UsuarioAutenticado usuario, Comandos.CrearTurno comando);
}
