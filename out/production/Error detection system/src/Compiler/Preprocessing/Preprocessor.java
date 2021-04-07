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
                while (lines[position].contains("\\") && isBackslashLast(lines[position])) {
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
                } else {
                    newFile.append("\n");
                }
            } else {
                newFile.append(lines[position]).append("\n");
            }
        }
        return newFile.toString().replaceAll("\\binline\\b", "");
    }

    /**
     * Metóda pre zistenie či sa na konci riadku nachádza \.
     *
     * @param line riadok, v ktorom zisťujeme posledný výskyt \
     *
     * @return true, ak je \ posledný, inak false
     */
    private boolean isBackslashLast(String line) {
        line = line.replaceAll("\r","").replaceAll("\t", "");
        int index = line.lastIndexOf("\\");
        return index == line.length() - 1;
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
                String indent = "";
                for(int i = 2; i < length; i++) {
                    temp = temp.concat(indent);
                    indent = " ";
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
                    int lastIndex = getLastIndex(index);
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
                        newString = replaceString(newString, arr2[i].trim(), arr1[i].trim());
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
                oldFile = replaceString(new String(oldFile), name, temp);
            }
        } else {
            // typ: #define ...
            String name = words[1];
            if (name.contains("(")) {
                // makro funkcia
                String temp = "";
                int length = words.length;
                String indent = "";
                for(int i = 1; i < length; i++) {
                    temp = temp.concat(indent);
                    indent = " ";
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
                    int lastIndex = getLastIndex(index);
                    //vyriešiť mapovanie
                    String t = oldFile.substring(oldFile.indexOf("(", index) + 1, lastIndex);
                    //t = t.replaceAll(" ", "");
                    String[] arr1 = t.split(",");
                    String[] arr2 = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")")).split(",");
                    //nebol nájdený
                    if (arr1.length != arr2.length) {
                        continue;
                    }

                    String newString = "";
                    newString = temp.substring(temp.indexOf(")") + 1);
                    for (int i = 0; i < arr1.length; i++) {
                        newString = replaceString(newString, arr2[i].trim(), arr1[i].trim());
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
                oldFile = replaceString(new String(oldFile), name, temp);
            }
        }
    }

    /**
     * Meóda na zistenie pozície poslednej ).
     *
     * @param index pozícia, od ktorej posledný vyskyt hľadáme
     *
     * @return pozícia výskytu poslednej )
     */
    private int getLastIndex(int index) {
        int position = oldFile.indexOf("(", index);
        int count = 1;
        while (count != 0) {
            position++;
            if (oldFile.charAt(position) == '(') {
                count++;
            }
            if (oldFile.charAt(position) == ')') {
                count--;
            }
        }
        return position;
    }

    /**
     * Metóda pre nahradenie jedného reťazca iným v zadanom reťazci.
     *
     * @param newString reťazec, v ktorom reťazce nahradzujeme
     *
     * @param temp1 reťazec, ktorý chceme nahradiť
     *
     * @param temp2 reťazec, ktorým nahradzujeme
     *
     * @return výsledný reťazec
     */
    private String replaceString(String newString, String temp1, String temp2) {
        if (temp1.equals("") && temp2.equals("")) {
            return newString;
        }
        newString += " ";
        int index = newString.indexOf(temp1);
        while (index != -1) {
            char c1 = newString.charAt(index -1);
            char c2 = newString.charAt(index + temp1.length());
            if (c1 == '\'' && c2 == '\'') {
                index = newString.indexOf(temp1, index + temp1.length());
                continue;
            }
            String temp = newString;
            newString = newString.substring(0, index - 1) + newString.substring(index - 1, index + temp1.length() + 1).replaceFirst("\\b" + temp1+ "\\b", temp2)
                    + newString.substring(index + temp1.length() + 1);
            if (newString.equals(temp)) {
                index = newString.indexOf(temp1, index + 1);
            } else {
                index = newString.indexOf(temp1, index + temp2.length());
            }
        }
        return newString;
    }
}