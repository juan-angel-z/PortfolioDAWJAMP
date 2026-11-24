#!/usr/bin/env bash
# Genera la documentación del proyecto en HTML, Markdown y PDF.
# Requisitos: JDK 17 o superior (javac y javadoc). Para el PDF, además pandoc y wkhtmltopdf.
# Uso: ./generar_documentacion.sh   (desde la carpeta donde está este archivo)
set -e
cd "$(dirname "$0")"

echo "1/4 Compilando el código..."
rm -rf build && mkdir build
javac -Xlint:all -encoding UTF-8 -d build codigo/src/gestortareas/*.java

echo "2/4 Generando HTML con Javadoc..."
rm -rf documentacion/html && mkdir -p documentacion/html
javadoc -Xdoclint:all -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -locale es \
  -author -version -windowtitle "Gestor de tareas - API" -doctitle "Gestor de tareas" \
  -d documentacion/html -sourcepath codigo/src -subpackages gestortareas

echo "3/4 Generando Markdown con el doclet propio..."
rm -rf herramientas/build && mkdir herramientas/build
javac -encoding UTF-8 -d herramientas/build herramientas/MarkdownDoclet.java
mkdir -p documentacion/markdown
javadoc -doclet MarkdownDoclet -docletpath herramientas/build -encoding UTF-8 \
  -sourcepath codigo/src -subpackages gestortareas -salida documentacion/markdown/api.md

echo "4/4 Generando PDF a partir del Markdown..."
if command -v pandoc >/dev/null && command -v wkhtmltopdf >/dev/null; then
  mkdir -p documentacion/pdf
  pandoc documentacion/markdown/api.md -f gfm -t html5 -s --embed-resources \
    --metadata pagetitle="Gestor de tareas - API" -c herramientas/estilo.css -o build/api.html
  wkhtmltopdf --quiet --enable-local-file-access --page-size A4 \
    --margin-top 18mm --margin-bottom 18mm --margin-left 16mm --margin-right 16mm \
    build/api.html documentacion/pdf/api.pdf
else
  echo "   (se omite el PDF: faltan pandoc o wkhtmltopdf)"
fi

rm -rf build herramientas/build
echo "Listo. Abre documentacion/html/index.html en el navegador."
