package com.pamplona.turnos.presentacion.controlador;

import com.pamplona.turnos.negocio.ReglaNegocioException;
import com.pamplona.turnos.presentacion.vista.Mensajes;

/** Ejecuta una accion y convierte los errores en mensajes claros (RNF-3). */
abstract class ControladorBase {

    protected void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (ReglaNegocioException e) {
            Mensajes.error(e.getMessage());
        } catch (RuntimeException e) {
            Mensajes.error("Ocurrio un problema inesperado. Intente de nuevo.");
        }
    }
}
