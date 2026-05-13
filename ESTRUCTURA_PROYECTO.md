#  ESTRUCTURA FINAL DEL PROYECTO

```
NutrikaliApp/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/nutrikaliapp/
│   │   │   │   ├── models/
│   │   │   │   │   └── AuthModels.kt ✅ NUEVO
│   │   │   │   │       └── LoginRequest
│   │   │   │   │       └── RegisterRequest
│   │   │   │   │       └── AuthResponse
│   │   │   │   │       └── UserData
│   │   │   │   │
│   │   │   │   ├── network/
│   │   │   │   │   ├── ApiService.kt ✅ NUEVO
│   │   │   │   │   │   └── fun login(): AuthResponse
│   │   │   │   │   │   └── fun register(): AuthResponse
│   │   │   │   │   │
│   │   │   │   │   └── RetrofitClient.kt ✅ NUEVO
│   │   │   │   │       └── BASE_URL = "http://10.0.2.2:3000/"
│   │   │   │   │       └── ApiService instance
│   │   │   │   │
│   │   │   │   ├── utils/
│   │   │   │   │   └── TokenManager.kt ✅ NUEVO
│   │   │   │   │       └── saveToken()
│   │   │   │   │       └── getToken()
│   │   │   │   │       └── isLoggedIn()
│   │   │   │   │       └── clearAll()
│   │   │   │   │
│   │   │   │   ├── MainActivity.kt ✅ ACTUALIZADO
│   │   │   │   │   └── performLogin()
│   │   │   │   │   └── showRegistrationDialog()
│   │   │   │   │   └── validateInputs()
│   │   │   │   │
│   │   │   │   ├── activity_home.kt
│   │   │   │   ├── FirstFragment.kt
│   │   │   │   └── SecondFragment.kt
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_home.xml
│   │   │   │   │   └── dialog_register.xml ✅ NUEVO
│   │   │   │   │
│   │   │   │   ├── xml/
│   │   │   │   │   ├── network_security_config.xml ✅
│   │   │   │   │   ├── data_extraction_rules.xml
│   │   │   │   │   └── backup_rules.xml
│   │   │   │   │
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   ├── ic_launcher_foreground.xml
│   │   │   │   │   ├── ic_settings.xml
│   │   │   │   │   └── ic_user.xml
│   │   │   │   │
│   │   │   │   └── mipmap-*/
│   │   │   │
│   │   │   └── AndroidManifest.xml ✅ ACTUALIZADO
│   │   │       └── <uses-permission android:name="android.permission.INTERNET" />
│   │   │       └── <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
│   │   │       └── android:networkSecurityConfig="@xml/network_security_config"
│   │   │
│   │   └── test/
│   │       └── java/com/example/nutrikaliapp/ExampleUnitTest.kt
│   │
│   ├── androidTest/
│   │   └── java/com/example/nutrikaliapp/ExampleInstrumentedTest.kt
│   │
│   ├── build.gradle.kts ✅ ACTUALIZADO
│   │   └── Retrofit 2.9.0
│   │   └── OkHttp Logging Interceptor
│   │   └── Coroutines Android
│   │   └── Lifecycle Runtime KTX
│   │
│   └── proguard-rules.pro
│
├── gradle/
│   └── libs.versions.toml
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── local.properties
│
├── SETUP_AUTH_API.md ✅ NUEVO (Documentación)
├── VERIFICACION_CHECKLIST.md ✅ NUEVO (Checklist)
└── ESTRUCTURA_PROYECTO.md ✅ NUEVO (Este archivo)
```

---

##  FLUJO DE DATOS

### 1️⃣ **Iniciar Sesión (Login)**

```
┌─────────────────────────────────────────────────────────────────┐
│                       MainActivity (Login)                       │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │ Usuario ingresa:                                         │  │
│  │ - Email: usuario@example.com                            │  │
│  │ - Password: ••••••                                       │  │
│  │ - Click "Ingresar"                                      │  │
│  └─────────────────────────────────────────────────────────┘  │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  Validación Local (Kotlin)                      │
│  - Email contiene @? ✓                                         │
│  - Password.length >= 6? ✓                                     │
│  - Todos los campos completos? ✓                               │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│            Retrofit → HTTP POST (OkHttp + Gson)                │
│  URL: http://10.0.2.2:3000/api/auth/login                    │
│  Body: {"email": "...", "password": "..."}                    │
│  Headers: Content-Type: application/json                       │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼ (Red)
┌─────────────────────────────────────────────────────────────────┐
│                   Backend (Node.js + Express)                   │
│  1. Recibe LoginRequest                                         │
│  2. Busca usuario en BD PostgreSQL                             │
│  3. Compara password con hash (bcryptjs)                       │
│  4. Genera JWT token                                            │
│  5. Devuelve AuthResponse                                       │
│     {                                                           │
│       "token": "eyJhbGciOiJIUzI1NiIs...",                    │
│       "user": {"id": 1, "email": "...", "name": "..."},       │
│       "message": "Sesión iniciada correctamente"               │
│     }                                                           │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼ (Red)
┌─────────────────────────────────────────────────────────────────┐
│                    TokenManager (SharedPrefs)                  │
│  - Guarda JWT en: "jwt_token"                                 │
│  - Guarda email en: "user_email"                              │
│  - Guarda nombre en: "user_name"                              │
│  - isLoggedIn() retorna TRUE                                   │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  HomeActivity (Inicio)                          │
│  - Usuario autenticado ✓                                        │
│  - Token persistente ✓                                          │
│  - Puede usar API protegida ✓                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2️⃣ **Registrarse (Signup)**

```
┌──────────────────────────────────────┐
│  MainActivity - Click "Registrarse"  │
└────────────┬──────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  AlertDialog (dialog_register.xml)   │
│  - Email field                       │
│  - Password field                    │
│  - Name field (opcional)             │
└────────────┬──────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  Validaciones (igual a Login)        │
└────────────┬──────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  Retrofit → POST /api/auth/register  │
│  Body: {email, password, name}       │
└────────────┬──────────────────────────┘
             │
             ▼ (Red)
┌──────────────────────────────────────┐
│  Backend:                            │
│  1. Valida email único               │
│  2. Hashea password (bcryptjs)       │
│  3. Inserta en BD PostgreSQL         │
│  4. Genera JWT                       │
│  5. Devuelve AuthResponse            │
└────────────┬──────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  TokenManager guarda token           │
└────────────┬──────────────────────────┘
             │
             ▼
└──────────────────────────────────────┐
│  HomeActivity ✓                      │
└──────────────────────────────────────┘
```

### 3️⃣ **Verificación de Sesión (Reinicio App)**

```
┌──────────────────────────────────────┐
│  App Inicia → MainActivity.onCreate() │
└────────────┬──────────────────────────┘
             │
             ▼
┌──────────────────────────────────────┐
│  TokenManager.isLoggedIn()?          │
│  - Lee SharedPreferences             │
│  - Busca JWT en "jwt_token"          │
└────────┬───────────────────┬──────────┘
         │                   │
      ✓ JWT existe       ✗ JWT no existe
         │                   │
         ▼                   ▼
    HomeActivity         MainActivity
    (Saltea login)       (Muestra login)
```

---

##  Variables Almacenadas

### SharedPreferences (TokenManager)

```kotlin
SharedPreferences: "nutrikali_prefs"
{
    "jwt_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user_email": "usuario@example.com",
    "user_name": "Juan Pérez"
}
```

### Network Config

```xml
<!-- Permite HTTP en emulador -->
<domain includeSubdomains="true">10.0.2.2</domain>

<!-- Para producción, usa HTTPS en dominio real -->
```

---

##  Pantallas y Estados

### ✅ Login Screen (MainActivity)

```
┌─────────────────────────────────────┐
│         NUTRIKALI (Logo)            │
│                                     │
│  [____________________]  Correo     │
│  [____________________]  Contraseña │
│                                     │
│  ¿Olvidaste contraseña?  Registrase │
│                                     │
│  [      Ingresar      ]             │
└─────────────────────────────────────┘

Estados:
- [Vacío] - Botón deshabilitado
- [Cargando...] - Botón deshabilitado + Loading
- [Error] - Toast con mensaje
- [Éxito] - Redirige a HomeActivity
```

### ✅ Register Dialog

```
┌──────────────────────────────┐
│   Crear Cuenta               │
│                              │
│ [______________]  Email      │
│ [______________]  Contraseña │
│ [______________]  Nombre     │
│                              │
│  [Cancelar] [Registrarse]   │
└──────────────────────────────┘
```

### ✅ Home Screen (activity_home.kt)

```
┌─────────────────────────────────────┐
│ ⚙️  NutriKali                     │  Barra verde
├─────────────────────────────────────┤
│ [Buscar...]                         │
│                                     │
│                                     │
│  [Contenido Principal]              │
│                                     │
│                                     │
│     [Cerrar Sesión]                 │  Botón Rojo
└─────────────────────────────────────┘

Click Cerrar Sesión:
1. TokenManager.clearAll()
2. Vuelve a MainActivity
3. Ya no hay sesión persistente
```

---

##  Lógica de Seguridad

```
1. FRONTEND (Android)
   - Valida inputs antes de enviar
   - Almacena JWT en SharedPreferences (cifrado en algunos casos)
   - No envía credenciales después del login (solo token)
   
2. NETWORK (HTTP/HTTPS)
   - OkHttp Logging permite debug
   - Usa JSON (puede ser interceptado, necesita HTTPS en prod)
   - network_security_config permite HTTP solo en emulador
   
3. BACKEND (Node.js)
   - Valida email usando Prisma
   - Hashea password con bcryptjs (salt 10)
   - Genera JWT con secret firmado
   - Base de datos: PostgreSQL (encriptada en BD)
   
4. OUTPUT
   - JWT almacenado en cliente
   - Se envía en próximas requests (si lo implementas)
   - Backend verifica firma JWT antes de procesar
```

---

##  Próximas Mejoras

### Soon ⏳

```
1. Enviar JWT en requests posteriores
   - Agregar header Authorization en ApiService
   - Todos los GET POST tendrán JWT

2. Refresh Token
   - Crear endpoint POST /api/auth/refresh
   - Auto-refresh si expira

3. Pantalla de Perfil
   - GET /api/auth/profile (requiere JWT)
   - Editar nombre, email, foto

4. Olvide Contraseña
   - POST /api/auth/forgot-password
   - Enviar email con link reset
```

---

**¡Implementación Completa! ✨**
