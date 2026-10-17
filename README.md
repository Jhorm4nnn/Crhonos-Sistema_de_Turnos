# Fase 3 - Arquitectura en CAPAS (con login al authcore) - Sistema de Generacion de Turnos

NetBeans: File > Open Project > carpeta `backend-fase3-capas` (proyecto Maven, Java 17+). Run: clase `com.pamplona.turnos.Main`.
Los datos se guardan en `data/` (se crea sola).

## Login con el authcore
Al abrir la app pide usuario y clave y llama a `POST {url}/api/auth/login`. Del JWT lee el usuario, los roles y la expiracion.
- **ADMIN**: todas las pestanas (clientes, profesionales, turnos, consulta).
- **Otro rol (USER)**: solo la pestana Consulta. Los servicios tambien lo exigen, no solo la pantalla.
- URL por defecto: `http://localhost:8081`. Para cambiarla (por ejemplo cuando el authcore este en AWS):
  NetBeans > clic derecho al proyecto > Properties > Run > VM Options: `-Dauthcore.url=http://IP-O-DOMINIO:8081`
  (o la variable de entorno `AUTHCORE_URL`).
- Usuario de prueba del authcore: `admin` / `admin123`.

## Las capas (cada una solo llama a la inferior)
| Capa | Paquete | Clases |
|---|---|---|
| Presentacion | `presentacion.vista`, `presentacion.controlador` | MainFrame, LoginFrame, paneles; controladores delgados (leen la pantalla y llaman al servicio) |
| Negocio | `negocio`, `negocio.reglas` | AuthService, ClienteService, ProfesionalService, TurnoService, TurnoConsultaService, Sesion, ReglaTurno y sus reglas |
| Datos | `datos` | Repositorios (interfaz + implementacion en archivos), AuthGateway / AuthCoreHttpClient |
| Dominio | `dominio` | Persona (abstracta), Cliente, Profesional, HorarioDisponibilidad, Turno, Genero |

Las excepciones (`excepciones`) las usan todas las capas.

## De MVC (fase 2) a capas (fase 3)
| En MVC | En capas |
|---|---|
| modelo/Persona, Cliente, Profesional, Turno... | `dominio/` (igual) |
| modelo/dao/*DAO (clases concretas) | `datos/*Repository` (interfaz) + `*RepositoryArchivo` (implementacion) |
| controlador/*Controller con las reglas de negocio dentro | `negocio/*Service` y `negocio/reglas/*` tienen las reglas; los controladores quedan delgados |
| Reglas de turno como metodo privado del controlador | Una clase por regla: ReglaFechaFutura, ReglaDisponibilidad, ReglaSinCruce |
| El controlador validaba duplicados | El servicio valida duplicados |
| vista/* | `presentacion/vista/*` (+ LoginFrame) |
| (no existia) | Sesion, AuthService, AuthGateway: login y roles |

## Principios SOLID aplicados
- **S (responsabilidad unica):** ClienteService, ProfesionalService y TurnoService (escribe) / TurnoConsultaService (lee) hacen una sola cosa cada uno; Entradas/ControladorBase solo convierten texto y muestran errores; los controladores ya no tienen reglas.
- **O (abierto/cerrado):** las reglas de un turno son clases que implementan `ReglaTurno`. Una regla nueva = una clase nueva y una linea en `Main`; `TurnoService` no cambia.
- **L (sustitucion de Liskov):** cualquier implementacion de `ClienteRepository`, `ProfesionalRepository`, `TurnoRepository` o `AuthGateway` puede reemplazar a otra sin romper los servicios (hoy archivos; manana MySQL). `Cliente` y `Profesional` se usan donde se espera una `Persona`.
- **I (segregacion de interfaces):** interfaces pequenas: `ReglaTurno` con un metodo, `AuthGateway` con uno, un repositorio por entidad.
- **D (inversion de dependencias):** los servicios dependen de interfaces (`*Repository`, `AuthGateway`, `Clock`), no de clases concretas. Las clases concretas se crean solo en `Main` (raiz de composicion).

## Historias de usuario
| Historia | Servicio / clase |
|---|---|
| HU-1 Registrar cliente | ClienteService.registrar |
| HU-2 Registrar profesional | ProfesionalService.registrar |
| HU-3 Crear turno | TurnoService.crear, ReglaFechaFutura, ReglaDisponibilidad |
| HU-4 Sin cruces | ReglaSinCruce, CruceDeHorarioException |
| HU-5 Cancelar o reprogramar | TurnoService.cancelar / reprogramar |
| HU-6 Consultar con filtros | TurnoConsultaService.consultar |
| HU-8 Mensajes claros | ControladorBase + jerarquia TurnosException |
| HU-7 CLI y API, HU-9 tiempos | Pendientes: fase 4 (hexagonal) |

## Limites
- La app lee el JWT pero no verifica su firma; esa verificacion pasa al domain-service cuando tenga API REST (fase 4).
- Cuando el token vence (1 hora en el authcore) hay que cerrar e iniciar sesion de nuevo.
