package com.pamplona.turnos.adaptadores.entrada.rest;

import com.pamplona.turnos.dominio.Cliente;
import com.pamplona.turnos.dominio.Persona;
import com.pamplona.turnos.dominio.Profesional;
import com.pamplona.turnos.dominio.Turno;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Convierte objetos del dominio al JSON que devuelve la API. */
final class Representaciones {

    private Representaciones() { }

    static String cliente(Cliente c) {
        return "{" + persona(c) + "}";
    }

    static String profesional(Profesional p) {
        return "{" + persona(p) + ",\"horaInicio\":" + Json.texto(p.getHorario().getInicio().toString())
                + ",\"horaFin\":" + Json.texto(p.getHorario().getFin().toString()) + "}";
    }

    static String turno(Turno t) {
        return "{\"id\":" + t.getId()
                + ",\"clienteId\":" + t.getCliente().getId()
                + ",\"clienteNombre\":" + Json.texto(t.getCliente().getNombreCompleto())
                + ",\"profesionalId\":" + t.getProfesional().getId()
                + ",\"profesionalNombre\":" + Json.texto(t.getProfesional().getNombreCompleto())
                + ",\"fecha\":" + Json.texto(t.getFecha().toString())
                + ",\"horaInicio\":" + Json.texto(t.getHora().toString())
                + ",\"horaFin\":" + Json.texto(t.getHoraFin().toString())
                + ",\"estado\":" + Json.texto(t.getEstado().name()) + "}";
    }

    static <T> String lista(List<T> items, Function<T, String> aJson) {
        return items.stream().map(aJson).collect(Collectors.joining(",", "[", "]"));
    }

    static String error(String mensaje) {
        return "{\"error\":" + Json.texto(mensaje) + "}";
    }

    private static String persona(Persona p) {
        return "\"id\":" + p.getId()
                + ",\"nombreCompleto\":" + Json.texto(p.getNombreCompleto())
                + ",\"documento\":" + Json.texto(p.getDocumento())
                + ",\"telefono\":" + Json.texto(p.getTelefono())
                + ",\"correo\":" + Json.texto(p.getCorreo())
                + ",\"genero\":" + Json.texto(p.getGenero().name());
    }
}
