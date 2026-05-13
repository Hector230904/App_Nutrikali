# ⚡ GUÍA RÁPIDA - EMPEZAR EN 1 MINUTO

##  START HERE

### **Paso 1: Backend (Terminal)**
```powershell
# Asume que tienes docker-compose.yml
cd nutrikali-backend
docker-compose up --build

# O si tienes Node.js localmente:
npm install
npx prisma db push
npm run dev
```
✅ Backend debe estar en: `http://localhost:3000/health`

### **Paso 2: Android Studio**
```
1. Abre Android Studio
2. File → Open → C:\Users\hesto\AndroidStudioProjects\NutrikaliApp
3. Wait for indexing...
```

### **Paso 3: Compilar**
```
Build → Make Project (Ctrl+F9)
Espera a que compile...
```

### **Paso 4: Correr**
```
Run → Select Device (emulador o dispositivo)
Espera a que instale la app...
```

### **Paso 5: Probar**
```
EMAIL: test@example.com
PASS: 123456
Click: Ingresar ✓

O click "Registrarse" para crear cuenta nueva
```

---

##  ARCHIVOS CREADOS

```
✅ models/AuthModels.kt
✅ network/ApiService.kt
✅ network/RetrofitClient.kt
✅ utils/TokenManager.kt
✅ res/layout/dialog_register.xml
✅ MainActivity.kt (ACTUALIZADO)
✅ AndroidManifest.xml (ACTUALIZADO)
```

---

##  QUÉ FUNCIONA

| Feature | Status |
|---------|--------|
| Login | ✅ |
| Registro | ✅ |
| Sesión Persistente | ✅ |
| JWT en SharedPreferences | ✅ |
| Logout | ✅ |
| Validaciones | ✅ |
| Manejo de Errores | ✅ |

---

##  PROBLEMAS COMUNES

### ❌ "Cannot connect to 10.0.2.2:3000"
- ✅ Verifica que el backend está corriendo
- ✅ En emulador: siempre usa `10.0.2.2` (no localhost)
- ✅ En dispositivo: modifica `RetrofitClient.kt` con tu IP local

### ❌ "Build failed"
- ✅ Limpia: `Build → Clean Project`
- ✅ Recompila: `Build → Make Project`
- ✅ Verifica Java 11+

### ❌ "Permission denied (INTERNET)"
- ✅ Limpia: `Build → Clean Project`
- ✅ Reinstala app
- ✅ Verifica `AndroidManifest.xml` tiene permisos

---

##  DOCUMENTACIÓN

| Archivo | Qué contiene |
|---------|-------------|
| `SETUP_AUTH_API.md` | Instrucciones detalladas |
| `VERIFICACION_CHECKLIST.md` | Items completados |
| `ESTRUCTURA_PROYECTO.md` | Diagramas y flujos |
| `RESUMEN_IMPLEMENTACION.md` | Resumen completo |

---

## ✨ NEXT STEPS

1. Verifica que login/registro funciona ✓
2. Lee `SETUP_AUTH_API.md` para entender la arquitectura
3. Implementa endpoints que necesites en el backend
4. Agrega más funcionalidad a HomeActivity

---

**¡Listo! **

*Para más ayuda, abre documentación en el repo*
