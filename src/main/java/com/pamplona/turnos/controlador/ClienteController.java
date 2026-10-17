package com.pamplona.turnos.controlador;

import com.pamplona.turnos.excepciones.DuplicadoException;
import com.pamplona.turnos.modelo.Cliente;
import com.pamplona.turnos.modelo.dao.ClienteDAO;
import com.pamplona.turnos.vista.ClientePanel;
import com.pamplona.turnos.vista.Mensajes;

/** HU-1: registro de clientes. */
public class ClienteController extends ControladorBase {

    private final ClientePanel vista;
    private final ClienteDAO dao;

    public ClienteController(ClientePanel vista, ClienteDAO dao) {
        this.vista = vista;
        this.dao = dao;
        vista.addRegistrarListener(e -> ejecutar(this::registrar));
        refrescar();
    }

    public void registrar() {
        String documento = vista.getDocumento().trim();
        if (dao.buscarPorDocumento(documento).isPresent()) {
            throw new DuplicadoException("Ya existe un cliente con el documento " + documento + ".");
        }
        // El constructor de Cliente valida los datos (vacios, formatos).
        Cliente nuevo = dao.crear(vista.getNombre(), documento, vista.getTelefono(), vista.getCorreo(),
                vista.getGenero());
        vista.limpiar();
        refrescar();
        Mensajes.info("Cliente registrado: " + nuevo);
    }

    public void refrescar() {
        vista.mostrarClientes(dao.listar());
    }
}
