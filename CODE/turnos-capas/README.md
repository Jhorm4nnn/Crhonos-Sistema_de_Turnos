# Sistema de Generacion de Turnos - version EN CAPAS (con login al authcore)

NetBeans: File > Open Project > carpeta `turnos-capas` (proyecto Maven). Run: clase `com.pamplona.turnos.Main`.
Datos en `data/` (se crea sola).

## Login con el authcore del profesor
Al abrir la app pide usuario y clave y llama a `POST {url}/api/auth/login`; lee del JWT el usuario, los roles y la expiracion.
- ADMIN: todas las pestanas (clientes, profesionales, turnos, consulta).
- Otro rol (USER): solo la pestana Consulta. Los servicios tambien lo validan, no solo la pantalla.
- URL por defecto: `http://localhost:8081`. Para cambiarla (por ejemplo cuando este en AWS), sin tocar codigo:
  NetBeans > clic derecho al proyecto > Properties > Run > VM Options: `-Dauthcore.url=http://IP-O-DOMINIO:8081`
  (o la variable de entorno `AUTHCORE_URL`).
- Usuario de prueba del authcore: `admin` / `admin123`.

## Capas (cada una solo llama a la inferior)
| Capa | Paquete | Clases |
|---|---|---|
| Presentacion | `presentacion.vista`, `presentacion.controlador` | MainFrame, LoginFrame, paneles; controladores delgados (solo leen la pantalla y llaman al servicio) |
| Negocio | `negocio` | AuthService, ClienteService, ProfesionalService, TurnoService, TurnoConsultaService, Sesion, ReglaTurno y sus reglas |
| Datos | `datos` | Repositorios (interfaz + implementacion en archivos), AuthCoreHttpClient |
| Dominio | `dominio` | Cliente, Profesional, Turno |

## De MVC a capas: que paso con cada clase
| En MVC | En capas |
|---|---|
| modelo/Cliente, Profesional, Turno | dominio/ (igual) |
| modelo/ClienteDAO, ProfesionalDAO, TurnoDAO | datos/*RepositoryArchivo, ahora detras de una interfaz |
| controlador/ClienteController (validaba + guardaba) | negocio/ClienteService (reglas) + presentacion/controlador/ClienteController (solo pantalla) |
| controlador/ProfesionalController | negocio/ProfesionalService + controlador delgado |
| controlador/TurnoController (reglas + consulta) | negocio/TurnoService, TurnoConsultaService, Regla* + controlador delgado |
| controlador/ValidacionException | negocio/ReglaNegocioException |
| vista/* | presentacion/vista/* (+ LoginFrame) |
| (no existia) | Sesion, AuthService, AuthGateway: login y roles |

## SOLID
- S (responsabilidad unica): ClienteService / ProfesionalService / TurnoService (escribe) / TurnoConsultaService (lee); TurnoDetalleAssembler solo arma nombres; Entradas solo convierte texto; los controladores ya no tienen reglas.
- O (abierto/cerrado): las reglas de un turno son clases que implementan `ReglaTurno` (FechaFutura, Disponibilidad, SinCruce). Una regla nueva = una clase nueva y una linea en `Main`; `TurnoService` no cambia.
- L (sustitucion de Liskov): cualquier implementacion de `ClienteRepository`, `TurnoRepository`, `ProfesionalRepository` o `AuthGateway` puede reemplazar a otra sin romper los servicios (hoy archivos; manana MySQL).
- I (segregacion de interfaces): interfaces pequenas y especificas (`ReglaTurno` con un metodo, `AuthGateway` con uno, un repositorio por entidad).
- D (inversion de dependencias): los servicios dependen de interfaces (`*Repository`, `AuthGateway`, `Clock`), no de clases concretas. Las clases concretas se crean solo en `Main` (raiz de composicion).

## Cobertura
HU-1 a HU-5 y HU-8 (RF-1 a RF-5, RNF-3). Pendiente: HU-6/RNF-1 (CLI + API REST) y HU-7/RNF-2, en la fase hexagonal.
