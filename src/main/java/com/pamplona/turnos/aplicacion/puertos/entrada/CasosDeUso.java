package com.pamplona.turnos.aplicacion.puertos.entrada;

/** Agrupa todos los puertos de entrada para pasarselos juntos a cada adaptador de entrada. */
public record CasosDeUso(
        IniciarSesionUseCase iniciarSesion,
        RegistrarClienteUseCase registrarCliente,
        ListarClientesUseCase listarClientes,
        RegistrarProfesionalUseCase registrarProfesional,
        ListarProfesionalesUseCase listarProfesionales,
        CrearTurnoUseCase crearTurno,
        CancelarTurnoUseCase cancelarTurno,
        ReprogramarTurnoUseCase reprogramarTurno,
        ConsultarTurnosUseCase consultarTurnos) { }
