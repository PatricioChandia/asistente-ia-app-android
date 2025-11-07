# Proyecto Asistente IA - App Android

Esta es la aplicación cliente nativa de Android para el proyecto de asistente de voz. Permite a los usuarios registrarse, iniciar sesión y gestionar su perfil y chatear con el asistente de IA.

## ✨ Características

* **Flujo de Autenticación Completo:**
    * **Pantalla de Carga (Splash):** Revisa si el usuario ya tiene una sesión activa.
    * **Login:** Permite iniciar sesión y obtener un token JWT.
    * **Registro:** Permite registrarse y automáticamente inicia sesión (navega al menú).
* **Sesiones de Usuario:** Guarda el token JWT en `SharedPreferences` para mantener la sesión activa.
* **Menú Principal:** Pantalla de bienvenida que saluda al usuario por su nombre y muestra su foto de perfil.
* **Gestión de Perfil:**
    * Permite al usuario ver su email y nombre.
    * Permite al usuario **actualizar su nombre**.
    * Permite al usuario **subir una foto de perfil** desde la galería del teléfono.
* **Sala de Chat:**
    * Interfaz de chat completa construida con `RecyclerView` (burbujas de chat).
    * Carga el historial de chat del usuario desde el backend.
    * Envía consultas de texto a la IA y muestra la respuesta en tiempo real.
* **Carga de Imágenes:** Usa **Glide** para cargar eficientemente las fotos de perfil desde URLs (Cloudinary).

## 🚀 Tecnologías Utilizadas

* **Android Nativo (Java)**
* **Android Studio**
* **Retrofit:** Para realizar las llamadas a la API REST del backend.
* **Gson:** Para convertir objetos Java a JSON y viceversa.
* **RecyclerView:** Para mostrar la lista de mensajes de chat.
* **Glide:** Para la carga de imágenes de perfil.
* **CircleImageView:** Para las vistas de imagen de perfil circulares.
* **Material Design:** Para los componentes de la interfaz de usuario.

## 📦 Instalación y Setup

Sigue estos pasos para correr la app en Android Studio.

### 1. Clonar el repositorio


git clone [https://github.com/PatricioChandia/asistente-ia-app-android](https://github.com/PatricioChandia/asistente-ia-app-android)

2. Abrir en Android Studio
Abre Android Studio.

Selecciona "Open an existing project" (Abrir un proyecto existente).

Navega y selecciona la carpeta LoginBasico.

Espera a que Gradle sincronice las dependencias (especialmente Retrofit, Glide, etc.).

3. Configuración del Backend
Esta app está diseñada para hablar con el Backend del Asistente IA.

¡IMPORTANTE! La app está configurada para conectarse a una IP en la red local. Debes actualizar esta IP en network/RetrofitClient.java para que coincida con la IP de la PC donde corre el servidor backend.

Java

// Cambia esta IP por la IP de tu PC en la red WiFi
private static final String BASE_URL = "[http://10.0.6.15:3000/](http://10.0.6.15:3000/)";
Para el Emulador: Usa http://10.0.2.2:3000/

Para un Teléfono Físico: Usa la IP de tu PC en la WiFi (ej. http://192.168.1.105:3000/) y asegúrate de que tu firewall de Windows permita conexiones al puerto 3000.

4. Correr la App
Conecta un Emulador o un dispositivo Android.

Presiona "Run" (▶) en Android Studio.
