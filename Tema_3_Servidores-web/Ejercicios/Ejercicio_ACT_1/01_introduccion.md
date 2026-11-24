# Servidor web Apache

## Instalación, configuración y HTTPS en Ubuntu

**Memoria**

Noviembre de 2026

---

# Servidor web Apache: instalación, configuración y HTTPS en Ubuntu

## Resumen

Esta memoria recoge el trabajo realizado con el servidor web Apache sobre Ubuntu, dentro de una máquina virtual. Primero se explica qué es Apache, de dónde viene y qué alternativas existen. Después se documenta la configuración de una página web personal servida desde un VirtualHost propio, con su nombre resuelto en local mediante el archivo `/etc/hosts` y comprobada desde el navegador. Por último se añade HTTPS: activación de los módulos `ssl` y `headers`, creación de un certificado autofirmado, VirtualHost en el puerto 443 y redirección permanente desde HTTP. Las pruebas confirman que el sitio responde por HTTPS y que las peticiones HTTP se redirigen al sitio seguro. También se comentan las limitaciones encontradas, como el aviso del nombre de servidor y el hecho de que un certificado autofirmado no es de confianza para los navegadores.

## Palabras clave

Apache, Ubuntu, servidor web, VirtualHost, HTTPS, SSL/TLS, certificado autofirmado, redirección

## Índice

1. [Introducción](#1-introducción)
   1. [Contexto](#11-contexto)
   2. [Alternativas a Apache](#12-alternativas-a-apache)
   3. [Motivación](#13-motivación)
2. [Configuración de un sitio web con VirtualHost](../Ejercicio_ACT_2/02_configuracion.md)
3. [Implementación de HTTPS](../Ejercicio_ACT_3/03_https.md)
4. [Bibliografía](#4-bibliografía)

---

## 1. Introducción

### 1.1 Contexto

El trabajo se ha realizado de forma individual en una máquina virtual con Ubuntu. El servidor no se publica en Internet: solo se usa desde la propia máquina, lo que basta para practicar y comprobar cada paso. La versión de Apache instalada es la 2.4.58, la que ofrece Ubuntu en sus repositorios.

#### Qué es Apache

Apache HTTP Server, o simplemente Apache, es un servidor web de código abierto y multiplataforma. Un servidor web es un programa que espera peticiones de los clientes (normalmente navegadores) y responde enviándoles páginas, archivos o el resultado de una aplicación. La comunicación se hace con el protocolo HTTP, o con HTTPS cuando va cifrada.

Se distribuye con licencia Apache 2.0 y lo mantiene la Apache Software Foundation.

#### Para qué sirve

Apache sirve para publicar sitios web, tanto estáticos (HTML, CSS, imágenes) como dinámicos, por ejemplo con PHP. Su punto fuerte es que es muy modular: se le pueden activar o desactivar módulos según lo que haga falta, como `ssl` para el cifrado, `rewrite` para reescribir direcciones, `headers` para controlar las cabeceras HTTP o `proxy` para funcionar como proxy inverso. Además, con los llamados hosts virtuales (VirtualHost) permite alojar varios sitios distintos en una misma máquina, cada uno con su nombre, su carpeta y sus registros.

#### Historia

A principios de 1995 el servidor web más usado era el NCSA HTTPd, desarrollado por Rob McCool en el Centro Nacional de Aplicaciones de Supercomputación (NCSA) de la Universidad de Illinois. Cuando McCool dejó el centro, el desarrollo se estancó y un grupo de webmasters empezó a intercambiar parches para corregir fallos y añadir mejoras. Tomando como base la versión 1.3 de NCSA httpd, reunieron todas esas correcciones y publicaron en abril de 1995 la primera versión pública, la 0.6.2. El nombre se suele explicar como «a patchy server» (un servidor «parcheado»), aunque también hay quien lo relaciona con el pueblo apache.

La versión 1.0 salió el 1 de diciembre de 1995 y en menos de un año superó a NCSA httpd como servidor más usado de Internet. En 1999 los miembros del grupo fundaron la Apache Software Foundation, una organización sin ánimo de lucro que desde entonces da soporte organizativo y legal al proyecto. La serie 2.0, con una arquitectura más modular, apareció en 2002, y la 2.4, publicada en 2012, es la rama que se mantiene hoy y la que instala Ubuntu.

### 1.2 Alternativas a Apache

Apache no es la única opción. Estas son las más conocidas:

| Servidor | Descripción |
| --- | --- |
| **Nginx** | Servidor web y proxy inverso creado por Igor Sysoev y publicado en 2004. Está diseñado para atender muchas conexiones a la vez con poco consumo de memoria, por lo que se usa mucho como proxy y balanceador de carga. |
| **Microsoft IIS** | Servidor web de Microsoft incluido en Windows Server. Es la opción habitual cuando se trabaja con ASP.NET. |
| **LiteSpeed** | Servidor de alto rendimiento compatible con la configuración de Apache. Tiene una versión de pago y otra de código abierto, OpenLiteSpeed. |
| **Caddy** | Servidor moderno escrito en Go. Destaca porque activa HTTPS de forma automática con certificados de Let's Encrypt y tiene una configuración muy corta. |
| **Lighttpd** | Servidor ligero pensado para equipos con pocos recursos. |

### 1.3 Motivación

El objetivo es entender cómo se publica una página web desde un servidor propio: dónde se guardan los archivos del sitio, cómo se separan varios sitios con VirtualHost, cómo se activan los cambios y se comprueba que la configuración es correcta, y cómo se protege la conexión con HTTPS.

Se ha elegido Apache porque tiene documentación abundante y una organización de la configuración muy clara en Ubuntu (carpetas `sites-available` y `sites-enabled`, y comandos como `a2ensite` o `a2enmod`), lo que ayuda a aprender cada pieza por separado. La parte práctica se divide en dos documentos: primero la configuración de un sitio web personal por HTTP y después la implementación de HTTPS sobre ese mismo sitio.

---

## 4. Bibliografía

- The Apache Software Foundation. *About the Apache HTTP Server Project.* https://httpd.apache.org/ABOUT_APACHE.html
- The Apache Software Foundation. *ASF History Project: Timeline.* https://apache.org/history/timeline.html
- The Apache Software Foundation. *Apache HTTP Server Project.* https://httpd.apache.org/
- Nginx. *nginx.* https://nginx.org/
- Microsoft. *Internet Information Services (IIS).* https://www.iis.net/
- LiteSpeed Technologies. *LiteSpeed Web Server.* https://www.litespeedtech.com/
- Caddy. *Caddy Web Server.* https://caddyserver.com/
- lighttpd. *lighttpd web server.* https://www.lighttpd.net/
