package com.pamplona.turnos.presentacion.vista;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Profesional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

/** Vista de consulta de turnos con filtros combinables (HU-5). */
public class ConsultaPanel extends JPanel {

    private final JComboBox<Profesional> cmbProfesional = new JComboBox<>();
    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private final JTextField txtDesde = new JTextField(9);
    private final JTextField txtHasta = new JTextField(9);
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnLimpiar = new JButton("Limpiar filtros");
    private final JTable tabla = Componentes.tabla("Id", "Cliente", "Profesional", "Fecha", "Horario", "Estado");

    public ConsultaPanel() {
        super(new BorderLayout(10, 10));
        setBorder(Componentes.margen());
        Componentes.rendererTodos(cmbProfesional);
        Componentes.rendererTodos(cmbCliente);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros (opcionales y combinables)"));
        filtros.add(new JLabel("Profesional:"));
        filtros.add(cmbProfesional);
        filtros.add(new JLabel("Cliente:"));
        filtros.add(cmbCliente);
        filtros.add(new JLabel("Desde:"));
        filtros.add(txtDesde);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(txtHasta);
        filtros.add(btnBuscar);
        filtros.add(btnLimpiar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public Profesional getProfesionalSeleccionado() { return (Profesional) cmbProfesional.getSelectedItem(); }
    public Cliente getClienteSeleccionado() { return (Cliente) cmbCliente.getSelectedItem(); }
    public String getDesde() { return txtDesde.getText(); }
    public String getHasta() { return txtHasta.getText(); }

    public void setClientes(List<Cliente> lista) { Componentes.recargar(cmbCliente, lista, true); }
    public void setProfesionales(List<Profesional> lista) { Componentes.recargar(cmbProfesional, lista, true); }
    public void mostrarTurnos(List<Object[]> filas) { Componentes.llenar(tabla, filas); }

    public void limpiarFiltros() {
        cmbProfesional.setSelectedIndex(0);
        cmbCliente.setSelectedIndex(0);
        txtDesde.setText("");
        txtHasta.setText("");
    }

    public void addBuscarListener(ActionListener l) { btnBuscar.addActionListener(l); }
    public void addLimpiarListener(ActionListener l) { btnLimpiar.addActionListener(l); }

    // Acceso para pruebas
    public void setRango(String desde, String hasta) { txtDesde.setText(desde); txtHasta.setText(hasta); }
    public JTable getTabla() { return tabla; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JComboBox<Profesional> getCmbProfesional() { return cmbProfesional; }
    public JComboBox<Cliente> getCmbCliente() { return cmbCliente; }
}
