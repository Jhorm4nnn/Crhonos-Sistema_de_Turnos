# Sistema de Turnos para un Negocio 

Un sistema de gestión de citas y turnos diseñado para solucionar los problemas de agendamiento en negocios pequeños, evitando los choques de horarios y optimizando la atención al cliente.

## Descripción del Problema
Actualmente, muchos negocios pequeños agendan sus citas por teléfono o utilizando un cuaderno físico. Este método tradicional es propenso a errores humanos, lo que genera constantes choques de horario, insatisfacción y, en última instancia, la pérdida de clientes.

## Descripción del Sistema
Para dar solución a esta problemática, este sistema permite crear turnos, asignarlos a los clientes correspondientes y validar los horarios para evitar que dos citas se crucen o solapen para el mismo profesional o recurso disponible.

## Requisitos Funcionales
El sistema cuenta con las siguientes características principales:
*   **Gestión de Usuarios:** Registro de Profesionales y Clientes.
*   **Gestión de Turnos:** Creación de nuevos turnos/citas.
*   **Validación de Horarios:** Sistema inteligente para evitar el solapamiento de horarios.
*   **Modificación:** Cancelación y reprogramación de turnos existentes.
*   **Consultas:** Listado y búsqueda de turnos filtrados por Profesional o por Cliente.

## Requisitos No Funcionales
*   Independencia de la interfaz de entrada (arquitectura desacoplada).
*   Tiempo de respuesta aceptable para una experiencia de usuario fluida.
*   Manejo adecuado de errores y visualización de mensajes claves y claros.

## Tecnologías Utilizadas
*   **Backend:** Java 21, Spring Boot, Spring Security, Gradle.
*   **Frontend (Cliente):** Java Swing (NetBeans).
*   **Base de Datos:** MySQL (para autenticación y roles) y persistencia en archivos de texto plano (para clientes, profesionales y turnos).

## Instrucciones de Ejecución
1.  **Base de Datos:** Tener MySQL encendido y crear la base de datos `authcore_db`.
2.  **Configuración:** En el proyecto `authcore-service`, configurar la contraseña de MySQL en el archivo `src/main/resources/application.yml`.
3.  **Backend:** Abrir una terminal en `authcore-service` y ejecutar:
    ```bash
    gradlew bootRun
