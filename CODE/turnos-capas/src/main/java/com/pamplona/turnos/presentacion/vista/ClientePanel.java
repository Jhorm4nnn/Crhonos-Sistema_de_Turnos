package com.pamplona.turnos.presentacion.vista;

import com.pamplona.turnos.dominio.Cliente;

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

/** Vista de registro de clientes (HU-1). Solo muestra y captura datos. */
public class ClientePanel extends JPanel {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtContacto = new JTextField(20);
    private final JButton btnRegistrar = new JButton("Registrar cliente");
    private final JTable tabla = Componentes.tabla("Id", "Nombre", "Contacto");

    public ClientePanel() {
        super(new BorderLayout(10, 10));
        setBorder(Componentes.margen());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Nuevo cliente"));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Contacto (telefono/correo):"));
        form.add(txtContacto);
        form.add(new JLabel());
        form.add(btnRegistrar);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    public String getNombre() { return txtNombre.getText(); }
    public String getContacto() { return txtContacto.getText(); }

    public void limpiar() {
        txtNombre.setText("");
        txtContacto.setText("");
    }

    public void addRegistrarListener(ActionListener l) { btnRegistrar.addActionListener(l); }

    public void mostrarClientes(List<Cliente> clientes) {
        List<Object[]> filas = new ArrayList<>();
        for (Cliente c : clientes) {
            filas.add(new Object[]{c.getId(), c.getNombre(), c.getContacto()});
        }
        Componentes.llenar(tabla, filas);
    }

    // Acceso para pruebas
    public void setNombre(String s) { txtNombre.setText(s); }
    public void setContacto(String s) { txtContacto.setText(s); }
    public JButton getBtnRegistrar() { return btnRegistrar; }
}
