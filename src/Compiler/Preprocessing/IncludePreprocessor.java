package Compiler.Preprocessing;

/**
 *
 */
public class IncludePreprocessor {
    String file;

    /**
     * Konštruktor na nastavenie obsahu súboru.
     * @param file obsah súboru
     */
    public IncludePreprocessor(String file) {
        this.file = file;
    }

    /**
     * Funkcia na zistenie, či súbor obsahuje aj inú knižnicu ako sú štandardné.
     * @return true, ak obsahuje len štandardné knižnice
     *         false, ak obsahuje aj študentom vytvorené knižnice.
     */
    public boolean process() {
        String[] lines = file.split("\n");

        for (String line : lines) {
            if (!line.trim().equals("") && line.trim().charAt(0) == '#' && line.contains("include")) {
                if (!preprocessInclude(line)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Funkcia na zistenie, či ide o štandardnú knižnicu.
     * @param line riadok s #include a s knižnicou
     * @return true, ak ide o štandardnú knižnicu inak false
     */
    private boolean preprocessInclude(String line) {
        //zmaže medzery
        String temp = line.replaceAll(" ", "");

        StringBuilder word = new StringBuilder("");
        for (int i = 9; i <temp.length(); i++) {
            if (Character.isLetterOrDigit(temp.charAt(i)) || temp.charAt(i) == '_' || temp.charAt(i) == '.') {
                word.append(temp.charAt(i));
            }
        }

        switch (word.toString()) {
            case "assert.h":
            case "complex.h":
            case "ctype.h":
            case "errno.h":
            case "fenv.h":
            case "float.h":
            case "inttypes.h":
            case "iso646.h":
            case "limits.h":
            case "locale.h":
            case "math.h":
            case "setjmp.h":
            case "signal.h":
            case "stdalign.h":
            case "stdarg.h":
            case "stdatomic.h":
            case "stdbool.h":
            case "stddef.h":
            case "stdint.h":
            case "stdio.h":
            case "stdlib.h":
            case "stdnoreturn.h":
            case "string.h":
            case "tgmath.h":
            case "threads.h":
            case "time.h":
            case "uchar.h":
            case "wchar.h":
            case "wctype.h":
                return true;
            default:
                return false;
        }
    }
}
