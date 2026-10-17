package com.pamplona.turnos.aplicacion.servicios;

import com.pamplona.turnos.aplicacion.puertos.entrada.IniciarSesionUseCase;
import com.pamplona.turnos.aplicacion.puertos.salida.AutenticadorPort;
import com.pamplona.turnos.aplicacion.puertos.salida.ValidadorTokenPort;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.excepciones.DatosInvalidosException;

/** Pide el token al authcore y lo lee (usuario, roles y vigencia) para abrir la sesion. */
public class IniciarSesionService implements IniciarSesionUseCase {

    private final AutenticadorPort autenticador;
    private final ValidadorTokenPort validador;

    public IniciarSesionService(AutenticadorPort autenticador, ValidadorTokenPort validador) {
        this.autenticador = autenticador;
        this.validador = validador;
    }

    @Override
    public UsuarioAutenticado ejecutar(String usuario, String clave) {
        if (usuario == null || usuario.isBlank()) {
            throw new DatosInvalidosException("El usuario es obligatorio.");
        }
        if (clave == null || clave.isEmpty()) {
            throw new DatosInvalidosException("La clave es obligatoria.");
        }
        // El token acaba de llegar directo del authcore: se lee (usuario, roles, vigencia). La firma se exige en la API.
        return validador.leer(autenticador.login(usuario.trim(), clave));
    }
}
