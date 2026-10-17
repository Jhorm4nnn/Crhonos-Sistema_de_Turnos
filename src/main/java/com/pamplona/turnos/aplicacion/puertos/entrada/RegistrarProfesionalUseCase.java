package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Profesional;

/** HU-2. Solo el administrador. */
public interface RegistrarProfesionalUseCase {

    Profesional ejecutar(UsuarioAutenticado usuario, Comandos.RegistrarProfesional comando);
}
