package com.pamplona.turnos.adaptadores.entrada.swing.controlador;

import com.pamplona.turnos.adaptadores.entrada.swing.vista.LoginFrame;
import com.pamplona.turnos.adaptadores.entrada.swing.vista.Mensajes;
import com.pamplona.turnos.aplicacion.puertos.entrada.IniciarSesionUseCase;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.excepciones.TurnosException;

import javax.swing.SwingWorker;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/** Login contra el authcore sin congelar la ventana (la llamada HTTP corre en segundo plano). */
public class LoginController {

    private final LoginFrame vista;
    private final IniciarSesionUseCase iniciarSesion;
    private final Consumer<UsuarioAutenticado> alIngresar;

    public LoginController(LoginFrame vista, IniciarSesionUseCase iniciarSesion, Consumer<UsuarioAutenticado> alIngresar) {
        this.vista = vista;
        this.iniciarSesion = iniciarSesion;
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
                return iniciarSesion.ejecutar(usuario, clave);
            }

            @Override
            protected void done() {
                vista.setOcupado(false);
                try {
                    alIngresar.accept(get());
                } catch (ExecutionException e) {
                    boolean claro = e.getCause() instanceof TurnosException;
                    Mensajes.error(claro ? e.getCause().getMessage() : "Ocurrio un problema inesperado. Intente de nuevo.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
