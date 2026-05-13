#  NutriKali App - Integración con API Backend

## ✅ Estructura del Proyecto Creada

```
app/src/main/java/com/example/nutrikaliapp/
├── models/
│   └── AuthModels.kt              ← Modelos de login/registro
├── network/
│   ├── ApiService.kt              ← Interfaz de Retrofit
│   └── RetrofitClient.kt          ← Cliente HTTP configurado
├── utils/
│   └── TokenManager.kt            ← Manejo de JWT en SharedPreferences
├── MainActivity.kt                ← ACTUALIZADO: Login y Registro
├── activity_home.kt
└── ...

app/src/main/res/
├── layout/
│   ├── activity_main.xml
│   ├── activity_home.xml
│   └── dialog_register.xml        ← NUEVO: Diálogo de registro
├── xml/
│   └── network_security_config.xml
└── ...
```

##  Archivos Creados/Modificados

### 1. ✅ **models/AuthModels.kt** 
Modelos de datos para la comunicación con el API:
- `LoginRequest` - Email y contraseña para login
- `RegisterRequest` - Email, contraseña y nombre para registro
- `AuthResponse` - Respuesta con token y datos del usuario
- `UserData` - Información del usuario autenticado

### 2. ✅ **network/ApiService.kt**
Interfaz de Retrofit con dos endpoints:
- `POST /api/auth/login` - Inicia sesión
- `POST /api/auth/register` - Crea nueva cuenta

### 3. ✅ **network/RetrofitClient.kt**
Cliente HTTP preconfigurado:
- Base URL: `http://10.0.2.2:3000/` (emulador)
- Incluye logging OkHttp para debug
- Convertidor JSON con Gson

### 4. ✅ **utils/TokenManager.kt**
Gestor de tokens JWT:
- `saveToken(token)` - Guarda JWT en SharedPreferences
- `getToken()` - Recupera JWT
- `saveUserInfo(email, name)` - Guarda datos del usuario
- `getUserEmail()` - Obtiene email del usuario almacenado
- `isLoggedIn()` - Verifica si hay sesión activa
- `clearAll()` - Limpia todo al cerrar sesión

### 5. ✅ **MainActivity.kt** (ACTUALIZADO)
Pantalla de login con:
- ✅ Login con validaciones
- ✅ Registro con diálogo modal
- ✅ Verificación de sesión existente
- ✅ Manejo de errores de red
- ✅ Redirección a HomeActivity

### 6. ✅ **layout/dialog_register.xml** (NUEVO)
Diálogo modal para registro con campos:
- Email
- Contraseña
- Nombre (opcional)
- Botones Cancelar/Registrarse

### 7. ✅ **AndroidManifest.xml** (ACTUALIZADO)
Agregados permisos:
- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`
- Referencia a `network_security_config`

### 8. ✅ **build.gradle.kts** (Actualizado)
Dependencias agregadas:
- Retrofit 2.9.0
- OkHttp Logging Interceptor
- Kotlinx Coroutines
- Androidx Lifecycle

### 9. ✅ **res/xml/network_security_config.xml**
Permite tráfico HTTP en emulador:
- Dominio: `10.0.2.2:3000`

---

##  Cómo Usar

### 1. **Ejecutar el Backend**
```bash
cd nutrikali-backend/
docker-compose up --build
```
O si tienes Node.js localmente:
```bash
npm install
npx prisma db push
npm run dev
```

### 2. **Compilar Android**
En Android Studio:
```bash
Build → Make Project
```

### 3. **Ejecutar en Emulador/Dispositivo**
- Si es **emulador**: Se conectará a `10.0.2.2:3000` automáticamente
- Si es **dispositivo físico**: Modifica `RetrofitClient.kt` con tu IP local:
```kotlin
private const val BASE_URL = "http://192.168.x.x:3000/"  // Reemplaza con tu IP
```

---

##  Flujo de Autenticación

### Login
```
1. Usuario ingresa email + contraseña
2. MainActivity valida inputs
3. Retrofit → POST /api/auth/login
4. Backend verifica credenciales en PostgreSQL
5. Devuelve JWT token + userData
6. TokenManager guarda token en SharedPreferences
7. Redirige a HomeActivity
```

### Registro
```
1. Usuario hace click en "Registrarse"
2. Se abre diálogo modal
3. Valida: email, contraseña (min 6 chars)
4. Retrofit → POST /api/auth/register
5. Backend hashea password con bcryptjs
6. Guarda usuario en BD PostgreSQL
7. Devuelve JWT token
8. TokenManager guarda token
9. Redirige a HomeActivity
```

### Verificación de Sesión
```
- Al abrir MainActivity, verifica TokenManager.isLoggedIn()
- Si hay token válido → directo a HomeActivity
- Si no → muestra pantalla de login
```

---

##  Prueba Rápida

### Usuarios de prueba
Si tienes datos en la BD, úsalos. Si no, registra uno nuevo:

1. Abre la app
2. Haz click en "Registrarse"
3. Ingresa:
   - Email: `test@nutrikali.com`
   - Contraseña: `123456`
   - Nombre: `Juan`
4. Click "Registrarse"
5. Debería redirigirse a HomeActivity ✅

---

##  Debugging

### Ver logs de Retrofit
- En logcat, busca: `OkHttp` o `HttpLogging`
- Muestra request/response completos

### Usuario no se loguea
1. Verifica que el backend está corriendo: `curl http://localhost:3000/health`
2. Valida que los datos existan en la BD: `SELECT * FROM users;`
3. Comprueba que `network_security_config.xml` está en `res/xml/`

### Error "Permission Denied"
- En `AndroidManifest.xml`, verifica que tenga:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

##  Próximos Pasos

1. ✅ **Logout funcionando** - Click botón rojo en HomeActivity
2. ✅ **Token persistente** - Ya está en SharedPreferences
3. ⏳ **Actualizar perfil** - Crear endpoint PATCH `/api/auth/profile`
4. ⏳ **Recuperar contraseña** - Implementar flujo de reset
5. ⏳ **Refresh token** - Si tokens expiran

---

##  API Endpoints

| Método | Endpoint | Request | Response |
|--------|----------|---------|----------|
| POST | `/api/auth/register` | `{email, password, name?}` | `{token, user, message}` |
| POST | `/api/auth/login` | `{email, password}` | `{token, user, message}` |
| GET | `/health` | - | `{status: "OK"}` |

---

## ⚙️ Variables de Entorno (.env del Backend)

```env
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

¡Listo para usar! 
