package com.pamplona.turnos.presentacion.vista;

import com.pamplona.turnos.negocio.UsuarioAutenticado;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;

/** Ventana principal. El rol ADMIN ve todas las pestanas; los demas roles solo la consulta. */
public class MainFrame extends JFrame {

    private final ClientePanel clientePanel = new ClientePanel();
    private final ProfesionalPanel profesionalPanel = new ProfesionalPanel();
    private final TurnoPanel turnoPanel = new TurnoPanel();
    private final ConsultaPanel consultaPanel = new ConsultaPanel();
    private final JTabbedPane pestanas = new JTabbedPane();

    public MainFrame(UsuarioAutenticado usuario) {
        super("Sistema de Generacion de Turnos - " + usuario.usuario() + " (" + usuario.rolesTexto() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        if (usuario.esAdmin()) {
            pestanas.addTab("Clientes", clientePanel);
            pestanas.addTab("Profesionales", profesionalPanel);
            pestanas.addTab("Turnos", turnoPanel);
        }
        pestanas.addTab("Consulta", consultaPanel);
        add(pestanas);

        setSize(900, 560);
        setLocationRelativeTo(null);
    }

    public ClientePanel getClientePanel() { return clientePanel; }
    public ProfesionalPanel getProfesionalPanel() { return profesionalPanel; }
    public TurnoPanel getTurnoPanel() { return turnoPanel; }
    public ConsultaPanel getConsultaPanel() { return consultaPanel; }

    public void addCambioPestanaListener(ChangeListener l) { pestanas.addChangeListener(l); }
    public void seleccionarPestana(int indice) { pestanas.setSelectedIndex(indice); }
    public int cantidadPestanas() { return pestanas.getTabCount(); }
}
