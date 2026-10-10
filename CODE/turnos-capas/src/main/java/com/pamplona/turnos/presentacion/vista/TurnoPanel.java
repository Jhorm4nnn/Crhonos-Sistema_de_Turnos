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
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;

/** Vista para crear, cancelar y reprogramar turnos (HU-2, HU-3, HU-4). */
public class TurnoPanel extends JPanel {

    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private final JComboBox<Profesional> cmbProfesional = new JComboBox<>();
    private final JTextField txtFecha = new JTextField();
    private final JTextField txtHora = new JTextField();
    private final JButton btnCrear = new JButton("Crear turno");

    private final JTextField txtNuevaFecha = new JTextField(10);
    private final JTextField txtNuevaHora = new JTextField(6);
    private final JButton btnReprogramar = new JButton("Reprogramar seleccionado");
    private final JButton btnCancelar = new JButton("Cancelar seleccionado");

    private final JTable tabla = Componentes.tabla("Id", "Cliente", "Profesional", "Fecha", "Horario", "Estado");

    public TurnoPanel() {
        super(new BorderLayout(10, 10));
        setBorder(Componentes.margen());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo turno (duracion 30 min)"));
        form.add(new JLabel("Cliente:"));
        form.add(cmbCliente);
        form.add(new JLabel("Profesional:"));
        form.add(cmbProfesional);
        form.add(new JLabel("Fecha (AAAA-MM-DD):"));
        form.add(txtFecha);
        form.add(new JLabel("Hora (HH:mm):"));
        form.add(txtHora);
        form.add(new JLabel());
        form.add(btnCrear);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acciones.setBorder(BorderFactory.createTitledBorder("Turno seleccionado en la tabla"));
        acciones.add(btnCancelar);
        acciones.add(new JLabel("Nueva fecha:"));
        acciones.add(txtNuevaFecha);
        acciones.add(new JLabel("Nueva hora:"));
        acciones.add(txtNuevaHora);
        acciones.add(btnReprogramar);

        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        centro.add(acciones, BorderLayout.SOUTH);

        add(form, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    public Cliente getClienteSeleccionado() { return (Cliente) cmbCliente.getSelectedItem(); }
    public Profesional getProfesionalSeleccionado() { return (Profesional) cmbProfesional.getSelectedItem(); }
    public String getFecha() { return txtFecha.getText(); }
    public String getHora() { return txtHora.getText(); }
    public String getNuevaFecha() { return txtNuevaFecha.getText(); }
    public String getNuevaHora() { return txtNuevaHora.getText(); }

    /** Id del turno seleccionado en la tabla, o null si no hay seleccion. */
    public Integer getTurnoSeleccionadoId() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : (Integer) tabla.getValueAt(fila, 0);
    }

    public void setClientes(List<Cliente> lista) { Componentes.recargar(cmbCliente, lista, false); }
    public void setProfesionales(List<Profesional> lista) { Componentes.recargar(cmbProfesional, lista, false); }
    public void mostrarTurnos(List<Object[]> filas) { Componentes.llenar(tabla, filas); }

    public void limpiarFormulario() {
        txtFecha.setText("");
        txtHora.setText("");
    }

    public void limpiarReprogramacion() {
        txtNuevaFecha.setText("");
        txtNuevaHora.setText("");
    }

    public void addCrearListener(ActionListener l) { btnCrear.addActionListener(l); }
    public void addCancelarListener(ActionListener l) { btnCancelar.addActionListener(l); }
    public void addReprogramarListener(ActionListener l) { btnReprogramar.addActionListener(l); }

    // Acceso para pruebas
    public void setFechaHora(String fecha, String hora) { txtFecha.setText(fecha); txtHora.setText(hora); }
    public void setNuevaFechaHora(String fecha, String hora) { txtNuevaFecha.setText(fecha); txtNuevaHora.setText(hora); }
    public JTable getTabla() { return tabla; }
    public JButton getBtnCrear() { return btnCrear; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getBtnReprogramar() { return btnReprogramar; }
    public JComboBox<Cliente> getCmbCliente() { return cmbCliente; }
    public JComboBox<Profesional> getCmbProfesional() { return cmbProfesional; }
}
