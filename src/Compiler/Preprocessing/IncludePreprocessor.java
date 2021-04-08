package Compiler.Preprocessing;

/**
 * Trieda pre sprcovanie include v zdrojových kódoch.
 *
 * @author Ivan Vykopal
 */
public class IncludePreprocessor {
    /** Atribút file predstavuje analyzovaný súbor v textovej podobe. **/
    private String file;

    /**
     * Konštruktor na nastavenie obsahu súboru.
     *
     * @param file obsah súboru v textovej podobe
     */
    public IncludePreprocessor(String file) {
        this.file = file;
    }

    /**
     * Metóda na zistenie, či súbor obsahuje aj inú knižnicu ako sú štandardné knižnice.
     *
     * @return prázdny reťazec, ak obsahuje len štandardné knižnice
     *         názov nepodporovanej knižnice, ak obsahuje aj študentom vytvorené knižnice.
     */
    public String process() {
        String[] lines = file.split("\n");

        for (String line : lines) {
            if (!line.trim().equals("") && line.trim().charAt(0) == '#' && line.contains("include")) {
                String lib = preprocessInclude(line);
                if (!lib.equals("")) {
                    return lib;
                }
            }
        }
        return "";
    }

    /**
     * Metóda na zistenie, či ide o štandardnú knižnicu.
     *
     * @param line riadok s #include a s knižnicou
     *
     * @return názov nepodporovanej knižnice, ak taká je, inak prázdny reťazec
     */
    private String preprocessInclude(String line) {
        //zmaže medzery
        String temp = line.replaceAll(" ", "");

        StringBuilder word = new StringBuilder("");
        for (int i = 9; i <temp.length(); i++) {
            if (Character.isLetterOrDigit(temp.charAt(i)) || temp.charAt(i) == '_' || temp.charAt(i) == '.') {
                word.append(temp.charAt(i));
            } else {
                break;
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
            case "malloc.h":
                return "";
            default:
                return word.toString();
        }
    }
}
