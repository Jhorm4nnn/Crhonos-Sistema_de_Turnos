package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Turno;

/** HU-5. Solo el administrador. */
public interface CancelarTurnoUseCase {

    Turno ejecutar(UsuarioAutenticado usuario, Integer turnoId);
}
