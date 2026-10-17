# Fase 2 - MVC - Sistema de Generacion de Turnos

Interfaz grafica (Swing) con persistencia en archivos de texto (carpeta `data/`, se crea sola).
NetBeans: File > Open Project > carpeta `backend-fase2-mvc` (proyecto Maven, Java 17+). Run: clase `com.pamplona.turnos.Main`.

## Actores
- **Administrador**: unico que opera el sistema.
- **Cliente** y **Profesional**: actores externos; el sistema guarda sus datos. Tienen pestanas separadas.

## MVC
| Parte | Paquete | Clases | Responsabilidad |
|---|---|---|---|
| Modelo | `modelo` | Persona (abstracta), Cliente, Profesional, HorarioDisponibilidad, Turno, Genero | Datos y comportamiento propio de cada objeto (heredado de la fase 1) |
| Modelo (acceso a datos) | `modelo.dao` | ArchivoDAO, ClienteDAO, ProfesionalDAO, TurnoDAO | Guardar y leer de archivos |
| Vista | `vista` | MainFrame, ClientePanel, ProfesionalPanel, TurnoPanel, ConsultaPanel, Mensajes | Mostrar y capturar datos; no decide nada |
| Controlador | `controlador` | ClienteController, ProfesionalController, TurnoController | Recibe lo que hace el usuario, aplica las reglas y actualiza la vista |

En MVC las **reglas de negocio** (fecha futura, disponibilidad, cruce de horarios) viven en `TurnoController`.
En la fase 3 (capas) se sacan a servicios.

## Historias de usuario
| Historia | Donde esta |
|---|---|
| HU-1 Registrar cliente (5 datos, sin documento duplicado) | ClientePanel, ClienteController, ClienteDAO |
| HU-2 Registrar profesional con horario | ProfesionalPanel, ProfesionalController, ProfesionalDAO |
| HU-3 Crear turno (fecha futura, dentro del horario) | TurnoPanel, TurnoController.crear |
| HU-4 Sin cruces de horario | TurnoController.validarHorario |
| HU-5 Cancelar o reprogramar | TurnoController.cancelar / reprogramar |
| HU-6 Consultar con filtros | ConsultaPanel, TurnoController.consultar |
| HU-8 Mensajes de error claros | ControladorBase + jerarquia TurnosException |
| HU-7 CLI y API | Pendiente (fase 4, hexagonal) |
| HU-9 Menos de 1 segundo | Las operaciones son inmediatas con pocos datos; la medicion formal llega en fase 4 |

## Cambios frente a la fase 1 (POO)
- El modelo se conserva; ahora se **guarda en archivos** (DAO) y se agrega la **interfaz grafica**.
- `Agenda` y las clases `Regla*` desaparecen: las reglas pasan al controlador (asi se ve despues el salto a capas).
