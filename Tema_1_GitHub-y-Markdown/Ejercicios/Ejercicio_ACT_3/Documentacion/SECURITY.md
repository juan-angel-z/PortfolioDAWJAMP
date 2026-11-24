# SECURITY.md

## Seguridad y accesibilidad del repositorio

**Proyecto:** Gestor de tareas (Java) con documentación generada automáticamente
**Repositorio:** `https://github.com/<ORGANIZACION>/<REPO>` *(sustituir por la URL real)*
**Fecha:** noviembre de 2026

---

## Índice

1. [Protección de datos sensibles](#1-protección-de-datos-sensibles)
2. [Accesibilidad del repositorio y permisos](#2-accesibilidad-del-repositorio-y-permisos)
3. [Buenas prácticas de seguridad](#3-buenas-prácticas-de-seguridad)
4. [Recuperación ante pérdida de datos o errores graves](#4-recuperación-ante-pérdida-de-datos-o-errores-graves)
5. [Qué hacer si se sube un secreto por error](#5-qué-hacer-si-se-sube-un-secreto-por-error)
6. [Evidencias](#6-evidencias)

---

## 1. Protección de datos sensibles

El repositorio contiene un programa Java (`codigo/src/gestortareas`), un script para generar la documentación (`generar_documentacion.sh`), una herramienta propia (`herramientas/MarkdownDoclet.java`) y la documentación generada en HTML, Markdown y PDF. Tras revisar el proyecto, **no contiene contraseñas ni claves**. Aun así, el archivo `.gitignore` se ha configurado de forma preventiva, porque un repositorio vive mucho más que el proyecto actual y un secreto subido una vez queda en el historial.

### 1.1 Qué se excluye y por qué

| Categoría | Patrones del `.gitignore` | Motivo de la exclusión |
|---|---|---|
| **Credenciales y secretos** | `.env`, `*.pem`, `*.key`, `*.jks`, `id_rsa`, `credentials.*`, `secrets.*` | Contraseñas, tokens y claves privadas dan acceso a servicios o servidores. Si se suben, cualquiera con acceso de lectura (o cualquier persona, si el repo es público) puede usarlos. Los bots rastrean GitHub en busca de ellos en cuestión de minutos. |
| **Configuración local** | `config.local.*`, `local.properties`, `application-local.*` | Contiene rutas, usuarios o ajustes propios de cada máquina. Subirla provoca conflictos entre compañeros y puede filtrar datos personales (nombre de usuario, estructura de carpetas). |
| **Binarios y compilados** | `*.class`, `*.jar`, `build/`, `out/`, `target/`, `bin/` | Se regeneran al compilar (el script ya ejecuta `javac` y borra `build/`). Aumentan el tamaño del repositorio, no se pueden revisar ni comparar con `diff` y pueden contener código que no coincide con las fuentes. |
| **Archivos de IDE** | `.idea/`, `*.iml`, `.vscode/`, `.classpath`, `.project`, `.settings/` | Dependen de cada desarrollador y de su versión del IDE. Generan conflictos continuos y pueden incluir rutas absolutas o preferencias personales. |
| **Temporales y logs** | `*.log`, `*.tmp`, `*.bak`, `*.swp`, `*~`, `hs_err_pid*` | No aportan valor al proyecto. Los logs y volcados de error de Java pueden incluir datos internos, rutas y variables de entorno. |
| **Ficheros del sistema** | `.DS_Store`, `Thumbs.db`, `desktop.ini` | Los crean macOS y Windows automáticamente; son ruido y revelan la estructura de carpetas local. |
| **Evidencias con datos personales** | `evidencias/privado/` | Las capturas en bruto pueden mostrar correos, tokens o datos de cuentas. Se guardan fuera del repositorio o se tapan antes de subirlas. |

### 1.2 Qué NO se excluye (decisión consciente)

La carpeta `documentacion/` (HTML, Markdown y PDF) **sí se versiona**. Aunque se puede regenerar con `generar_documentacion.sh`, es parte de la entrega del curso y debe estar disponible para quien revise el repositorio sin necesidad de instalar el JDK, `pandoc` ni `wkhtmltopdf`. En un proyecto real con integración continua se ignoraría y se publicaría como artefacto.

También se incluye la excepción `!.env.example`: una plantilla **sin valores reales** que documenta qué variables necesita el proyecto.

### 1.3 Limitación importante

`.gitignore` **solo evita que se añadan archivos nuevos**. No elimina archivos que ya estén rastreados ni borra el historial. Si un archivo sensible ya se subió, ver la sección [5](#5-qué-hacer-si-se-sube-un-secreto-por-error).

Comprobación de que las reglas funcionan:

```bash
git status --ignored          # lista lo que Git está ignorando
git check-ignore -v .env      # indica qué regla ignora un archivo concreto
```

---

## 2. Accesibilidad del repositorio y permisos

### 2.1 Configuración

En GitHub, los roles de acceso granulares (Read, Triage, Write, Maintain, Admin) solo existen en repositorios de una **organización**. En un repositorio personal solo hay un tipo de colaborador, con permisos de escritura. Por eso el repositorio se alojó en una organización gratuita creada con la cuenta A, y la cuenta B actuó como compañero.

Pasos seguidos:

1. Crear la organización (Perfil → *Your organizations* → *New organization* → plan *Free*).
2. Subir el proyecto al repositorio de la organización (`git remote add origin …`, `git push -u origin main`).
3. Ir a *Settings → Collaborators and teams → Add people* e invitar a la cuenta B.
4. Aceptar la invitación desde la cuenta B.
5. Repetir con cada rol (Read, Write, Admin), cambiándolo desde la lista de colaboradores.

### 2.2 Pruebas realizadas y resultados

> **Completar con lo que ocurrió realmente.** La columna «Resultado esperado» indica lo normal en GitHub; si algo difiere en la práctica, anotar lo observado.

| Rol | Prueba realizada con la cuenta B | Resultado esperado | Resultado observado | Evidencia |
|---|---|---|---|---|
| **Read** | `git clone` del repositorio | Funciona | *(completar)* | `evidencias/02-read-clone.png` |
| **Read** | `git push origin rama-prueba` | Rechazado: `remote: Permission to … denied` / error 403 | *(completar)* | `evidencias/03-read-push-403.png` |
| **Write** | `git push` de una rama nueva | Funciona | *(completar)* | `evidencias/04-write-push-ok.png` |
| **Write** | Entrar en *Settings* del repositorio | No aparece la pestaña *Settings* | *(completar)* | `evidencias/05-write-sin-settings.png` |
| **Write** | `git push` directo a `main` protegida | Rechazado por la regla de protección | *(completar)* | `evidencias/06-write-main-protegida.png` |
| **Admin** | Entrar en *Settings* y ver ajustes, colaboradores y ramas | Acceso completo | *(completar)* | `evidencias/07-admin-settings.png` |

### 2.3 Qué permite cada rol

| Rol | Puede | No puede |
|---|---|---|
| **Read** | Ver, clonar, abrir *issues* y comentar | Subir cambios |
| **Triage** | Gestionar *issues* y *pull requests* (etiquetar, asignar, cerrar) | Subir código |
| **Write** | Subir ramas, crear y fusionar *pull requests* (según reglas de rama) | Cambiar ajustes, borrar el repositorio |
| **Maintain** | Gestionar el repositorio sin acciones destructivas | Acciones sensibles: borrar o transferir el repo, gestionar secretos y claves |
| **Admin** | Todo: ajustes, colaboradores, reglas de rama, secretos, borrar o transferir | Nada relevante |

### 2.4 Qué nivel de permiso es más adecuado para cada rol de equipo

Se aplica el **principio de mínimo privilegio**: cada persona recibe el permiso mínimo que necesita para su trabajo, y se amplía solo cuando haga falta.

| Rol en el equipo | Permiso recomendado | Justificación |
|---|---|---|
| Profesor / revisor / auditor | **Read** | Solo necesita ver y descargar el trabajo. No debe poder modificarlo. |
| Cliente o persona externa que da *feedback* | **Read** o **Triage** | Puede abrir *issues* con sugerencias sin tocar el código. |
| Responsable de documentación / QA | **Triage** | Gestiona *issues* y seguimiento de errores sin riesgo para el código. |
| Desarrollador del equipo | **Write** | Necesita subir ramas y abrir *pull requests*. Con ramas protegidas no puede modificar `main` directamente. |
| Líder técnico / coordinador | **Maintain** | Gestiona ramas, etiquetas y *releases* sin poder borrar el repositorio. |
| Propietario / responsable del proyecto | **Admin** | Debe haber **pocos administradores (1 o 2)**: pueden borrar el repositorio, cambiar protecciones y gestionar secretos. |
| Colaborador ocasional o temporal | **Read** (o **Write** con caducidad) | Se retira el acceso cuando termina su tarea. |

Recomendaciones adicionales:

- Usar **equipos** (*Teams*) de la organización en lugar de permisos individuales: es más fácil de auditar.
- Revisar periódicamente *Settings → Collaborators and teams* y retirar a quien ya no participe.
- No compartir nunca una cuenta entre varias personas: cada colaborador usa la suya.

---

## 3. Buenas prácticas de seguridad

### 3.1 Ramas protegidas

La rama `main` contiene la versión válida del proyecto y debe protegerse (*Settings → Branches → Add branch ruleset / protection rule*):

- **Exigir *pull request* antes de fusionar**: nadie sube directamente a `main`.
- **Exigir al menos 1 aprobación** de otra persona.
- **Descartar aprobaciones obsoletas** cuando se suben nuevos *commits*.
- **Exigir que las conversaciones estén resueltas** antes de fusionar.
- **Bloquear el *force push*** y el **borrado** de la rama (evita reescribir o perder el historial).
- **Exigir historial lineal** o *commits* firmados, si el equipo lo decide.
- Aplicar las reglas también a los administradores («*Do not allow bypassing*»).

> Nota: en repositorios privados con plan gratuito, algunas de estas reglas pueden no estar disponibles. En ese caso, se usa un repositorio público o se documenta la limitación. *(Indicar aquí qué ocurrió en la práctica.)*

### 3.2 Revisión de *pull requests*

- Todo cambio entra mediante una rama (`feature/…`, `docs/…`, `fix/…`) y un *pull request*.
- El autor **no aprueba su propio PR**; la revisión la hace otra persona.
- El revisor comprueba: que no haya secretos ni archivos innecesarios, que el código compile, que la documentación siga siendo coherente y que el cambio haga lo que describe.
- Se usa un archivo `CODEOWNERS` para que ciertas rutas (por ejemplo `documentacion/` o `SECURITY.md`) requieran la revisión de una persona concreta.
- Los PR son pequeños y con una descripción clara, para que se puedan revisar bien.

### 3.3 Protección de la documentación

- La documentación vive en el mismo repositorio que el código, así que sigue las mismas reglas de rama y revisión.
- El historial de Git permite saber **quién cambió qué y cuándo** (`git log`, `git blame`).
- Se evitan datos personales en la documentación y en las capturas (tapar correos, tokens y nombres si no hacen falta).
- Los documentos generados se pueden reconstruir desde las fuentes con `generar_documentacion.sh`, lo que permite verificar que no han sido alterados a mano.

### 3.4 Seguridad de las cuentas

- Activar la **autenticación en dos pasos (2FA)** en todas las cuentas, y exigirla a nivel de organización.
- Preferir **SSH o tokens personales de acceso con permisos mínimos y caducidad** en lugar de contraseñas.
- Activar en *Settings → Code security* el **escaneo de secretos** (*secret scanning*), la **protección de subida** (*push protection*) y **Dependabot**, cuando el plan lo permita.
- Valorar **firmar los *commits*** (GPG o SSH) para garantizar la autoría.

### 3.5 Copias de seguridad

GitHub no sustituye a una copia de seguridad: si se borra el repositorio o se reescribe el historial, también se pierde lo que había en él. Se aplica la regla **3-2-1**: 3 copias, en 2 soportes distintos, 1 fuera del lugar principal.

| Copia | Cómo | Frecuencia |
|---|---|---|
| Clon local de cada colaborador | `git clone` (cada clon contiene todo el historial) | Continua |
| Espejo completo | `git clone --mirror <url> repo.git` y `git remote update` | Semanal o antes de cambios grandes |
| Archivo comprimido externo | `git bundle create repo.bundle --all` guardado en otro disco o nube | Semanal / en cada entrega |
| Segundo remoto | `git remote add backup <url-otro-servicio>` + `git push --mirror backup` (GitLab, Bitbucket…) | Automatizable |
| *Releases* con etiqueta | `git tag -a v1.0 -m "Entrega"` y *release* en GitHub | En cada entrega |

---

## 4. Recuperación ante pérdida de datos o errores graves

### 4.1 Escenarios y cómo se resuelven

| Situación | Solución |
|---|---|
| Se borró un archivo, aún sin *commit* | `git restore <archivo>` |
| Se borró un archivo y ya se hizo *commit* | `git restore --source=<commit> <archivo>` |
| *Commit* erróneo ya publicado en `main` | `git revert <commit>` (crea un commit inverso y **no reescribe** el historial) |
| *Commit* erróneo solo local | `git reset --soft HEAD~1` (conserva los cambios) |
| «Se perdió» un *commit* o rama tras un `reset` o `rebase` | `git reflog` localiza el commit; `git branch recuperada <hash>` lo recupera |
| Rama borrada por error | Con `git reflog` en local, o desde la pestaña *Branches → Deleted* / el PR cerrado en GitHub («Restore branch») |
| Alguien hizo *force push* y destruyó historial | Restaurar desde cualquier clon completo: `git push --force-with-lease origin <rama>`; o desde el espejo/bundle |
| Versión anterior funcional | `git checkout v1.0` o `git switch -c rescate v1.0` (por las etiquetas) |
| Repositorio de GitHub borrado | Restaurable desde *Settings → Repositories → Deleted repositories* (en cuentas personales, durante 90 días, y solo si no tiene forks); si no, desde el espejo: `git push --mirror <url-nueva>` |
| Pérdida total del equipo local | `git clone` desde GitHub |

### 4.2 Procedimiento de restauración completa desde copia espejo

```bash
# 1. Crear un repositorio vacío nuevo en GitHub (sin README ni .gitignore)
# 2. Restaurar desde el espejo o desde el bundle
cd repo.git
git push --mirror https://github.com/<ORGANIZACION>/<REPO-NUEVO>.git

# O desde un bundle
git clone repo.bundle repo-restaurado
cd repo-restaurado
git remote set-url origin https://github.com/<ORGANIZACION>/<REPO-NUEVO>.git
git push --all origin && git push --tags origin
```

> Los *issues*, los *pull requests* y los ajustes de permisos **no** viajan con el clon de Git. Para conservarlos habría que exportarlos con la API de GitHub o con `gh`. Por eso las reglas de protección y los roles quedan documentados en este mismo archivo.

### 4.3 Por qué las ramas protegidas ayudan a la recuperación

Bloquear *force push* y borrado de `main` evita la mayoría de los desastres, y exigir *pull requests* deja un registro de cada cambio que se puede revertir con `git revert` sin tocar el historial.

---

## 5. Qué hacer si se sube un secreto por error

1. **Revocar y cambiar el secreto de inmediato.** Es lo primero y lo único realmente seguro: asumir que ya está comprometido, aunque se borre después.
2. Dejar de rastrear el archivo, sin borrarlo del disco local:
   ```bash
   git rm --cached ruta/al/archivo
   echo "ruta/al/archivo" >> .gitignore
   git commit -m "Dejar de versionar archivo sensible"
   ```
3. Si hay que limpiar el historial, usar `git filter-repo` (o BFG Repo-Cleaner) y hacer `force push`, avisando antes al equipo, que tendrá que volver a clonar.
4. Pedir a GitHub que purgue las cachés si el dato era muy grave, y revisar los registros de acceso del servicio afectado.

---

## 6. Evidencias

Las capturas se guardan en la carpeta `evidencias/` del repositorio (sin datos personales visibles).

| N.º | Archivo | Qué demuestra |
|---|---|---|
| 1 | `evidencias/01-git-log-remote.png` | Historial (`git log --oneline`) y remoto (`git remote -v`) del repositorio |
| 2 | `evidencias/02-invitacion-read.png` | Invitación a la cuenta B con rol **Read** |
| 3 | `evidencias/02-read-clone.png` | La cuenta B con Read clona correctamente |
| 4 | `evidencias/03-read-push-403.png` | La cuenta B con Read no puede hacer `push` (error 403) |
| 5 | `evidencias/04-write-push-ok.png` | Con **Write**, `push` a una rama nueva correcto |
| 6 | `evidencias/05-write-sin-settings.png` | Con Write no aparece *Settings* |
| 7 | `evidencias/06-write-main-protegida.png` | `push` directo a `main` bloqueado por la protección |
| 8 | `evidencias/07-admin-settings.png` | Con **Admin** se accede a *Settings* |
| 9 | `evidencias/08-lista-colaboradores.png` | Lista de colaboradores con sus roles |
| 10 | `evidencias/09-proteccion-rama.png` | Regla de protección de `main` configurada |
| 11 | `evidencias/10-git-status-ignored.png` | `git status --ignored` y `git check-ignore -v` con el `.gitignore` funcionando |

---

## Conclusión

El repositorio se protege en tres capas: **qué se sube** (`.gitignore`, sin secretos ni binarios), **quién accede y con qué nivel** (mínimo privilegio, pocos administradores, 2FA) y **cómo se recupera** (ramas protegidas, copias espejo y conocimiento de `reflog` y `revert`). Estas medidas no dependen unas de otras, de modo que un fallo en una de ellas queda cubierto por las demás.
