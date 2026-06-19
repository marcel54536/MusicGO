# Reglas para contribuir a MusicGO

> **Obligatorio:** si vas a modificar el proyecto, lee este documento completo
> antes de tocar una sola línea. Estas reglas garantizan que el código siga
> cumpliendo MVC, SOLID, KISS y el límite de 300 líneas por archivo que exige
> el enunciado y que el profesor va a evaluar en la defensa.

## 1. Reglas de oro (no negociables)

1. **Nunca hagas `commit` directo a `main`.** Trabaja siempre en una rama.
2. **Máximo 300 líneas por archivo `.java`.** Si lo superas, divide la clase.
3. **No mezcles capas (MVC):**
   - `vista/` → solo arma interfaz. Prohibido `System.out.println`, cálculos,
     reglas de negocio o leer/escribir archivos.
   - `modelo/`, `servicios/`, `persistencia/` → prohibido importar `javafx.*`.
   - `controlador/` → es el único que conecta vista con servicios y captura
     excepciones para mostrar alertas.
4. **Aplica SOLID y KISS.** Una clase = una responsabilidad. Sin lógica
   duplicada (reutiliza `Componentes`, `Alertas`, los `Mapeador*`).
5. **JavaDoc en español** en toda clase y método público.
6. **Nombres y comentarios en español.**
7. **El proyecto debe compilar** (`./mvnw clean compile` = BUILD SUCCESS)
   antes de abrir un Pull Request.

## 2. Flujo de trabajo con Git

```bash
git checkout main
git pull origin main                  # parte siempre de lo último
git checkout -b feat/mi-funcionalidad # crea tu rama
# ... cambios ...
./mvnw clean compile                  # verifica que compila
git add -A
git commit -m "feat(playlists): agregar orden alfabético"
git push -u origin feat/mi-funcionalidad
# abre un Pull Request hacia main en GitHub
```

### Nombres de rama

| Prefijo     | Para qué                          | Ejemplo                         |
|-------------|-----------------------------------|---------------------------------|
| `feat/`     | funcionalidad nueva               | `feat/buscador-catalogo`        |
| `fix/`      | corrección de error               | `fix/saldo-negativo`            |
| `refactor/` | reorganizar sin cambiar conducta  | `refactor/dividir-admin-vista`  |
| `docs/`     | documentación                     | `docs/actualizar-readme`        |

### Mensajes de commit (Conventional Commits)

`tipo(área): descripción corta en imperativo`
Tipos: `feat`, `fix`, `refactor`, `docs`, `test`, `chore`.

## 3. Issues y Project (Hito 2)

El profesor evalúa el uso de **Issues** y **Project** de GitHub.

- Toda tarea nace como un **Issue** (usa la plantilla).
- Mueve la tarjeta en el **Project** (Todo → In progress → Done).
- Enlaza el PR al Issue con `Closes #N` en la descripción para que se cierre
  solo al hacer merge.

## 4. Pull Requests

- Un PR = un cambio enfocado. No mezcles varias funcionalidades.
- Describe **qué** cambiaste y **por qué**, y enlaza el Issue (`Closes #N`).
- Requiere **al menos 1 aprobación** de otro integrante antes del merge.
- Quien revisa usa la checklist de la sección 5.
- Borra la rama después del merge.

## 5. Checklist de revisión (antes de aprobar)

- [ ] Compila (`./mvnw clean compile`).
- [ ] Ningún archivo supera 300 líneas.
- [ ] No hay `javafx.*` en modelo/servicios/persistencia.
- [ ] No hay lógica de negocio ni `println` en `vista/`.
- [ ] Las excepciones se capturan en el controlador y se muestran con `Alertas`.
- [ ] Hay JavaDoc en lo nuevo y los nombres están en español.
- [ ] No se duplicó lógica que ya existía.
- [ ] Si tocó persistencia, se usó un `Mapeador*` (no JSON a mano por ahí).

## 6. Dónde va cada cosa

| Si vas a...                                  | Trabaja en...                         |
|----------------------------------------------|---------------------------------------|
| Agregar un campo a una entidad               | `modelo/` + su `Mapeador*`            |
| Crear una regla de negocio                   | `servicios/` (un gestor)              |
| Agregar un medio de pago                     | `modelo/pago/` + interfaz en `interfaces/pago/` |
| Cambiar una pantalla                         | `vista/...` + su controlador          |
| Cambiar colores/estética                     | `resources/.../estilos.css`           |
| Añadir una pantalla nueva                    | nueva `*Vista` + nuevo `*Controlador` |

## 7. Antes de la defensa

- Asegúrate de que `main` compila y arranca (`./mvnw javafx:run`).
- Revisa que `data/*.json` tenga datos de prueba coherentes.
- El último commit que cuenta es el del **jueves 02-07-26** (defensa 03-07-26).

¿Dudas de arquitectura? Lee `CLAUDE.md`. Gracias por mantener el código limpio. 🎧
