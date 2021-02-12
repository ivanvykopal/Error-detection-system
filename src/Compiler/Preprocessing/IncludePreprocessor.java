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

        int length = lines.length;
        for (int i = 0; i < length; i++) {
            if (lines[i].trim().charAt(0) == '#' && lines[i].contains("include")) {
                if (!preprocessInclude(lines[i])) {
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

        switch (word.hashCode()) {
            case Libraries.ASSERT: return true;           //assert.h
            case Libraries.COMPLEX: return true;          //complex.h
            case Libraries.CTYPE: return true;            //ctype.h
            case Libraries.ERRNO: return true;            //errno.h
            case Libraries.FENV: return true;             //fenv.h
            case Libraries.FLOAT: return true;            //float.h
            case Libraries.INTTYPES: return true;         //inttypes.h
            case Libraries.ISO646: return true;           //iso646.h
            case Libraries.LIMITS: return true;           //limits.h
            case Libraries.LOCALE: return true;           //locale.h
            case Libraries.MATH: return true;             //math.h
            case Libraries.SETJMP: return true;           //setjmp.h
            case Libraries.SIGNAL: return true;           //signal.h
            case Libraries.STDALIGN: return true;         //stdalign.h
            case Libraries.STDARG: return true;           //stdarg.h
            case Libraries.STDATOMIC: return true;        //stdatomic.h
            case Libraries.STDBOOL: return true;          //stdbool.h
            case Libraries.STDDEF: return true;           //stddef.h
            case Libraries.STDINT: return true;           //stdint.h
            case Libraries.STDIO: return true;            //stdio.h
            case Libraries.STDLIB: return true;           //stdlib.h
            case Libraries.STDNORETURN: return true;      //stdnoreturn.h
            case Libraries.STRING: return true;           //string.h
            case Libraries.TGMATH: return true;           //tgmath.h
            case Libraries.THREADS: return true;          //threads.h
            case Libraries.TIME: return true;             //time.h
            case Libraries.UCHAR: return true;            //uchar.h
            case Libraries.WCHAR: return true;            //wchar.h
            case Libraries.WCTYPE: return true;           //wctype.h
            default: return false;
        }
    }
}
