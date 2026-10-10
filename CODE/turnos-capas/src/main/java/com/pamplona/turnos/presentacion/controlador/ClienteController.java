package com.pamplona.turnos.presentacion.controlador;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.negocio.ClienteService;
import com.pamplona.turnos.presentacion.vista.ClientePanel;
import com.pamplona.turnos.presentacion.vista.Mensajes;

public class ClienteController extends ControladorBase {

    private final ClientePanel vista;
    private final ClienteService servicio;

    public ClienteController(ClientePanel vista, ClienteService servicio) {
        this.vista = vista;
        this.servicio = servicio;
        vista.addRegistrarListener(e -> ejecutar(this::registrar));
        ejecutar(this::refrescar);
    }

    public void registrar() {
        Cliente nuevo = servicio.registrar(vista.getNombre(), vista.getContacto());
        vista.limpiar();
        refrescar();
        Mensajes.info("Cliente registrado: " + nuevo);
    }

    public void refrescar() {
        vista.mostrarClientes(servicio.listar());
    }
}
