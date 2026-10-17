package com.pamplona.turnos.vista;

import com.pamplona.turnos.modelo.Cliente;
import com.pamplona.turnos.modelo.Genero;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Vista de registro de clientes (HU-1). Solo muestra y captura datos. */
public class ClientePanel extends JPanel {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDocumento = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JComboBox<Genero> cmbGenero = new JComboBox<>(Genero.values());
    private final JButton btnRegistrar = new JButton("Registrar cliente");
    private final JTable tabla = Componentes.tabla("Id", "Nombre completo", "Documento", "Telefono", "Correo", "Genero");

    public ClientePanel() {
        super(new BorderLayout(10, 10));
        setBorder(Componentes.margen());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo cliente"));
        form.add(new JLabel("Nombre completo:"));
        form.add(txtNombre);
        form.add(new JLabel("Documento:"));
        form.add(txtDocumento);
        form.add(new JLabel("Telefono:"));
        form.add(txtTelefono);
        form.add(new JLabel("Correo:"));
        form.add(txtCorreo);
        form.add(new JLabel("Genero:"));
        form.add(cmbGenero);
        form.add(new JLabel());
        form.add(btnRegistrar);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public String getNombre() { return txtNombre.getText(); }
    public String getDocumento() { return txtDocumento.getText(); }
    public String getTelefono() { return txtTelefono.getText(); }
    public String getCorreo() { return txtCorreo.getText(); }
    public Genero getGenero() { return (Genero) cmbGenero.getSelectedItem(); }

    public void limpiar() {
        txtNombre.setText("");
        txtDocumento.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cmbGenero.setSelectedIndex(0);
    }

    public void addRegistrarListener(ActionListener l) { btnRegistrar.addActionListener(l); }

    public void mostrarClientes(List<Cliente> clientes) {
        List<Object[]> filas = new ArrayList<>();
        for (Cliente c : clientes) {
            filas.add(new Object[]{c.getId(), c.getNombreCompleto(), c.getDocumento(), c.getTelefono(),
                c.getCorreo(), c.getGenero()});
        }
        Componentes.llenar(tabla, filas);
    }

    // Acceso para pruebas
    public void setDatos(String nombre, String documento, String telefono, String correo, Genero genero) {
        txtNombre.setText(nombre);
        txtDocumento.setText(documento);
        txtTelefono.setText(telefono);
        txtCorreo.setText(correo);
        cmbGenero.setSelectedItem(genero);
    }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JTable getTabla() { return tabla; }
}
