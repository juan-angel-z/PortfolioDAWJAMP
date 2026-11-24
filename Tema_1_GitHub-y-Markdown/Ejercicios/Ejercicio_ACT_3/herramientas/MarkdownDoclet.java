import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.ParamTree;
import com.sun.source.doctree.ReturnTree;
import com.sun.source.doctree.ThrowsTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Doclet que genera la documentación de la API en un único archivo Markdown.
 * Lee los mismos comentarios Javadoc que usa el generador de HTML estándar.
 *
 * <p>Uso: {@code javadoc -doclet MarkdownDoclet -docletpath <carpeta> -salida api.md ...}</p>
 */
public class MarkdownDoclet implements Doclet {

    private Path salida = Path.of("api.md");
    private DocTrees trees;

    @Override
    public void init(Locale locale, Reporter reporter) {
        // No se necesita configuración adicional
    }

    @Override
    public String getName() {
        return "MarkdownDoclet";
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        Option opcion = new Option() {
            @Override
            public int getArgumentCount() {
                return 1;
            }

            @Override
            public String getDescription() {
                return "Archivo Markdown de salida";
            }

            @Override
            public Kind getKind() {
                return Kind.STANDARD;
            }

            @Override
            public List<String> getNames() {
                return List.of("-salida");
            }

            @Override
            public String getParameters() {
                return "archivo";
            }

            @Override
            public boolean process(String option, List<String> arguments) {
                salida = Path.of(arguments.get(0));
                return true;
            }
        };
        return Set.of(opcion);
    }

    @Override
    public boolean run(DocletEnvironment env) {
        trees = env.getDocTrees();
        StringBuilder md = new StringBuilder();

        List<PackageElement> paquetes = new ArrayList<>(ElementFilter.packagesIn(env.getIncludedElements()));
        paquetes.sort(Comparator.comparing(p -> p.getQualifiedName().toString()));
        List<TypeElement> tipos = new ArrayList<>(ElementFilter.typesIn(env.getIncludedElements()));
        tipos.sort(Comparator.comparing(t -> t.getSimpleName().toString()));

        for (PackageElement paquete : paquetes) {
            md.append("# Documentación de la API: `").append(paquete.getQualifiedName()).append("`\n\n");
            md.append(descripcion(paquete)).append("\n\n");
        }

        md.append("## Índice de clases\n\n");
        for (TypeElement tipo : tipos) {
            md.append("- [").append(tipo.getSimpleName()).append("](#")
                    .append(ancla(nombreTipo(tipo) + " " + tipo.getSimpleName())).append(") — ")
                    .append(primeraFrase(tipo)).append("\n");
        }
        md.append("\n---\n\n");

        for (TypeElement tipo : tipos) {
            escribirTipo(md, env, tipo);
        }

        try {
            if (salida.getParent() != null) {
                Files.createDirectories(salida.getParent());
            }
            Files.writeString(salida, md.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("No se pudo escribir " + salida + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    private void escribirTipo(StringBuilder md, DocletEnvironment env, TypeElement tipo) {
        md.append("## ").append(nombreTipo(tipo)).append(" `").append(tipo.getSimpleName()).append("`\n\n");
        md.append("```java\n").append(cabeceraTipo(tipo)).append("\n```\n\n");
        md.append(descripcion(tipo)).append("\n\n");
        escribirEtiquetas(md, tipo, false);

        List<VariableElement> constantes = new ArrayList<>();
        for (VariableElement v : ElementFilter.fieldsIn(tipo.getEnclosedElements())) {
            if (v.getKind() == ElementKind.ENUM_CONSTANT && env.isIncluded(v)) {
                constantes.add(v);
            }
        }
        if (!constantes.isEmpty()) {
            md.append("### Constantes\n\n| Constante | Descripción |\n| --- | --- |\n");
            for (VariableElement c : constantes) {
                md.append("| `").append(c.getSimpleName()).append("` | ")
                        .append(descripcion(c).replace("\n", " ")).append(" |\n");
            }
            md.append("\n");
        }

        List<ExecutableElement> constructores = new ArrayList<>();
        for (ExecutableElement c : ElementFilter.constructorsIn(tipo.getEnclosedElements())) {
            if (env.isIncluded(c)) {
                constructores.add(c);
            }
        }
        if (!constructores.isEmpty()) {
            md.append("### Constructores\n\n");
            for (ExecutableElement c : constructores) {
                escribirMetodo(md, c, true);
            }
        }

        List<ExecutableElement> metodos = new ArrayList<>();
        for (ExecutableElement m : ElementFilter.methodsIn(tipo.getEnclosedElements())) {
            if (env.isIncluded(m)) {
                metodos.add(m);
            }
        }
        if (!metodos.isEmpty()) {
            md.append("### Métodos\n\n");
            for (ExecutableElement m : metodos) {
                escribirMetodo(md, m, false);
            }
        }
        md.append("---\n\n");
    }

    private void escribirMetodo(StringBuilder md, ExecutableElement m, boolean constructor) {
        String nombre = constructor ? m.getEnclosingElement().getSimpleName().toString()
                : m.getSimpleName().toString();
        md.append("#### `").append(nombre).append("`\n\n");
        md.append("```java\n").append(firma(m, constructor)).append("\n```\n\n");
        String texto = descripcion(m);
        if (texto.isEmpty() && m.getEnclosingElement().getKind() == ElementKind.ENUM) {
            // Métodos que el compilador añade a todas las enumeraciones
            if (nombre.equals("values")) {
                md.append("Devuelve un array con todas las constantes de la enumeración, "
                        + "en el orden en que están declaradas.\n\n");
                return;
            }
            if (nombre.equals("valueOf")) {
                md.append("Devuelve la constante de la enumeración con el nombre indicado.\n\n")
                        .append("**Parámetros:**\n\n- `name`: nombre exacto de la constante\n\n")
                        .append("**Lanza:**\n\n- `IllegalArgumentException`: "
                                + "si no existe ninguna constante con ese nombre\n\n");
                return;
            }
        }
        md.append(texto).append("\n\n");
        escribirEtiquetas(md, m, true);
    }

    private void escribirEtiquetas(StringBuilder md, Element e, boolean detalle) {
        DocCommentTree dc = trees.getDocCommentTree(e);
        if (dc == null) {
            return;
        }
        List<String> params = new ArrayList<>();
        List<String> lanza = new ArrayList<>();
        String devuelve = null;
        String version = null;
        String desde = null;
        List<String> vease = new ArrayList<>();

        for (DocTree t : dc.getBlockTags()) {
            switch (t.getKind()) {
                case PARAM -> {
                    ParamTree p = (ParamTree) t;
                    params.add("- `" + p.getName() + "`: " + limpiar(unir(p.getDescription())));
                }
                case RETURN -> devuelve = limpiar(unir(((ReturnTree) t).getDescription()));
                case THROWS, EXCEPTION -> {
                    ThrowsTree th = (ThrowsTree) t;
                    lanza.add("- `" + th.getExceptionName() + "`: " + limpiar(unir(th.getDescription())));
                }
                case VERSION -> version = limpiar(t.toString().replace("@version", "").trim());
                case SINCE -> desde = limpiar(t.toString().replace("@since", "").trim());
                case SEE -> vease.add("`" + t.toString().replace("@see", "").trim() + "`");
                default -> { }
            }
        }
        if (!params.isEmpty()) {
            md.append("**Parámetros:**\n\n").append(String.join("\n", params)).append("\n\n");
        }
        if (devuelve != null) {
            md.append("**Devuelve:** ").append(devuelve).append("\n\n");
        }
        if (!lanza.isEmpty()) {
            md.append("**Lanza:**\n\n").append(String.join("\n", lanza)).append("\n\n");
        }
        if (!detalle) {
            if (version != null) {
                md.append("**Versión:** ").append(version).append("  \n");
            }
            if (desde != null) {
                md.append("**Desde:** ").append(desde).append("  \n");
            }
            if (!vease.isEmpty()) {
                md.append("**Véase también:** ").append(String.join(", ", vease)).append("  \n");
            }
            if (version != null || desde != null || !vease.isEmpty()) {
                md.append("\n");
            }
        }
    }

    private String descripcion(Element e) {
        DocCommentTree dc = trees.getDocCommentTree(e);
        if (dc == null) {
            return "";
        }
        return limpiar(unir(dc.getFullBody()));
    }

    private String primeraFrase(Element e) {
        DocCommentTree dc = trees.getDocCommentTree(e);
        if (dc == null) {
            return "";
        }
        return limpiar(unir(dc.getFirstSentence()));
    }

    private static String unir(List<? extends DocTree> partes) {
        return partes.stream().map(DocTree::toString).collect(Collectors.joining());
    }

    /** Convierte las etiquetas HTML y en línea de Javadoc más habituales a Markdown. */
    private static String limpiar(String s) {
        List<String> bloques = new ArrayList<>();
        Matcher m = Pattern.compile("(?s)<pre>\\s*\\{@code(.*?)\\}\\s*</pre>").matcher(s);
        StringBuilder sinCodigo = new StringBuilder();
        while (m.find()) {
            bloques.add(quitarSangria(m.group(1)));
            m.appendReplacement(sinCodigo,
                    Matcher.quoteReplacement("@@P@@@@CODIGO" + (bloques.size() - 1) + "@@@@P@@"));
        }
        m.appendTail(sinCodigo);
        s = sinCodigo.toString();

        s = s.replaceAll("\\{@code\\s+([^}]*)\\}", "`$1`");
        s = s.replaceAll("\\{@link(?:plain)?\\s+([^}\\s]+)(?:\\s+([^}]*))?\\}", "`$1`");
        s = s.replace("`#", "`");
        s = s.replace("<p>", "@@P@@").replace("</p>", "@@P@@");
        s = s.replaceAll("\\s+", " ");
        s = s.replaceAll("(?: ?@@P@@ ?)+", "\n\n");
        for (int i = 0; i < bloques.size(); i++) {
            s = s.replace("@@CODIGO" + i + "@@", "```java\n" + bloques.get(i) + "\n```");
        }
        return s.trim();
    }

    /** Quita las líneas vacías del principio y la sangría común de un bloque de código. */
    private static String quitarSangria(String codigo) {
        List<String> lineas = new ArrayList<>(List.of(codigo.split("\\R", -1)));
        while (!lineas.isEmpty() && lineas.get(0).isBlank()) {
            lineas.remove(0);
        }
        while (!lineas.isEmpty() && lineas.get(lineas.size() - 1).isBlank()) {
            lineas.remove(lineas.size() - 1);
        }
        int minimo = Integer.MAX_VALUE;
        for (String l : lineas) {
            if (!l.isBlank()) {
                minimo = Math.min(minimo, l.length() - l.stripLeading().length());
            }
        }
        final int quitar = minimo == Integer.MAX_VALUE ? 0 : minimo;
        return lineas.stream().map(l -> l.length() >= quitar ? l.substring(quitar) : l.strip())
                .collect(Collectors.joining("\n"));
    }

    /** Calcula el enlace interno que genera Markdown para un título. */
    private static String ancla(String titulo) {
        return titulo.toLowerCase(Locale.ROOT).replace(" ", "-");
    }

    private static String nombreTipo(TypeElement t) {
        if (t.getKind() == ElementKind.ENUM) {
            return "Enumeración";
        }
        if (t.getKind() == ElementKind.INTERFACE) {
            return "Interfaz";
        }
        String nombre = t.getSimpleName().toString();
        return nombre.endsWith("Exception") ? "Excepción" : "Clase";
    }

    private static String cabeceraTipo(TypeElement t) {
        String mods = t.getModifiers().stream().filter(m -> m != Modifier.STATIC
                        && !(t.getKind() == ElementKind.ENUM && m == Modifier.FINAL))
                .map(Modifier::toString).collect(Collectors.joining(" "));
        String tipo = switch (t.getKind()) {
            case ENUM -> "enum";
            case INTERFACE -> "interface";
            default -> "class";
        };
        String cabecera = (mods.isEmpty() ? "" : mods + " ") + tipo + " " + t.getSimpleName();
        TypeMirror padre = t.getSuperclass();
        if (t.getKind() == ElementKind.CLASS && !simple(padre).equals("Object")
                && !simple(padre).equals("none")) {
            cabecera += " extends " + simple(padre);
        }
        return cabecera;
    }

    private static String firma(ExecutableElement m, boolean constructor) {
        StringBuilder sb = new StringBuilder();
        String mods = m.getModifiers().stream().map(Modifier::toString).collect(Collectors.joining(" "));
        if (!mods.isEmpty()) {
            sb.append(mods).append(" ");
        }
        if (!constructor) {
            sb.append(simple(m.getReturnType())).append(" ");
        }
        sb.append(constructor ? m.getEnclosingElement().getSimpleName() : m.getSimpleName()).append("(");
        sb.append(m.getParameters().stream()
                .map(p -> simple(p.asType()) + " " + p.getSimpleName())
                .collect(Collectors.joining(", ")));
        sb.append(")");
        if (!m.getThrownTypes().isEmpty()) {
            sb.append(" throws ").append(m.getThrownTypes().stream()
                    .map(MarkdownDoclet::simple).collect(Collectors.joining(", ")));
        }
        return sb.toString();
    }

    /** Quita los nombres de paquete de un tipo para que la firma sea más legible. */
    private static String simple(TypeMirror t) {
        return t.toString().replaceAll("\\b(?:[a-z_][a-z0-9_]*\\.)+(?=[A-Z])", "");
    }
}
