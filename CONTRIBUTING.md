# Guía de contribución a UniTeam

¡Gracias por colaborar en UniTeam, la app de BBI Dynamics (Burro Blanco Innovation) para organizar proyectos académicos en equipo! Esta guía explica cómo trabajar en el repositorio sin pisarnos entre integrantes.

Al participar en este proyecto aceptas respetar nuestro [Código de Conducta](CODE_OF_CONDUCT.md).

## Estructura del repositorio

- `UniTeam App/`: proyecto de Android Studio (Java).
- `Documentacion/`: documentos del proyecto.
- `.github/`: plantillas de issues y flujos de trabajo (GitHub Actions).

## Requisitos para trabajar en el código

- Android Studio actualizado.
- JDK 17.
- Abrir la carpeta `UniTeam App` como proyecto en Android Studio y esperar a que termine la sincronización de Gradle.

## Flujo de trabajo

1. **Elige o crea un issue.** Toda tarea debe tener un issue donde se describa qué se va a hacer. Asígnatelo para que el equipo sepa que lo estás trabajando.
2. **Actualiza tu copia.** Antes de empezar, trae los últimos cambios de `main`.
3. **Crea una rama** a partir de `main`. No se trabaja directamente en `main`. Usa nombres cortos y claros:
   - `feature/nombre-de-la-funcion` para funciones nuevas (ej. `feature/login`).
   - `fix/descripcion-del-error` para correcciones.
   - `docs/tema` para documentación.
4. **Haz commits pequeños** y frecuentes, con mensajes claros (ver más abajo).
5. **Sube tu rama** y abre un **Pull Request** hacia `main`.
6. **Espera la revisión.** Al menos un compañero debe revisar y aprobar el Pull Request antes de mezclarlo.
7. **Mezcla y limpia.** Cuando se apruebe y la revisión automática pase, se mezcla y se elimina la rama.

## Mensajes de commit

Escribe en español, en tiempo presente o pasado, de forma breve y concreta. Ejemplos:

- `Agrega pantalla de inicio de sesión`
- `Corrige cierre de la app al editar una tarea`
- `Actualiza documentación de requerimientos`

Evita mensajes como "cambios", "arreglos" o "listo".

## Pull Requests

Antes de abrir un Pull Request, verifica que:

- La app **compila y corre** en Android Studio sin errores.
- Probaste el cambio en un emulador o dispositivo.
- El Pull Request describe **qué cambia y por qué**, e incluye capturas de pantalla si modifica la interfaz.
- Referencias el issue relacionado (por ejemplo, `Cierra #12`).
- No incluye archivos innecesarios (compilados, configuraciones personales del IDE, etc.).

Cada Pull Request ejecuta una **revisión automática** (GitHub Actions) que compila el proyecto. Si aparece una X roja, revisa el detalle del error en la pestaña **Actions** y corrígelo antes de pedir revisión.

## Estilo de código

- Nombres de clases en `PascalCase`, métodos y variables en `camelCase`, constantes en `MAYUSCULAS_CON_GUION_BAJO`.
- Un método debe hacer una sola cosa; si crece demasiado, divídelo.
- Comenta el **por qué** de las decisiones complicadas, no lo obvio.
- Elimina código comentado y `Log` de depuración antes de subir.
- Usa la función de formato de Android Studio (Code, Reformat Code) antes de hacer commit.

## Seguridad

- **Nunca subas** contraseñas, claves de API, tokens ni archivos de credenciales (por ejemplo `google-services.json` con datos privados o `local.properties`).
- Si subiste algo sensible por error, avisa al equipo de inmediato para cambiar
