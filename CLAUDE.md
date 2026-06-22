# CLAUDE.md — Contexto del proyecto MusicGO

Guía para cualquier sesión de Claude (o desarrollador) que trabaje en este
repositorio. **Léela completa antes de modificar código.**

## Qué es

**MusicGO** es un simulador de plataforma de streaming de audio (estilo
Spotify / Apple Music) escrito en **Java**, hecho para la cátedra de
**Programación Orientada a Objetos (UCAB)** — Prof. Marcel J. Castro G.

- **Fase 1** (entregada): motor de lógica de negocio + interfaz por **consola**.
- **Fase 2** (este repo): refactorización profunda a **GUI JavaFX** con
  arquitectura **MVC**, dos perfiles (Administrador / Usuario final),
  pasarela de pagos multimodal, control parental, compartir playlists,
  dashboard de estadísticas y persistencia JSON en caliente con hilos.

## Stack y ejecución

- **Java 25** (Temurin) · **JavaFX 25.0.3** · **Maven** (con wrapper).
- JavaFX NO viene en el JDK: lo resuelve Maven. No hay que instalar nada gracias
  al wrapper.

```bash
./mvnw javafx:run        # arranca la GUI (macOS/Linux)
mvnw.cmd javafx:run      # Windows
./mvnw clean compile     # solo compilar
```

La app lee/escribe los JSON en `data/` (relativo al directorio de ejecución).
Se puede pasar otra ruta: `./mvnw javafx:run -Djavafx.args="/otra/ruta"`.

### Credenciales de prueba

- **Administradores** (`data/administradores.json`): `admin` / `admin123`,
  `marcel` / `musicgo2026`.
- **Usuarios** (login por alias): `rocker99`, `podcastlover`, `marcel`.
  `podcastlover` tiene el control parental activo (sirve para demostrar el
  bloqueo de contenido explícito).

## Arquitectura — MVC en capas

El sistema está estrictamente separado en tres capas (requisito de la Fase 2).
**Las vistas no contienen lógica de negocio ni acceso a datos.**

```
musicgo/
├── MusicGoApp.java        Punto de entrada JavaFX (extends Application)
├── app/                   Infraestructura: AppContext (composition root),
│                          Router (navegación), Vista (contrato)
├── modelo/                MODELO — entidades de dominio (sin UI, sin JavaFX)
│   └── pago/              Medios de pago concretos + Recibo
├── interfaces/            Contratos: Identificable, Reproducible, Comprable
│   └── pago/              Interfaces SEGREGADAS de pago (ISP)
├── excepciones/           Excepciones de dominio personalizadas
├── persistencia/          Repositorio + Mapeadores JSON + persistencia async
├── servicios/             CONTROL/negocio — "gestores" (un servicio por tarea)
│   └── pago/              ProcesadorPago (Strategy)
├── controlador/           CONTROLADORES (MVC) — cablean vista <-> servicios
└── vista/                 VISTAS JavaFX (solo UI, sin lógica)
    ├── ui/                Sistema de diseño: Tema, Componentes, Alertas
    ├── admin/             Back-office
    └── usuario/           App del usuario final
        ├── AppUsuarioVista     Armazón: sidebar 4 módulos, topbar, reproductor
        ├── CatalogoVista       Cuadrícula + buscador en tiempo real + filtros
        ├── PlaylistsVista      Gestión + botón "Reproducir todo"
        ├── ComprasVista        Historial de compras + métricas de saldo
        ├── DashboardVista      Estadísticas + Top reproducidos
        └── ReproductorBarra    Mini reproductor fijo en la parte inferior
```

- **Modelo** = `modelo/` + `interfaces/` + `excepciones/` + `servicios/` +
  `persistencia/` (lógica y datos, sin nada de JavaFX).
- **Vista** = `vista/` (arma nodos JavaFX, expone controles, llama callbacks).
- **Controlador** = `controlador/` (escucha eventos de la vista, invoca al
  modelo, captura excepciones y muestra alertas).

### Módulos de la app de usuario

| Módulo | Vista | Controlador | Función |
|---|---|---|---|
| Catálogo | `CatalogoVista` | `CatalogoUsuarioControlador` | Explorar, reproducir, comprar; buscador en vivo |
| Mis playlists | `PlaylistsVista` | `PlaylistControlador` | Crear/eliminar, compartir, reproducir todo |
| Mis compras | `ComprasVista` | `ComprasControlador` | Historial de compras y saldo actual |
| Estadísticas | `DashboardVista` | `DashboardControlador` | Top reproducidos, métricas de uso |

Recursos visuales en `src/main/resources/musicgo/vista/ui/estilos.css`
(tema "Apple Music light"; todos los colores viven ahí).

## Reglas de código (OBLIGATORIAS)

1. **Máximo 300 líneas por archivo.** Si un archivo crece, divídelo. No hay
   mínimo. (El más grande hoy es `JsonParser` con 271.)
2. **SOLID**:
   - *SRP*: cada clase/gestor hace una sola cosa (un gestor por tarea).
   - *OCP*: agregar un medio de pago o un tipo de contenido no debe obligar a
     reescribir lo existente (ver `MedioPago`, `Producto`, `Audio`).
   - *LSP*: `FondosInsuficientesException` extiende `PagoRechazadoException`.
   - *ISP*: pagos con interfaces pequeñas (`RequiereTarjeta`,
     `RequiereCuentaBancaria`, `UsaSaldoVirtual`) — ningún medio implementa
     métodos vacíos.
   - *DIP*: los controladores dependen de abstracciones; `AppContext` cablea.
3. **KISS / DRY**: pocas líneas bien pensadas; nada de lógica duplicada
   (`Componentes` evita repetir UI; los `Mapeador*` centralizan el JSON).
4. **Encapsulamiento**: atributos privados, getters/setters con validación.
5. **JavaDoc** en todas las clases y métodos públicos. En español.
6. **Idioma**: nombres y comentarios en **español** (convención del equipo).
7. **MVC estricto**: NADA de `System.out.println` ni lógica de negocio en
   `vista/`; NADA de JavaFX en `modelo/`, `servicios/` o `persistencia/`.
8. **Persistencia**: solo JSON, vía los `Mapeador*` y `ServicioPersistencia`
   (escritura en hilo de fondo). El parser/writer JSON es propio (sin librerías).

## Persistencia

- Archivos en `data/`: `catalogo.json`, `usuarios.json`,
  `administradores.json`, `reproducciones.json` (contador global, se crea solo).
- `ServicioPersistencia` guarda en un **hilo daemon** (no congela la UI) y
  hace cierre limpio al salir (`AppContext.cerrar()`).
- Los `Mapeador*` son los únicos que conocen la forma de cada JSON.

## Verificación

- `./mvnw clean compile` debe terminar en BUILD SUCCESS.
- La lógica de negocio se valida headless (sin GUI). Cualquier cambio en
  servicios/modelo debería seguir pasando una prueba como la del informe.

## Documentos relacionados

- `CONTRIBUTING.md` — **reglas para modificar el proyecto en GitHub** (léelo
  antes de tocar nada si eres del equipo).
- `CAMBIOS.md` — bitácora de todos los cambios de la Fase 2.
- `docs/` — diagramas UML, informe técnico y PDFs (Fase 2 + defensa).
- `README.md` — instrucciones de uso para el usuario final.
