package com.pamplona.turnos.presentacion.vista;

import com.pamplona.turnos.dominio.Profesional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

/** Vista de registro de profesionales (HU-1). */
public class ProfesionalPanel extends JPanel {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtInicio = new JTextField("08:00");
    private final JTextField txtFin = new JTextField("17:00");
    private final JButton btnRegistrar = new JButton("Registrar profesional");
    private final JTable tabla = Componentes.tabla("Id", "Nombre", "Disponible desde", "Disponible hasta");

    public ProfesionalPanel() {
        super(new BorderLayout(10, 10));
        setBorder(Componentes.margen());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo profesional"));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Hora inicio (HH:mm):"));
        form.add(txtInicio);
        form.add(new JLabel("Hora fin (HH:mm):"));
        form.add(txtFin);
        form.add(new JLabel());
        form.add(btnRegistrar);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public String getNombre() { return txtNombre.getText(); }
    public String getHoraInicio() { return txtInicio.getText(); }
    public String getHoraFin() { return txtFin.getText(); }

    public void limpiar() {
        txtNombre.setText("");
    }

    public void addRegistrarListener(ActionListener l) { btnRegistrar.addActionListener(l); }

    public void mostrarProfesionales(List<Profesional> lista) {
        List<Object[]> filas = new ArrayList<>();
        for (Profesional p : lista) {
            filas.add(new Object[]{p.getId(), p.getNombre(), p.getHoraInicio().toString(), p.getHoraFin().toString()});
        }
        Componentes.llenar(tabla, filas);
    }

    // Acceso para pruebas
    public void setNombre(String s) { txtNombre.setText(s); }
    public void setHoras(String inicio, String fin) { txtInicio.setText(inicio); txtFin.setText(fin); }
    public JButton getBtnRegistrar() { return btnRegistrar; }
}
