# 🎧 MusicGO — Plataforma de Streaming (Fase 2)

Proyecto de la cátedra de **Programación Orientada a Objetos (UCAB)** ·
Prof. Marcel J. Castro G.

MusicGO simula una plataforma de streaming de audio estilo Spotify / Apple Music.
La **Fase 2** migra el motor de la Fase 1 (consola) a una **interfaz gráfica
JavaFX** con arquitectura **MVC**, dos perfiles de acceso, pasarela de pagos,
control parental, compartir playlists y un dashboard de estadísticas en vivo.

> Persistencia 100% en archivos **JSON** con un parser/writer propio (sin
> librerías externas de JSON). La GUI usa **JavaFX**, resuelto por Maven.

## ✨ Funcionalidades

- **Login con dos perfiles**: Administrador (back-office) y Usuario final.
- **Back-office**: alta, edición y baja de canciones, podcasts y productos, con
  actualización en tiempo real para los usuarios.
- **Catálogo visual** en cuadrícula con filtros (canciones / podcasts / productos).
- **Playlists**: crear, editar y **compartir** con otro usuario por su alias.
- **Reproductor** con barra de progreso, controles y metadatos.
- **Control parental**: bloquea el contenido explícito con una advertencia.
- **Pasarela de pagos multimodal**: tarjeta de crédito, transferencia bancaria
  o saldo virtual, cada uno con su propia validación.
- **Dashboard**: tiempo de escucha, biblioteca, compras y **Top de más
  reproducidos** de la plataforma.

## 🚀 Cómo ejecutar

Requisito: **JDK 21 o superior** (probado con JDK 25). **No** necesitas instalar
Maven ni JavaFX: el wrapper los resuelve.

```bash
# macOS / Linux
./mvnw javafx:run

# Windows
mvnw.cmd javafx:run
```

Compilar sin ejecutar:

```bash
./mvnw clean compile
```

### Credenciales de prueba

| Perfil        | Usuario        | Clave          |
|---------------|----------------|----------------|
| Administrador | `admin`        | `admin123`     |
| Administrador | `marcel`       | `musicgo2026`  |
| Usuario       | `rocker99`     | *(solo alias)* |
| Usuario       | `podcastlover` | *(solo alias)* |

> `podcastlover` tiene el control parental activo: úsalo para ver el bloqueo de
> contenido explícito. `rocker99` tiene saldo virtual para probar la compra.

## 🧱 Arquitectura (MVC)

```
src/main/java/musicgo/
├── MusicGoApp.java     Entrada JavaFX
├── app/                AppContext, Router, Vista
├── modelo/  (+pago/)   Entidades de dominio (sin JavaFX)
├── interfaces/ (+pago/) Contratos (incl. interfaces de pago segregadas, ISP)
├── excepciones/        Excepciones personalizadas
├── persistencia/       Repositorio + Mapeadores JSON + persistencia async
├── servicios/ (+pago/) Lógica de negocio (gestores) + ProcesadorPago
├── controlador/        Controladores MVC
└── vista/  (ui/admin/usuario/)  Vistas JavaFX (solo UI)
```

Detalles de diseño, principios y convenciones: **[CLAUDE.md](CLAUDE.md)**.

## 📂 Datos

`data/` contiene `catalogo.json`, `usuarios.json` y `administradores.json`. La
app los lee al iniciar y los reescribe en caliente (en un hilo de fondo) ante
cualquier cambio. `reproducciones.json` se genera solo para el ranking.

## 👥 Para el equipo

Antes de modificar el proyecto, lee **[CONTRIBUTING.md](CONTRIBUTING.md)**
(reglas de ramas, PRs, MVC y límite de 300 líneas). El histórico de cambios de
esta fase está en **[CAMBIOS.md](CAMBIOS.md)**.

## 📑 Documentación

En `docs/`: diagramas UML, informe técnico, el **PDF de la Fase 2** (qué se hizo
y qué mejoró respecto a la Fase 1) y el **PDF de defensa**.

---

Cronograma Fase 2 — Defensa grupal: **03-07-26** · Defensa individual: **10-07-26**.
