package com.pamplona.turnos.aplicacion.servicios;

import com.pamplona.turnos.aplicacion.puertos.entrada.Comandos;
import com.pamplona.turnos.aplicacion.puertos.entrada.ListarClientesUseCase;
import com.pamplona.turnos.aplicacion.puertos.entrada.RegistrarClienteUseCase;
import com.pamplona.turnos.aplicacion.puertos.salida.ClienteRepositoryPort;
import com.pamplona.turnos.aplicacion.seguridad.Autorizacion;
import com.pamplona.turnos.aplicacion.seguridad.UsuarioAutenticado;
import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.excepciones.DuplicadoException;

import java.time.Clock;
import java.util.List;

/** Casos de uso de clientes (HU-1). Depende solo de puertos, nunca de archivos ni de HTTP. */
public class ClienteService implements RegistrarClienteUseCase, ListarClientesUseCase {

    private final ClienteRepositoryPort repo;
    private final Clock reloj;

    public ClienteService(ClienteRepositoryPort repo, Clock reloj) {
        this.repo = repo;
        this.reloj = reloj;
    }

    @Override
    public Cliente ejecutar(UsuarioAutenticado usuario, Comandos.RegistrarCliente c) {
        Autorizacion.exigirAdmin(usuario, reloj);
        // El constructor de Cliente valida los datos (vacios y formatos).
        Cliente nuevo = new Cliente(repo.siguienteId(), c.nombre(), c.documento(), c.telefono(), c.correo(), c.genero());
        if (repo.buscarPorDocumento(nuevo.getDocumento()).isPresent()) {
            throw new DuplicadoException("Ya existe un cliente con el documento " + nuevo.getDocumento() + ".");
        }
        repo.guardar(nuevo);
        return nuevo;
    }

    @Override
    public List<Cliente> ejecutar(UsuarioAutenticado usuario) {
        Autorizacion.exigirAutenticado(usuario, reloj);
        return repo.listar();
    }
}
