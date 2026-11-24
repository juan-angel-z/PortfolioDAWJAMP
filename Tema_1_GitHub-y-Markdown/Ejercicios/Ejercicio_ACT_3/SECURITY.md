# SECURITY.md

## Seguridad y accesibilidad del repositorio

**Proyecto:** Gestor de tareas (Java) con documentación generada automáticamente
**Repositorio:** `https://github.com/PortfolioDAWSeguridad/Documentacion`
**Fecha:** noviembre de 2026

---

## Índice

1. [Protección de datos sensibles](#1-protección-de-datos-sensibles)
2. [Accesibilidad del repositorio y permisos](#2-accesibilidad-del-repositorio-y-permisos)
3. [Buenas prácticas de seguridad](#3-buenas-prácticas-de-seguridad)
4. [Recuperación ante pérdida de datos o errores graves](#4-recuperación-ante-pérdida-de-datos-o-errores-graves)
5. [Qué hacer si se sube un secreto por error](#5-qué-hacer-si-se-sube-un-secreto-por-error)
6. [Registro de lo realizado](#6-registro-de-lo-realizado)

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
| **Material con datos personales** | `evidencias/privado/` | Cualquier material en bruto (capturas, registros) puede mostrar correos, tokens o datos de cuentas, por lo que se guarda fuera del repositorio o se anonimiza antes de subirlo. |

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

En GitHub, los roles de acceso granulares (Read, Triage, Write, Maintain, Admin) solo existen en repositorios de una **organización**. En un repositorio personal solo hay un tipo de colaborador, con permisos de escritura. Por eso el repositorio se alojó en una organización gratuita (`PortfolioDAWSeguridad`) creada con la cuenta A, que es la propietaria. La cuenta B hizo de compañero y se añadió como miembro de la organización.

Pasos seguidos:

1. Crear la organización gratuita (*Your organizations → New organization → Free*).
2. Crear un repositorio vacío dentro de ella y subir el proyecto: `git init -b main`, `git add .`, `git commit`, `git remote add origin …`, `git push -u origin main`.
3. Añadir a la cuenta B a la organización (*People → Invite member*).
4. Con la cuenta B, clonar el repositorio y probar a subir una rama (`prueba-read`).

### 2.2 Prueba realizada y resultado observado

| Prueba con la cuenta B | Resultado |
|---|---|
| `git clone` del repositorio | Correcto |
| `git push origin prueba-read` (rama nueva) | **Se aceptó**, sin que se pidieran credenciales |

El `push` funcionó, y con un rol de solo lectura habría fallado. Por tanto, esta prueba **no demuestra el comportamiento del rol Read**, y no se pudo determinar con certeza la causa. Las dos explicaciones más probables son:

- **Credenciales almacenadas.** Git reutilizó las credenciales de la cuenta A (propietaria), que se habían guardado en el primer `push`, así que la operación no se hizo realmente con la cuenta B.
- **Permiso base de la organización.** Un *miembro* de la organización hereda el permiso base de la organización. Si este es Write, puede subir ramas aunque no se le haya asignado ningún rol concreto en el repositorio.

**Lección:** para probar un rol hay que (1) asignarlo de forma explícita en *Settings → Collaborators and teams*, (2) poner el permiso base de la organización en *No permission* o *Read* (*Organization settings → Member privileges*), y (3) autenticarse realmente como esa cuenta, usando un token personal propio y evitando las credenciales almacenadas (`git -c credential.helper= push …`).

El comportamiento esperado de cada rol, **según la documentación de GitHub y no comprobado en esta práctica**, es:

| Rol | Clonar | `push` a una rama | Acceder a *Settings* |
|---|---|---|---|
| **Read** | Sí | No (error 403: `Permission denied`) | No |
| **Write** | Sí | Sí (en `main` solo si no está protegida) | No |
| **Admin** | Sí | Sí | Sí, acceso completo |

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

> Nota: en repositorios privados con plan gratuito, algunas de estas reglas pueden no estar disponibles. En ese caso se usa un repositorio público. En esta práctica estas reglas se describen como configuración recomendada; no se llegaron a aplicar en el repositorio.

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
git push --mirror https://github.com/PortfolioDAWSeguridad/<REPO-NUEVO>.git

# O desde un bundle
git clone repo.bundle repo-restaurado
cd repo-restaurado
git remote set-url origin https://github.com/PortfolioDAWSeguridad/<REPO-NUEVO>.git
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

## 6. Registro de lo realizado

Resumen de lo que se hizo y se comprobó en la práctica:

1. **`.gitignore`:** se creó en la raíz del proyecto. Se comprobó con `git status --ignored` que un archivo de prueba `.env` queda ignorado, mientras que el código, el script y la carpeta `documentacion/` se rastrean con normalidad.
2. **Error corregido durante la práctica:** un primer `git init` se ejecutó por error en la carpeta `Downloads`. Se eliminó borrando su carpeta `.git` y se comprobó con `git status` que ya no era un repositorio. No se subió nada a GitHub desde ahí, y se repitió el proceso en la carpeta correcta del proyecto.
3. **Repositorio:** se creó el repositorio `PortfolioDAWSeguridad/Documentacion` y se subió la rama `main` (63 objetos, 171,52 KiB).
4. **Acceso de un compañero:** la cuenta B se añadió como miembro de la organización, clonó el repositorio y pudo subir una rama de prueba. Esto mostró que el acceso no estaba limitado a lectura y por qué hay que configurar con cuidado el permiso base y las credenciales (sección 2.2).
5. **Limpieza:** la rama de prueba `prueba-read` se borra del repositorio con `git push origin --delete prueba-read`.

---

## Conclusión

El repositorio se protege en tres capas: **qué se sube** (`.gitignore`, sin secretos ni binarios), **quién accede y con qué nivel** (mínimo privilegio, pocos administradores, 2FA) y **cómo se recupera** (ramas protegidas, copias espejo y conocimiento de `reflog` y `revert`). Estas medidas no dependen unas de otras, de modo que un fallo en una de ellas queda cubierto por las demás.
