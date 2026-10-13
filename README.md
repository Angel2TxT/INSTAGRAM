# GramPulse (InstaConnect)

**GramPulse** (también conocido como *InstaConnect*) es una aplicación moderna multiplataforma desarrollada con **Kotlin Multiplatform (KMP)** y **Compose Multiplatform**, diseñada para ofrecer una experiencia fluida inspirada en Instagram tanto en dispositivos móviles (**Android**) como en ordenadores de escritorio (**Desktop - Windows/macOS/Linux**).

El proyecto integra soporte tanto para la **Meta Graph API oficial de Instagram** como para un **Modo Demostración interactivo** completo que permite explorar todas las funcionalidades sin necesidad de configurar credenciales de desarrollo.

---

## Tabla de Contenidos

- [Características Principales](#características-principales)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Stack Tecnológico](#stack-tecnológico)
- [Requisitos Previos](#requisitos-previos)
- [Inicialización y Ejecución](#inicialización-y-ejecución)
  - [Ejecutar en Desktop](#1-ejecutar-en-desktop)
  - [Ejecutar en Android](#2-ejecutar-en-android)
  - [Empaquetar para Escritorio](#3-empaquetado-de-escritorio)
- [Configuración de Meta / Instagram API](#configuración-de-meta--instagram-api)
- [Equipo de Desarrollo](#equipo-de-desarrollo)
- [Licencia y Créditos](#licencia-y-créditos)

---

## Características Principales

- **Autenticación Versátil (3 Modos):**
  - **Modo Demostración (Offline / Mock Data):** Explora de inmediato el feed, perfiles, historias y estadísticas con datos simulados interactivos.
  - **Flujo Oficial OAuth 2.0 (Meta Developers):** Diálogo integrado con intercambio seguro de código por token mediante endpoints oficiales de Meta.
  - **Token de Acceso Directo:** Opción para ingresar tokens temporales generados desde *Graph API Explorer*.
- **Feed y Publicaciones Dinámicas:**
  - Visualización de publicaciones con imágenes, títulos, fecha, carruseles y enlaces directos.
  - Interacciones en tiempo real de *Me gusta* (con animaciones de corazón) y guardado en colecciones.
  - Modales para ver comentarios detallados y ampliar fotografías.
- **Barra de Historias (Stories):**
  - Historias interactivas en carrusel horizontal con bordes degradados estilo Instagram.
  - Visor inmersivo a pantalla completa con temporizador y barra de progreso.
- **Perfil de Usuario Completo:**
  - Métricas de cuenta: seguidores, seguidos y cantidad de publicaciones.
  - Biografía, nombre y fotografía de perfil en alta resolución.
  - Vista en cuadrícula (*Grid*) con visualizador de posts individuales.
- **Pantalla "Acerca de" Interactiva:**
  - Sección dedicada al equipo de desarrollo con datos académicos, roles en el proyecto, bio oficial, métricas y enlaces directos a sus perfiles de Instagram.
- **Diseño Premium y Microinteracciones:**
  - Tema *Glossy Dark Mode* con degradados neón y componentes Material 3.
  - Animación nativa de bienvenida (*Splash Screen*) con efecto de pulso continuo en Android.

---

## Arquitectura del Proyecto

El proyecto está organizado en una arquitectura modular limpia multi-módulo:

```
instagram/
├── androidApp/               # Módulo nativo Android
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/.../android/
│       │   ├── MainActivity.kt        # Host de Compose
│       │   └── SplashActivity.kt      # Splash screen nativo animado
│       └── res/                       # Recursos XML, layouts y drawables
│
├── desktopApp/               # Módulo de escritorio JVM (Compose Desktop)
│   └── src/desktopMain/kotlin/
│       └── Main.kt                   # Ventana y punto de entrada para PC
│
├── shared/                   # Módulo común KMP (Lógica y UI compartida)
│   └── src/
│       ├── commonMain/kotlin/com/instaconnect/
│       │   ├── App.kt                # Enrutador principal y Scaffold
│       │   ├── data/                 # Repositorios, fuentes de datos y modelos
│       │   │   ├── InstagramConfig.kt
│       │   │   ├── InstagramRepository.kt
│       │   │   └── models/
│       │   ├── theme/                # Paleta de colores, tipografías y tema
│       │   └── ui/                   # Pantallas y componentes Compose
│       │       ├── about/            # Pantalla de equipo y detalles
│       │       ├── components/       # Componentes reusables (Avatar, AsyncImage)
│       │       ├── home/             # Feed, historias, modales
│       │       ├── login/            # Login, OAuth y selección de modo
│       │       └── welcome/          # Onboarding y bienvenida
│       ├── androidMain/              # Implementaciones específicas para Android
│       ├── desktopMain/              # Implementaciones específicas para Desktop
│       └── iosMain/                  # Preparado para target iOS
│
├── gradle/
│   └── libs.versions.toml    # Catálogo centralizado de versiones
├── build.gradle.kts          # Configuración raíz de plugins
└── settings.gradle.kts       # Definición de submódulos y repositorios
```

---

## Stack Tecnológico

| Componente | Tecnología / Versión |
| :--- | :--- |
| **Lenguaje** | [Kotlin 2.2.10](https://kotlinlang.org/) |
| **Framework UI** | [Jetpack Compose / Compose Multiplatform 1.7.1](https://www.jetbrains.com/lp/compose-multiplatform/) |
| **Android Gradle Plugin** | AGP 9.0.1 (Compile SDK 36, Target SDK 35, Min SDK 24) |
| **Cliente HTTP** | [Ktor 3.0.0](https://ktor.io/) (`ktor-client-core`, `content-negotiation`, `cio`, `okhttp`, `darwin`) |
| **Serialización** | `kotlinx-serialization-json 1.7.1` |
| **Asincronía** | `kotlinx-coroutines 1.8.1` |
| **Diseño / Componentes** | Material 3 & Material Icons Extended |
| **Java Toolchain** | JDK 17 (resuelto mediante Foojay Resolver Convention) |

---

## Requisitos Previos

1. **Java Development Kit (JDK):**
   - JDK 17 o superior instalado (ej. Eclipse Temurin, Oracle JDK u OpenJDK 17).
   - Asegúrate de tener la variable de entorno `JAVA_HOME` configurada.
2. **Entorno de Desarrollo (IDE Recomendado):**
   - **Android Studio** (Ladybug / Meerkat o más reciente) con el plugin *Kotlin Multiplatform* habilitado.
   - Opcionalmente **IntelliJ IDEA Ultimate** para ejecutar Desktop.
3. **Android SDK (Para ejecutar en Android):**
   - Android SDK Platforms 34 / 35 / 36 instalados.
   - Un emulador configurado o un dispositivo físico con depuración USB habilitada.

---

## Inicialización y Ejecución

Clona o abre la carpeta del proyecto en tu terminal:

```bash
cd c:\APLICACIONES\instagram
```

### 1. Ejecutar en Desktop

La forma más rápida de probar la aplicación sin necesidad de emuladores ni dispositivos móviles es mediante el target de escritorio:

- **En Windows:**
  ```powershell
  .\gradlew.bat :desktopApp:run
  ```
- **En macOS / Linux:**
  ```bash
  ./gradlew :desktopApp:run
  ```

> **Nota:** Se abrirá una ventana de escritorio con las dimensiones estilizadas de un teléfono móvil (440x860 px) ejecutando toda la interfaz gráfica en Compose Desktop.

---

### 2. Ejecutar en Android

#### Desde Android Studio:
1. Abre el proyecto en Android Studio (`File -> Open...` y selecciona la carpeta del proyecto).
2. Espera a que la sincronización de Gradle finalice con éxito.
3. En la barra superior, selecciona la configuración de ejecución **`androidApp`**.
4. Elige tu dispositivo o emulador conectado y presiona el botón verde de **Run** (`Shift + F10`).

#### Desde Línea de Comandos:
Asegúrate de tener un emulador encendido o un dispositivo conectado reconocido por `adb devices`:

- **En Windows:**
  ```powershell
  .\gradlew.bat :androidApp:installDebug
  ```
- **En macOS / Linux:**
  ```bash
  ./gradlew :androidApp:installDebug
  ```

---

### 3. Empaquetado de Escritorio

Para compilar un instalador ejecutable independiente para el sistema operativo actual:

```powershell
.\gradlew.bat :desktopApp:packageDistributionForCurrentOS
```

Los instaladores generados (`.msi` en Windows, `.dmg` en macOS o `.deb` en Linux) se ubicarán en `desktopApp/build/compose/binaries/main/`.

---

## Configuración de Meta / Instagram API

El archivo central de configuración se encuentra en:
[shared/src/commonMain/kotlin/com/instaconnect/data/InstagramConfig.kt](file:///c:/APLICACIONES/instagram/shared/src/commonMain/kotlin/com/instaconnect/data/InstagramConfig.kt)

```kotlin
object InstagramConfig {
    var clientId: String = "TU_CLIENT_ID"
    var clientSecret: String = "TU_CLIENT_SECRET"
    var redirectUri: String = "https://localhost/callback"
    var useMockData: Boolean = false
    var customAccessToken: String = ""
    ...
}
```

### Pasos para conectar con la API Real:
1. Ingresa a [Meta for Developers](https://developers.facebook.com/) y crea una aplicación de tipo **Negocios (Business)**.
2. Agrega el producto **Instagram Graph API**.
3. En la configuración de la app, añade como URL de redirección: `https://localhost/callback`.
4. Reemplaza en `InstagramConfig.kt` tu **Client ID** (`clientId`) y **Client Secret** (`clientSecret`).
5. Los permisos solicitados por defecto son:
   - `instagram_business_basic`
   - `instagram_business_manage_messages`
   - `instagram_business_manage_comments`
   - `instagram_business_content_publish`
   - `instagram_business_manage_insights`

> **Nota:** Si deseas probar la aplicación sin credenciales de Meta, simplemente activa la opción **"Modo Demo"** desde la pantalla de inicio de sesión o mantén `useMockData = true`.

---

## Equipo de Desarrollo

Proyecto desarrollado para la materia de **Desarrollo Móvil Multiplataforma** (10° Cuatrimestre) en la **Universidad Politécnica de Chiapas (UPChiapas)**:

| Desarrollador | Rol en el Proyecto | Perfil Instagram |
| :--- | :--- | :--- |
| **Arbey Esteban Torres Hernández** | Arquitectura KMP & Estructura del Proyecto | [@arbe__yy](https://www.instagram.com/arbe__yy/) |
| **Angel Emmanuel Trujillo López** | Lead Developer & Meta Graph API Integration | [@angll_trujillo](https://www.instagram.com/angll_trujillo/) |
| **Carlos Jafet Domínguez López** | UI/UX Designer & Compose Specialist | [@jzzstyl](https://www.instagram.com/jzzstyl/) |
| **Roldan Aldemar Arcos Gómez** | Backend, QA & Multiplatform Deployment | [@rdy__a](https://www.instagram.com/rdy__a/) |

---

## Licencia y Créditos

Este proyecto fue desarrollado con fines educativos y de investigación sobre arquitecturas multiplataforma con Kotlin y Compose Multiplatform. 
Las marcas y diseños de Instagram son propiedad de Meta Platforms, Inc.