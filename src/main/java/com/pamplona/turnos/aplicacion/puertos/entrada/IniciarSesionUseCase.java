package com.pamplona.turnos.aplicacion.puertos.entrada;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;

/** Inicia sesion contra el authcore (lo usan los adaptadores que tienen pantalla: consola y ventanas). */
public interface IniciarSesionUseCase {

    UsuarioAutenticado ejecutar(String usuario, String clave);
}
