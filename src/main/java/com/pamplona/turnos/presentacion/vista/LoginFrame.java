package com.pamplona.turnos.presentacion.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/** Ventana de inicio de sesion contra el authcore. */
public class LoginFrame extends JFrame {

    private final JTextField txtUsuario = new JTextField(16);
    private final JPasswordField txtClave = new JPasswordField(16);
    private final JButton btnIngresar = new JButton("Ingresar");

    public LoginFrame(String urlAuthcore) {
        super("Iniciar sesion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Usuario:"));
        form.add(txtUsuario);
        form.add(new JLabel("Clave:"));
        form.add(txtClave);
        form.add(new JLabel());
        form.add(btnIngresar);

        JLabel servidor = new JLabel("Servidor de autenticacion: " + urlAuthcore);
        servidor.setFont(servidor.getFont().deriveFont(10f));

        JPanel contenido = new JPanel(new BorderLayout(8, 8));
        contenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        contenido.add(new JLabel("Sistema de Generacion de Turnos"), BorderLayout.NORTH);
        contenido.add(form, BorderLayout.CENTER);
        contenido.add(servidor, BorderLayout.SOUTH);
        add(contenido);

        getRootPane().setDefaultButton(btnIngresar);
        pack();
        setLocationRelativeTo(null);
    }

    public String getUsuario() { return txtUsuario.getText(); }
    public String getClave() { return new String(txtClave.getPassword()); }

    public void setOcupado(boolean ocupado) {
        btnIngresar.setEnabled(!ocupado);
        btnIngresar.setText(ocupado ? "Conectando..." : "Ingresar");
    }

    public void addIngresarListener(ActionListener l) { btnIngresar.addActionListener(l); }

    // Acceso para pruebas
    public void setCredenciales(String usuario, String clave) {
        txtUsuario.setText(usuario);
        txtClave.setText(clave);
    }
    public JButton getBtnIngresar() { return btnIngresar; }
}
