package com.pamplona.turnos.adaptadores.entrada.swing.controlador;

import com.pamplona.turnos.adaptadores.entrada.swing.SesionSwing;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.ClientePanel;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.Mensajes;
import com.pamplona.turnos.aplicacion.puertos.entrada.CasosDeUso;
import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.dominio.Cliente;

/** Adaptador de entrada (ventana de clientes): llama a los casos de uso, sin reglas de negocio. */
public class ClienteController extends ControladorBase {

    private final ClientePanel vista;
    private final CasosDeUso casos;
    private final SesionSwing sesion;

    public ClienteController(ClientePanel vista, CasosDeUso casos, SesionSwing sesion) {
        this.vista = vista;
        this.casos = casos;
        this.sesion = sesion;
        vista.addRegistrarListener(e -> ejecutar(this::registrar));
        ejecutar(this::refrescar);
    }

    public void registrar() {
        Cliente nuevo = casos.registrarCliente().ejecutar(sesion.getUsuario(), new Comandos.RegistrarCliente(
                vista.getNombre(), vista.getDocumento(), vista.getTelefono(), vista.getCorreo(), vista.getGenero()));
        vista.limpiar();
        refrescar();
        Mensajes.info("Cliente registrado: " + nuevo);
    }

    public void refrescar() {
        vista.mostrarClientes(casos.listarClientes().ejecutar(sesion.getUsuario()));
    }
}
