# UniTeam

**UniTeam** es una aplicación móvil orientada a estudiantes, desarrollada para facilitar la **organización, gestión y colaboración en proyectos académicos realizados en equipo**.

El proyecto es desarrollado por **BBI Dynamics (Burro Blanco Innovation)** como parte del **Laboratorio de Desarrollo de Software del CECyT 9 "Juan de Dios Bátiz" del Instituto Politécnico Nacional**.

UniTeam busca centralizar en una misma aplicación herramientas para organizar tareas, consultar actividades pendientes y completadas, colaborar con compañeros y contar con herramientas de apoyo basadas en **inteligencia artificial**.

---

## Tecnologías utilizadas

UniTeam es una aplicación móvil desarrollada para dispositivos Android.

### Aplicación móvil

* **Java** — Lenguaje principal utilizado para el desarrollo de la aplicación.
* **Android Studio** — Entorno de desarrollo utilizado para la creación y mantenimiento del proyecto.
* **Android SDK** — Herramientas necesarias para el desarrollo de la aplicación Android.
* **SQLite** — Sistema de base de datos utilizado para almacenar la información de la aplicación.
* **Gradle** — Sistema utilizado para la gestión y configuración del proyecto y sus dependencias.

---

## Requisitos previos

Para ejecutar y desarrollar UniTeam se requiere contar con las siguientes herramientas:

* **Android Studio** — Entorno de desarrollo recomendado.
* **JDK** — Java Development Kit compatible con la versión utilizada por el proyecto.
* **Android SDK** — SDK correspondiente a la versión de Android configurada en el proyecto.
* **Git** — Para clonar y administrar el repositorio.
* **Dispositivo Android o emulador** — Para ejecutar y probar la aplicación.

---

## Instalación y configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/[usuario]/[repositorio].git
```

### 2. Abrir el proyecto

Después de clonar el repositorio:

1. Abrir **Android Studio**.
2. Seleccionar **Open**.
3. Seleccionar la carpeta del proyecto **UniTeam**.
4. Esperar a que Android Studio sincronice el proyecto y descargue las dependencias necesarias.

### 3. Configurar el proyecto

Verificar que Android Studio tenga configurado:

* El **JDK** requerido por el proyecto.
* El **Android SDK** correspondiente.
* Las herramientas de compilación necesarias.
* Un dispositivo Android físico o un emulador.

### 4. Ejecutar la aplicación

1. Conectar un dispositivo Android mediante USB o iniciar un emulador.
2. Esperar a que el dispositivo sea reconocido por Android Studio.
3. Seleccionar el dispositivo de ejecución.
4. Presionar **Run ▶**.
5. Android Studio compilará e instalará UniTeam en el dispositivo seleccionado.

---

## Base de datos

UniTeam utiliza **SQLite** como sistema de almacenamiento local.

SQLite permite almacenar la información necesaria para el funcionamiento de la aplicación directamente en el dispositivo Android, sin requerir un servidor de base de datos externo.

La base de datos se utiliza para gestionar información relacionada con las funcionalidades de la aplicación, incluyendo las tareas y los datos necesarios para la organización de los proyectos académicos.

---

## Funcionalidades

UniTeam está diseñado para proporcionar herramientas que faciliten la organización y colaboración durante la realización de proyectos académicos.

Entre sus principales funcionalidades se encuentran:

* **Inicio de sesión** para acceder al sistema.
* **Registro y administración de tareas académicas.**
* **Consulta de tareas pendientes y terminadas.**
* **Marcado de tareas como completadas.**
* **Edición de tareas.**
* **Eliminación de tareas.**
* **Organización de actividades académicas.**
* **Colaboración entre integrantes de un equipo.**
* **Comunicación entre los miembros del proyecto.**
* **Herramientas de apoyo mediante inteligencia artificial.**

Las funcionalidades podrán ampliarse conforme avance el desarrollo de UniTeam.

---

## Roles del sistema

UniTeam está orientado principalmente a estudiantes que trabajan en proyectos académicos en equipo.

### Estudiante

El estudiante podrá:

* Crear y administrar tareas.
* Consultar actividades pendientes y completadas.
* Marcar actividades como terminadas.
* Editar y eliminar tareas.
* Organizar sus actividades académicas.
* Colaborar con los integrantes de su equipo.
* Consultar información relacionada con sus proyectos.

---

## Objetivo

El objetivo de **UniTeam** es proporcionar una herramienta que facilite la **organización, planificación y colaboración de los estudiantes durante la realización de proyectos académicos en equipo**.

La aplicación busca reducir la desorganización provocada por el uso de diferentes plataformas y centralizar las actividades relacionadas con un proyecto en un mismo espacio.

---

## Problemática

La realización de proyectos académicos en equipo requiere una adecuada organización, comunicación y distribución de actividades entre los integrantes.

Sin embargo, los estudiantes pueden presentar dificultades para llevar un seguimiento de las tareas asignadas, identificar actividades pendientes y conocer el avance general del proyecto.

El uso de diferentes medios de comunicación y herramientas digitales puede provocar que la información se encuentre distribuida entre diferentes plataformas, dificultando su consulta y seguimiento.

Como consecuencia, pueden generarse retrasos, desorganización y una distribución desigual de las actividades.

Ante esta situación, **UniTeam** surge como una herramienta que busca centralizar la gestión de proyectos académicos y facilitar la colaboración entre los estudiantes.

---

## Misión

Desarrollar soluciones de software innovadoras y funcionales que ayuden a los estudiantes a organizar sus actividades académicas, administrar sus proyectos escolares y mejorar la colaboración en equipo mediante herramientas tecnológicas accesibles y fáciles de utilizar.

---

## Visión

Ser una aplicación de gestión reconocida por crear soluciones tecnológicas que mejoren la organización, productividad y colaboración de los estudiantes, contribuyendo al desarrollo de una educación más eficiente mediante la innovación tecnológica.

---

## Requerimientos

### Requerimientos funcionales

El sistema deberá:

* Permitir que los usuarios inicien sesión.
* Permitir registrar nuevas tareas académicas.
* Mostrar una lista de tareas pendientes y terminadas.
* Permitir marcar una tarea como completada.
* Permitir editar las tareas registradas.
* Permitir eliminar tareas registradas.
* Facilitar la organización de actividades académicas.
* Permitir la colaboración entre integrantes de un equipo.
* Proporcionar herramientas de apoyo para la organización de proyectos.
* Integrar funcionalidades basadas en inteligencia artificial.

### Requerimientos no funcionales

* **Usabilidad:** La interfaz deberá ser sencilla y fácil de comprender para los estudiantes.
* **Rendimiento:** Las operaciones principales deberán responder de manera adecuada bajo condiciones normales de uso.
* **Seguridad:** El sistema deberá validar las credenciales antes de permitir el acceso a una cuenta.
* **Compatibilidad:** La aplicación deberá funcionar correctamente en dispositivos Android compatibles.
* **Mantenibilidad:** El código deberá mantenerse organizado para facilitar la corrección de errores y futuras modificaciones.
* **Escalabilidad:** La estructura del proyecto deberá permitir agregar nuevas funcionalidades conforme evolucione UniTeam.

---

## Estructura del proyecto

El proyecto está desarrollado en **Java** utilizando **Android Studio** y se organiza de acuerdo con la estructura de un proyecto Android.

```text
UniTeam/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           ├── res/
│           └── AndroidManifest.xml
├── gradle/
├── build.gradle
├── settings.gradle
└── README.md
```

La estructura puede modificarse conforme avance el desarrollo del proyecto.

---

## Equipo de desarrollo

### BBI Dynamics

**Burro Blanco Innovation**

Proyecto escolar desarrollado en:

**Instituto Politécnico Nacional**  
**CECyT 9 "Juan de Dios Bátiz"**  
**Laboratorio de Desarrollo de Software**

### Integrantes

* **Arellano Vázquez Saúl Alejandro**
* **Ramirez Mercado César**
* **Reyes Díaz Marley Valentina**

---

## Eslogan

> **"Tecnología que conecta, organización que impulsa"**

---

## Estado del proyecto

**En desarrollo**

UniTeam se encuentra actualmente en proceso de desarrollo. Las funcionalidades, diseño y estructura del proyecto pueden cambiar conforme avance la implementación.

---

## Licencia

Este proyecto fue desarrollado con fines **académicos** como parte del Laboratorio de Desarrollo de Software del **CECyT 9 "Juan de Dios Bátiz" del Instituto Politécnico Nacional**.

---

**BBI Dynamics — Burro Blanco Innovation**

*Tecnología que conecta, organización que impulsa.*
