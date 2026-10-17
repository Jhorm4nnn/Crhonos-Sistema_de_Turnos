package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Profesional;

import java.util.List;

public interface ListarProfesionalesUseCase {

    List<Profesional> ejecutar(UsuarioAutenticado usuario);
}
