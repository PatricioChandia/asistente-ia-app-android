Proyecto Asistente IA - App Android

Esta es la aplicación cliente nativa de Android para el proyecto de asistente de voz. Permite a los usuarios registrarse, iniciar sesión y ver su historial de chat con el asistente de IA.

✨ Características

Flujo de Autenticación: Pantallas de Login y Registro que se conectan al backend.

Sesiones de Usuario: Guarda el token JWT del usuario en SharedPreferences para mantener la sesión.

Interfaz de Chat: Una interfaz de chat completa construida con RecyclerView que muestra las burbujas de chat del usuario y de la IA.

Comunicación en Tiempo Real (vía API):

Envía consultas al backend.

Carga el historial de chat al iniciar.

🚀 Tecnologías Utilizadas

Android Nativo (Java)

Android Studio

Retrofit: Para realizar las llamadas a la API REST del backend.

Gson: Para convertir objetos Java a JSON y viceversa.

RecyclerView: Para mostrar la lista de mensajes de chat de forma eficiente.

Material Design: Para los componentes de la interfaz de usuario.

📦 Instalación y Setup

Sigue estos pasos para correr la app en Android Studio.

1. Clonar el repositorio

git clone [https://github.com/PatricioChandia/asistente-ia-app-android.git](https://github.com/PatricioChandia/asistente-ia-app-android.git)


2. Abrir en Android Studio

Abre Android Studio.

Selecciona "Open an existing project" (Abrir un proyecto existente).

Navega y selecciona la carpeta LoginBasico (o como se llame tu proyecto).

Espera a que Gradle sincronice las dependencias.

3. Configuración del Backend

Esta app está diseñada para hablar con el Backend del Asistente IA.

Asegúrate de que el servidor backend esté corriendo en http://localhost:3000.

¡IMPORTANTE!
La app usa la IP especial 10.0.2.2 para conectarse desde el Emulador de Android al localhost de tu PC. Esto está configurado en network/RetrofitClient.java:

private static final String BASE_URL = "[http://10.0.2.2:3000/](http://10.0.2.2:3000/)";


Si corres la app en un teléfono físico, esta IP no funcionará. Deberás reemplazarla con la dirección IP local de tu PC en la red WiFi.

4. Correr la App

Conecta un Emulador o un dispositivo Android.

Presiona "Run" (▶) en Android Studio.

Deberías poder registrar un nuevo usuario e iniciar sesión.
