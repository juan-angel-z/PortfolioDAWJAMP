# Política básica de seguridad

## Comunicación de vulnerabilidades

Si se detecta una vulnerabilidad o una exposición accidental de información, debe comunicarse al responsable del repositorio de forma privada, evitando publicar credenciales, tokens o datos personales en incidencias públicas.

## Protección del repositorio

- No subir contraseñas, claves privadas, tokens ni archivos `.env`.
- Mantener un `.gitignore` adecuado al lenguaje y a las herramientas utilizadas.
- Utilizar permisos mínimos necesarios para cada colaborador.
- Revisar los cambios mediante Pull Requests antes de incorporarlos a la rama principal.
- Activar la autenticación en dos pasos en las cuentas que tengan acceso.
- Revocar el acceso cuando un colaborador deje de participar.
- Mantener copias de seguridad y utilizar el historial de Git para recuperar versiones anteriores.

## Gestión de permisos de colaboradores

En GitHub, los permisos se gestionan desde la configuración del repositorio, en la sección de colaboradores y equipos. Antes de invitar a alguien conviene comprobar qué nivel de acceso necesita. No se debe conceder permiso de administración si solo necesita consultar o modificar el código.

Niveles habituales:
- **Read:** consultar y clonar el repositorio.
- **Triage:** gestionar incidencias y solicitudes sin escribir directamente en el código.
- **Write:** modificar el contenido y colaborar en el proyecto.
- **Maintain:** administrar parte de la gestión del repositorio.
- **Admin:** controlar toda la configuración y los accesos.

## Recuperación ante problemas

1. Identificar el archivo o cambio que ha causado el problema.
2. Consultar el historial de commits.
3. Restaurar la versión correcta o revertir el commit que introdujo el error.
4. Si se ha publicado un secreto, revocarlo o rotarlo inmediatamente. Borrarlo del último commit no garantiza que haya desaparecido del historial.
5. Revisar los permisos y los registros de actividad si se sospecha de un acceso no autorizado.

## Evidencias de configuración

La configuración de permisos debe comprobarse desde la interfaz de GitHub. En esta entrega no se incluyen capturas de una invitación o de una prueba real con otra cuenta; por tanto, no se afirma que se haya verificado un acceso de colaborador. Si la actividad exige esa evidencia, se debe añadir una captura propia de la pantalla de gestión de colaboradores y de los permisos asignados, ocultando correos u otros datos privados.
