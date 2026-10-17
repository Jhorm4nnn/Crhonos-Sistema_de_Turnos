package com.pamplona.turnos.adaptadores.entrada.swing;

import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;

/** Guarda quien inicio sesion en la ventana, para pasarlo a cada caso de uso. */
public class SesionSwing {

    private UsuarioAutenticado usuario;

    public UsuarioAutenticado getUsuario() { return usuario; }
    public void setUsuario(UsuarioAutenticado usuario) { this.usuario = usuario; }
}
