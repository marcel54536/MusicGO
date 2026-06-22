# CAMBIOS — MusicGO Fase 2

Bitácora de todo lo que cambió respecto a la Fase 1. Sirve como referencia
rápida para el equipo y como insumo del informe técnico.

---

## Resumen ejecutivo

La Fase 1 era una aplicación **de consola** con un menú de texto. La Fase 2 es
una **aplicación gráfica JavaFX** con arquitectura **MVC**, dos perfiles de
acceso, pasarela de pagos, control parental, compartir playlists, dashboard de
estadísticas y persistencia en caliente con hilos. Se reutilizó el dominio del
negocio (canciones, podcasts, productos, usuarios, playlists) y se **refactorizó
en profundidad** para cumplir SOLID, KISS, DRY y el límite de 300 líneas/archivo.

| Métrica                  | Fase 1            | Fase 2                        |
|--------------------------|-------------------|-------------------------------|
| Interfaz                 | Consola (texto)   | GUI JavaFX (Apple Music light)|
| Arquitectura             | Capas + servicios | **MVC** estricto en capas     |
| Perfiles                 | Uno (genérico)    | **Administrador** y **Usuario**|
| Pagos                    | Compra directa    | **Pasarela multimodal (ISP)** |
| Build                    | `javac` manual    | **Maven + wrapper**           |
| Persistencia             | Guardado al salir | **Hilos, en caliente**        |
| Principios               | DRY, KISS         | + **SOLID completo**          |

---

## 1. Arquitectura y build

- Migración a **Maven** (`pom.xml`) con `javafx-maven-plugin`; se agregó el
  **Maven Wrapper** (`mvnw`) para que nadie tenga que instalar Maven ni JavaFX.
- Reorganización a la estructura estándar `src/main/java` + `src/main/resources`.
- Se eliminó el paquete de consola `ui/` (menús de texto) y `Main.java`.
- Nuevo punto de entrada **`MusicGoApp`** (`extends Application`).
- Capa de infraestructura **`app/`**: `AppContext` (composition root que cablea
  todas las dependencias — DIP), `Router` (navegación entre pantallas) y `Vista`
  (contrato de las vistas).

## 2. Modelo (dominio)

- **`Clasificacion`** (enum nuevo): `APTO_TODO_PUBLICO` / `EXPLICITO`, base del
  control parental.
- **`Administrador`** (clase nueva): perfil de back-office con validación de
  credenciales encapsulada; se carga de `administradores.json`.
- **`Usuario`** ampliado: `saldo` (billetera virtual), `controlParental` y
  `compartidasConmigo` (playlists que otros le compartieron).
- **`Audio`** refactorizado: se le quitó `reproducir()` que imprimía en consola
  (violaba MVC) y ahora expone `creditos()` polimórfico y `getClasificacion()`.
- **`Producto`**: `mostrarDetalle()` (consola) → `detalle()` que **devuelve**
  texto para que lo muestre la vista.
- **`Catalogo`**: se añadieron bajas (`eliminarAudio/Producto`) y filtro genérico
  por subtipo (DRY).

## 3. Pasarela de pagos (SOLID / ISP)

Carpeta nueva `modelo/pago/` + `interfaces/pago/` + `servicios/pago/`.

- Interfaz mínima común **`MedioPago`** (`etiqueta()`, `procesar()`).
- Interfaces **segregadas** por capacidad (ISP): `RequiereTarjeta`,
  `RequiereCuentaBancaria`, `UsaSaldoVirtual` — cada medio implementa solo lo
  que necesita, sin métodos vacíos.
- Tres medios concretos con **flujo de validación propio**: `TarjetaCredito`,
  `TransferenciaBancaria`, `SaldoVirtual`.
- **`ProcesadorPago`** (patrón Strategy): único punto de cobro; agregar un medio
  nuevo no obliga a modificarlo (OCP).
- **`Recibo`**: comprobante inmutable de la transacción.

## 4. Excepciones (robustez)

Nuevas excepciones personalizadas, capturadas por los controladores y mostradas
como alertas JavaFX sin tumbar el hilo de la app:

- `PagoRechazadoException` (base) y `FondosInsuficientesException` (la hereda → LSP).
- `CredencialesInvalidasException` (login).
- `ContenidoRestringidoException` (control parental).

## 5. Persistencia

- **`ServicioPersistencia`**: escribe los JSON en un **hilo daemon** (sin
  congelar la UI) y cierra de forma limpia al salir.
- **Mapeadores** dedicados (SRP, único lugar que conoce cada JSON):
  `MapeadorCatalogo`, `MapeadorUsuarios`, `MapeadorAdministradores`.
- Nuevo `administradores.json` y `reproducciones.json` (contador global de
  reproducciones para el Top de la plataforma).
- Los JSON ahora guardan `clasificacion`, `saldo`, `controlParental` y las
  playlists `compartidas`.

## 6. Servicios (negocio)

- **`GestorAutenticacion`** (nuevo): login de usuario (por alias) y de
  administrador (usuario + clave).
- **`GestorAdministradores`** (nuevo): carga y valida administradores.
- **`GestorCatalogo`** reescrito: ahora hace **CRUD** completo (alta/baja/edición),
  genera IDs, persiste en caliente y **notifica a observadores** para que los
  cambios del admin se reflejen en vivo en la app del usuario (sin acoplar el
  modelo a JavaFX).
- **`GestorUsuarios`** reescrito: registro con **validación de unicidad en vivo**
  y persistencia tras cada cambio.
- **`GestorPlaylists`**: añadida la función **Compartir Playlist** por alias.
- **`GestorReproduccion`**: aplica el **control parental** (lanza excepción si el
  perfil bloquea contenido explícito) y alimenta el contador global.
- **`GestorEstadisticas`**: ranking **Top de contenidos más reproducidos**.
- **`GestorCompras`**: integrado con la pasarela de pagos (`MedioPago`).

## 7. Vistas (JavaFX, Apple Music light)

- Sistema de diseño en `vista/ui/`: `Tema` (CSS), `Componentes` (fábrica de
  nodos estilizados — mantiene las vistas cortas y consistentes) y `Alertas`.
- **Login/Registro** con perfiles Usuario / Administrador.
- **Back-office** (`vista/admin/`): gestión visual del catálogo con
  alta/edición/baja y formularios (`DialogosCatalogo`).
- **App de usuario** (`vista/usuario/`): catálogo en cuadrícula, gestión de
  playlists, **reproductor con barra de progreso**, **dashboard** dinámico y
  **pasarela de pagos** (diálogo multimodal).
- Tema visual claro tipo Apple Music; todos los colores en un solo `estilos.css`.

## 8. Controladores (MVC)

`controlador/` nuevo: `LoginControlador`, `AdminControlador`,
`UsuarioControlador` (coordinador), `CatalogoUsuarioControlador`,
`PlaylistControlador`, `ReproductorControlador`, `DashboardControlador`,
`ComprasControlador`.
Conectan eventos de la vista con los servicios y traducen excepciones a alertas.

---

## 9. Mejoras de UI/UX (iteración posterior a la entrega base)

### 9.1 Módulo "Mis compras" (nuevo)

- Nueva clase **`ComprasVista`**: muestra 3 métricas resumen (total gastado,
  número de compras, saldo actual) y el historial como tarjetas con nombre del
  producto, fecha y monto.
- Nuevo **`ComprasControlador`**: resuelve el nombre del producto buscándolo en
  el catálogo (el `Compra` solo guarda el ID) y pasa los datos a la vista.
- Integrado en `UsuarioControlador` como cuarto módulo de navegación.

### 9.2 Navegación con 4 módulos visibles

- `AppUsuarioVista` refactorizada: 4 ítems de nav con iconos emoji
  (`🎵 Catálogo`, `📚 Mis playlists`, `🛒 Mis compras`, `📊 Estadísticas`).
- Perfil del usuario en la barra lateral: caratula generativa + alias + correo.
- Chip de saldo verde `💳 Saldo: $XX.XX` en la barra superior (clase CSS
  `.chip-saldo`; color verde `#1d8a4e` sobre fondo `#e8f7ee`).
- Resaltado activo de nav-item al cambiar de módulo (`nav-activo`).

### 9.3 Buscador en tiempo real en el catálogo

- `CatalogoVista` tiene un `TextField` en la cabecera que filtra mientras el
  usuario escribe (listener sobre `textProperty()`).
- La búsqueda es **case-insensitive** y cruza título, créditos (artista/podcast)
  y nombre/descripción de productos.
- Estado vacío "Sin resultados para tu búsqueda." cuando no hay coincidencias.

### 9.4 "Reproducir todo" en Playlists

- Botón **▶ Reproducir todo** en `PlaylistsVista`; deshabilitado si la playlist
  tiene 0 audios.
- `ReproductorControlador` nuevo método `reproducirCola(List<Audio>)`: copia la
  lista defensivamente y arranca desde el primer audio respetando el control
  parental.
- `PlaylistControlador` recibe el reproductor por constructor y cableya el
  callback en el constructor (sin setter tardío).

### 9.5 Mejoras de CSS

- `.chip-saldo` (pill verde para el saldo).
- `.check-box` con acento rojo al seleccionar (el `mark` queda blanco).

---

## Requerimientos del enunciado de Fase 2 — estado

- [x] GUI JavaFX con login y dos interfaces independientes.
- [x] Back-office: CRUD de catálogo con actualización en tiempo real.
- [x] Registro/login de usuario con validación de unicidad en vivo.
- [x] Exploración visual en cuadrícula (diferencia audios de productos).
- [x] Gestión avanzada de playlists.
- [x] Reproductor gráfico con barra de progreso y metadatos.
- [x] Compartir Playlist por alias.
- [x] Filtro y restricción de contenido (control parental + advertencia).
- [x] Dashboard dinámico con Top de más reproducidos.
- [x] Pasarela de pagos multimodal (tarjeta / transferencia / saldo) con ISP.
- [x] Excepciones personalizadas capturadas en el controlador.
- [x] Persistencia JSON inmediata con hilos.
- [x] MVC estricto, SOLID, KISS, modular (≤300 líneas/archivo).
- [x] Módulo "Mis compras" con historial y métricas.
- [x] Buscador en tiempo real en el catálogo.
- [x] "Reproducir todo" con cola de reproducción en playlists.
