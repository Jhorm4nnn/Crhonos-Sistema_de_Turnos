package com.pamplona.turnos;

import com.pamplona.turnos.consola.MenuConsola;
import com.pamplona.turnos.gestion.Agenda;
import com.pamplona.turnos.reglas.ReglaDisponibilidad;
import com.pamplona.turnos.reglas.ReglaFechaFutura;
import com.pamplona.turnos.reglas.ReglaSinCruce;

import java.time.Clock;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Agenda agenda = new Agenda(List.of(
                new ReglaFechaFutura(Clock.systemDefaultZone()),
                new ReglaDisponibilidad(),
                new ReglaSinCruce()));
        new MenuConsola(agenda).iniciar();
    }
}
