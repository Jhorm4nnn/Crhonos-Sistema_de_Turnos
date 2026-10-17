package com.pamplona.turnos.presentacion.vista;

import com.pamplona.turnos.dominio.Genero;
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
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/** Vista de registro de profesionales (HU-2). */
public class ProfesionalPanel extends JPanel {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDocumento = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JComboBox<Genero> cmbGenero = new JComboBox<>(Genero.values());
    private final JTextField txtInicio = new JTextField("08:00");
    private final JTextField txtFin = new JTextField("17:00");
    private final JButton btnRegistrar = new JButton("Registrar profesional");
    private final JTable tabla = Componentes.tabla("Id", "Nombre completo", "Documento", "Telefono", "Correo",
            "Genero", "Disponible");

    public ProfesionalPanel() {
        super(new BorderLayout(10, 10));
        setBorder(Componentes.margen());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo profesional"));
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
        form.add(new JLabel("Disponible desde (HH:mm):"));
        form.add(txtInicio);
        form.add(new JLabel("Disponible hasta (HH:mm):"));
        form.add(txtFin);
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
    public String getHoraInicio() { return txtInicio.getText(); }
    public String getHoraFin() { return txtFin.getText(); }

    public void limpiar() {
        txtNombre.setText("");
        txtDocumento.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cmbGenero.setSelectedIndex(0);
    }

    public void addRegistrarListener(ActionListener l) { btnRegistrar.addActionListener(l); }

    public void mostrarProfesionales(List<Profesional> lista) {
        List<Object[]> filas = new ArrayList<>();
        for (Profesional p : lista) {
            filas.add(new Object[]{p.getId(), p.getNombreCompleto(), p.getDocumento(), p.getTelefono(),
                p.getCorreo(), p.getGenero(), p.getHorario().toString()});
        }
        Componentes.llenar(tabla, filas);
    }

    // Acceso para pruebas
    public void setDatos(String nombre, String documento, String telefono, String correo, Genero genero,
                         String inicio, String fin) {
        txtNombre.setText(nombre);
        txtDocumento.setText(documento);
        txtTelefono.setText(telefono);
        txtCorreo.setText(correo);
        cmbGenero.setSelectedItem(genero);
        txtInicio.setText(inicio);
        txtFin.setText(fin);
    }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JTable getTabla() { return tabla; }
}
