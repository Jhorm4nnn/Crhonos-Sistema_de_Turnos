package com.pamplona.turnos.presentacion.controlador;

import com.pamplona.turnos.negocio.AuthService;
import com.pamplona.turnos.negocio.ReglaNegocioException;
import com.pamplona.turnos.negocio.UsuarioAutenticado;
import com.pamplona.turnos.presentacion.vista.LoginFrame;
import com.pamplona.turnos.presentacion.vista.Mensajes;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/** Login contra el authcore sin congelar la ventana (la llamada HTTP corre en segundo plano). */
public class LoginController {

    private final LoginFrame vista;
    private final AuthService auth;
    private final Consumer<UsuarioAutenticado> alIngresar;

    public LoginController(LoginFrame vista, AuthService auth, Consumer<UsuarioAutenticado> alIngresar) {
        this.vista = vista;
        this.auth = auth;
        this.alIngresar = alIngresar;
        vista.addIngresarListener(e -> ingresar());
    }

    private void ingresar() {
        String usuario = vista.getUsuario();
        String clave = vista.getClave();
        vista.setOcupado(true);
        new SwingWorker<UsuarioAutenticado, Void>() {
            @Override
            protected UsuarioAutenticado doInBackground() {
                return auth.login(usuario, clave);
            }

            @Override
            protected void done() {
                vista.setOcupado(false);
                try {
                    alIngresar.accept(get());
                } catch (ExecutionException e) {
                    boolean claro = e.getCause() instanceof ReglaNegocioException;
                    Mensajes.error(claro ? e.getCause().getMessage() : "Ocurrio un problema inesperado. Intente de nuevo.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
