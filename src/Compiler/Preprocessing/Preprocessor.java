package Compiler.Preprocessing;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Preprocessor {
    private int position = 0;
    private String oldFile;
    private String newFile = "";
    String[] lines;

    public Preprocessor(String file) {
        this.oldFile = file;
    }

    private void nextLine() {
        position++;
    }

    private void preprocess() {
        lines = oldFile.split("\n");

        for (; position < lines.length; nextLine()) {
            //odstránenie zbytočných medzier
            String temp = lines[position].trim().replaceAll(" +", " ");
            if (temp.charAt(0) == '#') {
                while (lines[position].contains("\\")) {
                    temp = temp.replace("\\"," ");
                    nextLine();
                    temp = temp.concat(lines[position].trim().replaceAll(" +"," "));
                }

                if (temp.contains("define")) {
                    //# define
                    preprocessDefine(temp.split(" "));
                } else if (temp.contains("if") || temp.contains("else")) {
                    //# if, #ifdef, #ifndef, #else, #elif
                    preprocessConditions();
                }
            } else {
                newFile = newFile.concat(lines[position]);
            }
        }
    }

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
                for(int i = 2; i < length; i++) {
                    temp = temp.concat(words[i]);
                }
                //nahradiť v texte
                oldFile = oldFile.replaceAll("\\b"+name+"\\b", temp);
            }
        }
    }

    private void preprocessConditions() {
        nextLine();
        while (true) {
            String line = lines[position].trim();
            if (line.charAt(0) == '#' && (line.contains("#endif") || line.contains("# endif"))) {
                break;
            }
            nextLine();
        }
    }
}