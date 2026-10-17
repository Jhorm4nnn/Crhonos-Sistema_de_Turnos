# Fase 4 - Arquitectura HEXAGONAL (puertos y adaptadores) - Sistema de Generacion de Turnos

El mismo sistema, con **tres puertas de entrada** (ventanas, consola y API REST) que usan los mismos casos de uso.
NetBeans: File > Open Project > carpeta `backend-fase4-hexagonal` (proyecto Maven, Java 17+, sin librerias externas).

## Como ejecutarlo
`Main` recibe el modo como argumento. En NetBeans: clic derecho al proyecto > Properties > Run > Arguments.
| Modo | Argumento | Que hace |
|---|---|---|
| Ventanas (por defecto) | *(vacio)* o `gui` | Login y pestanas, igual que la fase 3 |
| Consola (CLI) | `cli` | Menu por consola con login |
| API REST | `api` | Servidor HTTP en el puerto 8082 |

Configuracion (en VM Options como `-Dnombre=valor`, o como variable de entorno):
| Que | Propiedad | Variable | Por defecto |
|---|---|---|---|
| URL del authcore | `authcore.url` | `AUTHCORE_URL` | `http://100.27.6.5:8080` |
| Clave con que el authcore firma el JWT | `jwt.secret` | `JWT_SECRET` | la del authcore del profesor |
| Puerto de la API | `api.puerto` | `API_PUERTO` | `8082` |

**Sobre `jwt.secret`:** solo la **API REST** lo necesita, porque ahi llegan tokens de afuera y hay que comprobar que son autenticos.
Debe ser EXACTAMENTE la clave con la que el authcore firma (en AWS, la variable `JWT_SECRET` del servicio); si es distinta, la API responde 401 "firma incorrecta".
Las ventanas y la consola no la necesitan: piden el token directo al authcore y solo leen usuario, roles y vigencia.

## La idea del hexagono
```
   ADAPTADORES DE ENTRADA            NUCLEO (no conoce a nadie de afuera)            ADAPTADORES DE SALIDA
  (quien usa el sistema)                                                            (con que se conecta)

  Ventanas Swing  ──┐                                                          ┌──  Archivos de texto
  Consola (CLI)   ──┼──►  PUERTOS DE ENTRADA  ──►  Servicios  ──►  PUERTOS DE SALIDA  ──┤    (repositorios)
  API REST        ──┘     (casos de uso)          + Dominio        (interfaces)   └──  authcore (login HTTP)
                                                                                  └──  Validador del JWT (HS256)
```
- **Nucleo** = `dominio` + `aplicacion`. No importa nada de `adaptadores` ni de `config` (se puede comprobar con un buscador de imports).
- **Puertos de entrada** (`aplicacion.puertos.entrada`): lo que el sistema ofrece. Una interfaz por caso de uso.
- **Puertos de salida** (`aplicacion.puertos.salida`): lo que el sistema necesita del exterior (guardar datos, autenticar, validar tokens).
- **Adaptadores**: traducen. Los de entrada convierten ventanas / texto / HTTP en llamadas a casos de uso; los de salida implementan los puertos de salida.
- **`config.Ensamblador`** es el unico lugar donde se eligen los adaptadores concretos.

## Estructura
| Paquete | Contenido |
|---|---|
| `dominio` | Persona (abstracta), Cliente, Profesional, HorarioDisponibilidad, Turno, Genero |
| `excepciones` | Jerarquia de errores (TurnosException y sus hijas) |
| `aplicacion.puertos.entrada` | RegistrarClienteUseCase, RegistrarProfesionalUseCase, CrearTurnoUseCase, CancelarTurnoUseCase, ReprogramarTurnoUseCase, ConsultarTurnosUseCase, ListarClientes/ProfesionalesUseCase, IniciarSesionUseCase, Comandos, CasosDeUso |
| `aplicacion.puertos.salida` | ClienteRepositoryPort, ProfesionalRepositoryPort, TurnoRepositoryPort, AutenticadorPort, ValidadorTokenPort |
| `aplicacion.servicios` | ClienteService, ProfesionalService, TurnoService, ConsultaTurnosService, IniciarSesionService (implementan los casos de uso) |
| `aplicacion.reglas` | ReglaTurno, ReglaFechaFutura, ReglaDisponibilidad, ReglaSinCruce |
| `aplicacion.seguridad` | UsuarioAutenticado, Autorizacion (rol y vigencia) |
| `adaptadores.entrada.swing` | Ventanas y sus controladores |
| `adaptadores.entrada.consola` | ConsolaCli |
| `adaptadores.entrada.rest` | ApiRest, Json, Representaciones |
| `adaptadores.salida.persistencia` | Repositorios en archivo (implementan los puertos) |
| `adaptadores.salida.seguridad` | AuthCoreHttpClient (login), JwtHs256Validador (`validar` = con firma, para la API; `leer` = sin firma, para el login) |
| `config` | Configuracion, Ensamblador |

## De capas (fase 3) a hexagonal (fase 4)
| En capas | En hexagonal |
|---|---|
| `negocio/*Service` con metodos mezclados | `aplicacion/servicios` que implementan un **caso de uso por interfaz** (puerto de entrada) |
| `datos/*Repository` (interfaz en la capa de datos) | `aplicacion/puertos/salida/*Port`: la **interfaz pertenece al nucleo**, el archivo es un adaptador |
| `presentacion/*` (unica forma de usar el sistema) | `adaptadores/entrada/*`: ventanas, consola y API son iguales de importantes |
| `Sesion` guardada en el servicio | El usuario viaja en cada llamada (`UsuarioAutenticado`); asi funciona igual en la API sin estado |
| `AuthGateway` | `AutenticadorPort` + `ValidadorTokenPort` (validacion de firma incluida) |
| `Main` armaba todo | `config.Ensamblador` arma todo; `Main` solo elige la puerta de entrada |

## API REST (HU-7)
Todas las rutas (menos `/api/salud`) exigen `Authorization: Bearer <token del authcore>`. Cada respuesta trae `X-Tiempo-Ms` (HU-9).
| Metodo y ruta | Quien | Codigos |
|---|---|---|
| `GET /api/salud` | cualquiera | 200 |
| `POST /api/clientes` `{nombre, documento, telefono, correo, genero}` | ADMIN | 201, 400, 401, 403, 409 |
| `GET /api/clientes` | autenticado | 200 |
| `POST /api/profesionales` `{... , horaInicio, horaFin}` | ADMIN | 201, 400, 401, 403, 409 |
| `GET /api/profesionales` | autenticado | 200 |
| `POST /api/turnos` `{clienteId, profesionalId, fecha, hora}` | ADMIN | 201, 400, 404, 409 (cruce, fuera de horario, fecha pasada) |
| `GET /api/turnos?profesionalId=&clienteId=&desde=&hasta=` | autenticado | 200, 400 |
| `POST /api/turnos/{id}/cancelar` | ADMIN | 200, 404, 409 |
| `PUT /api/turnos/{id}` `{fecha, hora}` (reprogramar) | ADMIN | 200, 404, 409 |

Ejemplo (cmd): primero se pide el token al authcore y luego se usa:
```
curl.exe -X POST http://100.27.6.5:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password123\"}"
curl.exe http://localhost:8082/api/turnos -H "Authorization: Bearer EL_TOKEN"
```

## Historias de usuario
| Historia | Caso de uso |
|---|---|
| HU-1 Registrar cliente | RegistrarClienteUseCase |
| HU-2 Registrar profesional | RegistrarProfesionalUseCase |
| HU-3 Crear turno | CrearTurnoUseCase |
| HU-4 Sin cruces | ReglaSinCruce (dentro de CrearTurno y ReprogramarTurno) |
| HU-5 Cancelar o reprogramar | CancelarTurnoUseCase, ReprogramarTurnoUseCase |
| HU-6 Consultar | ConsultarTurnosUseCase |
| HU-7 CLI y API | adaptadores `consola` y `rest`, sobre los mismos casos de uso (sin duplicar logica) |
| HU-8 Errores claros | Jerarquia TurnosException; cada adaptador la traduce (mensaje en ventana/consola, codigo HTTP en la API) |
| HU-9 Menos de 1 segundo | CLI muestra los ms de cada operacion; la API manda `X-Tiempo-Ms` (en las pruebas, menos de 20 ms) |

## SOLID
Se mantiene lo de la fase 3 y se refuerza:
- **D:** el nucleo depende de puertos (interfaces) y nunca de archivos, HTTP o ventanas.
- **I:** un puerto de entrada por caso de uso; cada adaptador usa solo los que necesita.
- **O:** una regla nueva o un adaptador nuevo (por ejemplo MySQL) se agrega sin modificar el nucleo.
- **S y L:** un servicio por responsabilidad; cualquier adaptador de salida sustituye a otro que implemente el mismo puerto.

## Limites
- Los datos siguen guardandose en archivos de texto (`data/`). Pasar a MySQL = escribir un adaptador nuevo que implemente los tres `*RepositoryPort` y cambiar una linea en `Ensamblador`.
- La API usa el servidor HTTP incluido en Java (sin Spring) para que corra sin descargar librerias.
