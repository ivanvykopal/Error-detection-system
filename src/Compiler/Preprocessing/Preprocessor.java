package Compiler.Preprocessing;

/**
 * Trieda pre predspracovanie zdrojového kódu.
 * V rámci predspracovania ide o spracovanie #define, #include a zvyšných direktív.
 *
 * @author Ivan Vykopal
 */
public class Preprocessor {
    /** Atribút position predstavuje pozíciu aktuálne spracovávanéh znaku v rámci pôvodného súboru. **/
    private int position = 0;

    /** Atribút oldFile predstavuje textovú podobu pôvodného súboru. **/
    private String oldFile;

    /** Atribút newFile predstavuje novú podobu súboru, už predspracovnú. **/
    private StringBuilder newFile = new StringBuilder();

    /** Atribút lines predstavuje rozdelenie súboru do riadkov. **/
    String[] lines;

    /**
     * Konštruktor, ktorý inicaliuje atribút oldFile.
     *
     * @param file textová spodoba analyzovaného súboru
     */
    public Preprocessor(String file) {
        this.oldFile = file;
    }

    /**
     * Metóda na posunutie pozície na nasledujúci znak.
     */
    private void nextLine() {
        position++;
    }

    /**
     * Metóda pre predspracovanie zdrojovéjo kódu a vyriešenie direktív.
     *
     * @return predspracovaný súbor
     */
    public String preprocess() {
        lines = oldFile.split("\n");

        for (; position < lines.length; nextLine()) {
            //odstránenie zbytočných medzier
            String temp = lines[position].trim().replaceAll(" +", " ");
            if (temp.contains("//")) {
                temp = temp.substring(0, temp.indexOf("//"));
            }
            if (!temp.equals("") && temp.charAt(0) == '#') {
                while (lines[position].contains("\\") && !containWhiteSpace(lines[position])) {
                    newFile.append("\n");
                    temp = temp.replace("\\"," ");
                    nextLine();
                    temp = temp.concat(lines[position].trim().replaceAll(" +"," "));
                }

                if (temp.contains("define")) {
                    //#define
                    preprocessDefine(temp.split(" "));
                    newFile.append("\n");
                    lines = oldFile.split("\n");
                } else if (temp.contains("if") || temp.contains("else")) {
                    //#if, #ifdef, #ifndef, #else, #elif
                    preprocessConditions();
                } else {
                    newFile.append("\n");
                }
            } else {
                newFile.append(lines[position]).append("\n");
            }
        }
        String newFileString = newFile.toString().replaceAll("\\binline\\b", "");
        return newFileString;
    }

    private boolean containWhiteSpace(String line) {
        if (line.contains("\r") || line.contains("\n") || line.contains("\t")) {
            return true;
        }
        return false;
    }

    /**
     * Metóda pre predspracovanie #define direktívy v zdrojovom kóde.
     *
     * <p> Všetky definované makrá sú nahradené makrom nachádzajúcom pri direktíve #define.
     *
     * @param words riadok s direktívou define rozdelený na slová.
     */
    private void preprocessDefine(String[] words) {
        if (words[0].length() == 1) {
            // typ: # define ...
            String name = words[2];
            if (name.contains("(")) {
                // makro funkcia
                String temp = "";
                int length = words.length;
                for(int i = 2; i < length; i++) {
                    temp = temp.concat(words[i]);
                }

                int index = temp.indexOf('(');
                name = temp.substring(0, index);
                //prvý výskyt je makro
                index = oldFile.indexOf(name);
                while (true) {
                    index = oldFile.indexOf(name, index + 1);
                    if (index == -1) {
                        break;
                    }
                    int lastIndex = oldFile.indexOf(")", index);
                    //vyriešiť mapovanie
                    String t = oldFile.substring(oldFile.indexOf("(", index) + 1, lastIndex);
                    t = t.replaceAll(" ", "");
                    String[] arr1 = t.split(",");
                    String[] arr2 = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")")).split(",");
                    //nebol nájdený
                    if (arr1.length != arr2.length) {
                        continue;
                    }

                    length = arr1.length;
                    String newString = "";
                    newString = temp.substring(temp.indexOf(")") + 1);
                    for (int i = 0; i < length; i++) {
                        newString = newString.replaceAll("\\b" + arr2[i] + "\\b", arr1[i]);
                    }

                    oldFile = oldFile.substring(0, index) + newString + oldFile.substring(lastIndex + 1);
                }
            } else {
                // makro konštanta
                int length = words.length;
                String temp = "";
                for(int i = 3; i < length; i++) {
                    temp = temp.concat(words[i]);
                }
                //nahradiť v texte
                oldFile = oldFile.replaceAll("\\b"+name+"\\b", temp);
            }
        } else {
            // typ: #define ...
            String name = words[1];
            if (name.contains("(")) {
                // makro funkcia
                String temp = "";
                int length = words.length;
                for(int i = 1; i < length; i++) {
                    temp = temp.concat(words[i]);
                }

                int index = temp.indexOf('(');
                name = temp.substring(0, index);
                //prvý výskyt je makro
                index = oldFile.indexOf(name);
                while (true) {
                    index = oldFile.indexOf(name +"(", index + 1);
                    if (index == -1) {
                        break;
                    }
                    int lastIndex = oldFile.indexOf(")", index);
                    //vyriešiť mapovanie
                    String t = oldFile.substring(oldFile.indexOf("(", index) + 1, lastIndex);
                    t = t.replaceAll(" ", "");
                    String[] arr1 = t.split(",");
                    String[] arr2 = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")")).split(",");
                    //nebol nájdený
                    if (arr1.length != arr2.length) {
                        continue;
                    }

                    String newString = "";
                    newString = temp.substring(temp.indexOf(")") + 1);
                    for (int i = 0; i < arr1.length; i++) {
                        newString = newString.replaceAll("\\b" + arr2[i] + "\\b", arr1[i]);
                    }

                    oldFile = oldFile.substring(0, index) + newString + oldFile.substring(lastIndex + 1);
                }
            } else {
                // makro konštanta
                int length = words.length;
                String temp = "";
                for(int i = 2; i < length; i++) {
                    temp = temp.concat(words[i]);
                }
                //nahradiť v texte
                oldFile = oldFile.replaceAll("\\b"+name+"\\b", temp);
            }
        }
    }

    /**
     * Metóda pre predspracovanie podmienkových direktív v zdrojovom kóde.
     */
    private void preprocessConditions() {
        newFile.append("\n");
        nextLine();
        while (true) {
            String line = lines[position].trim();
            if (!line.equals("") && line.charAt(0) == '#' && (line.contains("#endif") || line.contains("# endif"))) {
                newFile.append("\n");
                break;
            }
            newFile.append("\n");
            nextLine();
        }
    }
}