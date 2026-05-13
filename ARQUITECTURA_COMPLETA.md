#  DIAGRAMA COMPLETO - NutriKali App

## ️ ARQUITECTURA GENERAL

```
┌─────────────────────────────────────────────────────────────────┐
│                    NUTRIKALI APP (Android)                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │              MainActivity (Login/Registro)             │    │
│  │  ┌───────────────────────────────────────────────┐    │    │
│  │  │ Email: [_________________]                    │    │    │
│  │  │ Pass:  [_________________]                    │    │    │
│  │  │                                               │    │    │
│  │  │ [¿Olvidaste?]          [Registrarse]        │    │    │
│  │  │                                               │    │    │
│  │  │ [       Ingresar       ]                     │    │    │
│  │  └───────────────────────────────────────────────┘    │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │           TokenManager (SharedPreferences)            │    │
│  │  - JWT Token                                           │    │
│  │  - User Email                                          │    │
│  │  - User Name                                           │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │         HomeActivity (Main Screen)                    │    │
│  │  [Toolbar verde con config, title, user button]      │    │
│  │  [Barra de búsqueda]                                  │    │
│  │  [Contenido Principal]                                │    │
│  │  [Botón Cerrar Sesión]                               │    │
│  └────────────────────────────────────────────────────────┘    │
│                                                                  │
│  Componentes Internos:                                          │
│  - models/AuthModels.kt (Modelos de datos)                    │
│  - network/ApiService.kt (Interfaz Retrofit)                  │
│  - network/RetrofitClient.kt (Cliente HTTP)                   │
│  - utils/TokenManager.kt (Almacenamiento tokens)              │
└─────────────────────────────────────────────────────────────────┘
                              ↑↓ HTTP
                         (10.0.2.2:3000)
┌─────────────────────────────────────────────────────────────────┐
│              BACKEND API (Node.js + Express)                    │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  POST /api/auth/login                                 │    │
│  │  {email, password} → {token, user, message}          │    │
│  │                                                        │    │
│  │  POST /api/auth/register                              │    │
│  │  {email, password, name} → {token, user, message}    │    │
│  │                                                        │    │
│  │  GET /health                                           │    │
│  │  → {status: "OK"}                                     │    │
│  └────────────────────────────────────────────────────────┘    │
│                           ↓                                      │
│  ┌────────────────────────────────────────────────────────┐    │
│  │    Prisma ORM + PostgreSQL                            │    │
│  │                                                        │    │
│  │    Table: users                                       │    │
│  │    - id (PK)                                          │    │
│  │    - email (UNIQUE)                                  │    │
│  │    - password (hashed)                               │    │
│  │    - name                                             │    │
│  │    - createdAt, updatedAt                            │    │
│  │                                                        │    │
│  │    Seguridad:                                         │    │
│  │    - bcryptjs (hash passwords)                       │    │
│  │    - jsonwebtoken (generation)                       │    │
│  │    - CORS habilitado                                 │    │
│  └────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

---

##  FLUJO DE LOGIN DETALLADO

```
Usuario entra a MainActivity
  ↓
¿Tiene jwt_token en SharedPreferences?
  ├─ SÍ → goToHome() (salta a HomeActivity)
  └─ NO → Muestra formulario login
         ↓
         Usuario ingresa email + password
         ↓
         Click "Ingresar"
         ↓
         validateInputs()
         ├─ Email vacío? → Toast + return
         ├─ Email no tiene @? → Toast + return
         ├─ Password vacío? → Toast + return
         ├─ Password < 6? → Toast + return
         └─ Todo OK ✓ → Continúa
            ↓
            lifecycleScope.launch {
              Retrofit → POST /api/auth/login
                ↓
              Backend recibe
              ├─ Busca usuario por email
              ├─ Compara password con hash (bcryptjs)
              ├─ Si no existe o password malo → 401 error
              └─ Si todo bien:
                 ├─ Genera JWT
                 └─ Devuelve {token, user, message}
                    ↓
              Android recibe response
              ├─ Extrae JWT
              ├─ Guarda en TokenManager
              ├─ Toast "Welcome!"
              └─ goToHome()
                 ↓
                 Intent → HomeActivity
                 finish() MainActivity
                 ↓
                 Usuario ve: HomeActivity ✓
            }
```

---

##  PANTALLAS

### **1. MainActivity (Login)**

```
┌─────────────────────────────────────┐
│                                     │
│       [80x80 Logo]                 │
│                                     │
│      NUTRIKALI (título)            │
│                                     │
│  ┌─────────────────────────┐       │
│  │ Correo electrónico     │       │
│  │ [_____________________] │       │
│  └─────────────────────────┘       │
│                                     │
│  ┌─────────────────────────┐       │
│  │ Contraseña             │       │
│  │ [_________] (toggle) │       │
│  └─────────────────────────┘       │
│                                     │
│  ¿Olvidaste contraseña?  Registrarse│
│                                     │
│  ┌─────────────────────────┐       │
│  │     Ingresar            │       │
│  └─────────────────────────┘       │
│                                     │
└─────────────────────────────────────┘

Color scheme:
- Fondo: Blanco
- Top section: Verde #149C68
- Botones: Verde #4CAF50, Rojo #D32F2F
- Texto: Negro/Gris
```

### **2. LoginActivity - Dialog Registrarse**

```
┌───────────────────────────────┐
│     Crear Cuenta              │
│                               │
│  ┌──────────────────────────┐ │
│  │ Correo electrónico       │ │
│  │ [____________________]   │ │
│  └──────────────────────────┘ │
│                               │
│  ┌──────────────────────────┐ │
│  │ Contraseña               │ │
│  │ [____________________]   │ │
│  └──────────────────────────┘ │
│                               │
│  ┌──────────────────────────┐ │
│  │ Nombre (opcional)        │ │
│  │ [____________________]   │ │
│  └──────────────────────────┘ │
│                               │
│  [Cancelar]  [Registrarse]    │
└───────────────────────────────┘
```

### **3. HomeActivity (tras login)**

```
┌──────────────────────────────────┐
│ ⚙️   NutriKali                │ Verde #38C958
├──────────────────────────────────┤
│ ┌───────────────────────────┐   │
│ │  Buscar...            │   │
│ └───────────────────────────┘   │
│                                  │
│                                  │
│     CONTENIDO PRINCIPAL          │
│     (Próximamente)               │
│                                  │
│                                  │
│                                  │
│                                  │
│  ┌──────────────────────────┐  │
│  │  Cerrar Sesión           │  │ Rojo #D32F2F
│  └──────────────────────────┘  │
└──────────────────────────────────┘
```

---

##  ARCHIVOS IMPORTANTES

```
NutrikaliApp/
│
├──  QUICK_START.md                ← START AQUÍ
├──  SETUP_AUTH_API.md             ← Instrucciones setup
├──  VERIFICACION_CHECKLIST.md     ← Items completados
├──  ESTRUCTURA_PROYECTO.md        ← Diagramas
└──  RESUMEN_IMPLEMENTACION.md     ← Resumen total
│
└── app/src/main/
    │
    ├── java/com/example/nutrikaliapp/
    │   ├── models/                  ✨ NUEVA
    │   │   └── AuthModels.kt
    │   │
    │   ├── network/                 ✨ NUEVA
    │   │   ├── ApiService.kt
    │   │   └── RetrofitClient.kt
    │   │
    │   ├── utils/                   ✨ NUEVA
    │   │   └── TokenManager.kt
    │   │
    │   ├── MainActivity.kt           ✏️ MODIFICADO
    │   ├── activity_home.kt
    │   └── ...
    │
    ├── res/
    │   ├── layout/
    │   │   ├── activity_main.xml
    │   │   ├── activity_home.xml
    │   │   └── dialog_register.xml  ✨ NUEVO
    │   │
    │   └── xml/
    │       └── network_security_config.xml
    │
    └── AndroidManifest.xml          ✏️ MODIFICADO
```

---

##  FLUJO DE TOKENS

```
FRONTEND (Android)
└─ JSON {credentials}
   ↓
NETWORK (HTTP POST)
└─ OkHttp sends JSON
   ↓
BACKEND (Node.js)
├─ Recibe JSON
├─ Hash password con bcryptjs
├─ Valida con BD
└─ Genera JWT con jsonwebtoken
   ↓
RESPONSE (JSON)
└─ {token: "eyJhbGc...", user: {...}}
   ↓
FRONTEND (Android)
├─ Deserializa JSON → AuthResponse
├─ Extrae JWT
├─ Guarda en SharedPreferences
│  {
│    "jwt_token": "eyJhbGc...",
│    "user_email": "user@example.com",
│    "user_name": "Juan"
│  }
└─ Inicio de sesión completado ✓
```

---

## ️ BASE DE DATOS

```
PostgreSQL Database: nutrical
User: nutrical_user
Password: nutrical_password

Table: users
┌──────────┬─────────────────┬──────────────────┬──────────┐
│ id (PK)  │ email (UNIQUE)  │ password (HASH)  │ name     │
├──────────┼─────────────────┼──────────────────┼──────────┤
│ 1        │ juan@mail.com   │ $2a$10$xXxX...  │ Juan     │
│ 2        │ maria@mail.com  │ $2a$10$yYyY...  │ Maria    │
│ 3        │ test@mail.com   │ $2a$10$zZzZ...  │ null     │
└──────────┴─────────────────┴──────────────────┴──────────┘

Ejemplo password hash (bcryptjs):
Original: "123456"
Hashed: "$2a$10$7RZ8VZX4HzY2kJl5NqLQ9.N9f8m7K6p3X2w1V0u9T8s7R6QpO5"
```

---

## ⚙️ CONFIGURACIÓN

### **Android - AndroidManifest.xml**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<application android:networkSecurityConfig="@xml/network_security_config">
```

### **Android - RetrofitClient.kt**
```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"
// Para dispositivo: "http://192.168.x.x:3000/"
```

### **Backend - .env**
```
PORT=3000
DB_HOST=db
DB_PORT=5432
DB_NAME=nutrical
DB_USER=nutrical_user
DB_PASSWORD=nutrical_password
JWT_SECRET=your-super-secret-jwt-key-change-in-production
DATABASE_URL="postgresql://nutrical_user:nutrical_password@db:5432/nutrical?schema=public"
```

---

## ✅ CHECKLIST PARA EMPEZAR

```
PRE-REQUISITOS:
[ ] Java 11+ instalado
[ ] Android Studio actualizado
[ ] Docker Desktop corriendo (para backend)
[ ] Git (opcional)

SETUP:
[ ] Backend: docker-compose up --build
[ ] Verifica: curl http://localhost:3000/health
[ ] Android: Build → Make Project
[ ] Emulador: Run app

TESTING:
[ ] Login funcionando ✓
[ ] Registro funcionando ✓
[ ] Sesión persistente ✓
[ ] Logout funcionando ✓
[ ] Tokenmgmt verificado ✓
```

---

##  RESUMEN FINAL

**LO QUE ESTÁ HECHO:**
- ✅ Integración API (Retrofit)
- ✅ Authentication (JWT)
- ✅ persistencia de sesión (SharedPreferences)
- ✅ Login/Registro con validaciones
- ✅ Manejo de errores
- ✅ UI profesional

**PRÓXIMOS PASOS:**
- Implementar endpoints adicionales del backend
- Agregar funcionalidad a HomeActivity
- Crear pantallas de perfil, configuración, etc.
- Implementar refresh tokens
- Agregar más seguridad (HTTPS en prod)

---

*¡PROYECTO LISTO PARA USAR! *
