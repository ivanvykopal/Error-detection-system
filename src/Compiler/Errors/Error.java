package Compiler.Errors;

import java.util.HashMap;
import java.util.Map;

/**
 * Trieda obsahujúca chyby.
 */
public class Error {
    Map<String, String> errors = new HashMap<String, String>() {{
        // symbol table errors
        put("E-ST-01", "neinicializovaná premenná");
        put("E-ST-02", "inicializácia mimo rozsah");
        put("E-ST-03", "nedeklarovaná premenná");
        put("E-ST-04", "využitie premennej mimo rozsah platnosti");

        // lexical analysis errors
        put("E-LA-01", "neidentifikovateľný reťazec");
        put("E-LA-02", "prekročenie dĺžky názvu premennej");
        put("E-LA-03", "chýbajúce uzatvorenie komentáru");
        put("E-LA-04", "chýbajúce úvodzovky pre reťazec");

        // syntax analysis errors
        put("E-SxA-01", "chýbajúca zátvorka");
        put("E-SxA-02", "zátvorka navyše");
        put("E-SxA-03", "chýbajúca bodkočiarka");
        put("E-SxA-04", "chybné kľúčové slovo");
        put("E-SxA-05", "chýbajúci operátor");
        put("E-SxA-06", "chýbajúci argument");
        put("E-SxA-07", "nesprávna syntax");
        put("L-SxA-08", "nesprávny operátor pre porovnávanie");
        put("L-SxA-09", "využitie kľúčového slova ako premennej");

        // semantic analysis errors
        put("E-SmA-01", "typová nezhoda");
        put("E-SmA-02", "viacnásobná deklarácia premennej");
        put("L-SmA-03", "nezhoda medzi typmi premennej a návratovej hodnoty funkcie");

        // register problems
        put("E-RP-01", "neuvoľnenie dynamicky alokovanej pamäte");
        put("E-RP-02", "uvoľnenie neexistujúcej pamäte");
        put("E-RP-03", "prístup do uvoľnenej pamäte");
        put("E-RP-04", "smerník na neplatný objekt (dangling pointer)");
        put("E-RP-05", "dlho aktívna premenná");
        put("E-RP-06", "prístup mimo pamäť");
        put("E-RP-07", "neoptimálne využívanie premenných");
        put("E-RP-08", "priradenie dynamického smerníka do statického");
    }};

    public String getError(String key) {
        return errors.get(key);
    }

}