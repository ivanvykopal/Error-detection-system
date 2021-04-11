package Backend;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trieda určená pre logovanie informácií a varovaní do súboru.
 *
 * @author Ivan Vykopal
 */
public class ProgramLogger {
    /** Atribút logger sa využíva pre zápsi logov do súboru. **/
    private static Logger logger;

    /** Atribút handler predstavuje miesto, kde sa logy zapisujú. **/
    private FileHandler handler = null;

    /**
     * Konštruktor, v ktorom sa nastavuje miesto pre ukladanie výpisov.
     *
     * @param className názov triedy, v ktorej sa daný logger volá
     */
    private ProgramLogger(String className) {
        try {
            handler = new FileHandler("logs/log-file.log", true);
        } catch (IOException e) {
            File file = new File("logs/log-file.log");
            try {
                file.createNewFile();
                handler = new FileHandler("logs/log-file.log", true);
            } catch (IOException ignored) {
            }
        }

        logger = Logger.getLogger(className);
        logger.setUseParentHandlers(false);
        if (handler != null) {
            logger.addHandler(handler);
        }
    }

    /**
     * Metóda pre vytvorenie triedy {@code ProgramLogger}.
     *
     * @param className názov triedy, v ktorej sa daný logger volá
     *
     * @return vytvorená trieda {@code ProgramLogger}
     */
    public static ProgramLogger createLogger(String className) {
        return new ProgramLogger(className);
    }

    /**
     * Metóda pre zápis logu do súboru.
     *
     * @param level úroveň chyby, napr. INFO
     *
     * @param message správa
     */
    public void log(Level level, String message) {
        logger.log(level, message);
        handler.close();
    }

}
