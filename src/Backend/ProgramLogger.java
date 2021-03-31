package Backend;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProgramLogger {
    private static Logger logger;
    private FileHandler handler = null;

    private ProgramLogger(String className) {
        try {
            handler = new FileHandler("logs/log-file.log", true);
        } catch (IOException e) {
            e.printStackTrace();
            File file = new File("logs/log-file.log");
            try {
                file.createNewFile();
                handler = new FileHandler("logs/log-file.log", true);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        logger = Logger.getLogger(className);
        if (handler != null) {
            logger.addHandler(handler);
        }
    }

    public static ProgramLogger createLogger(String className) {
        return new ProgramLogger(className);
    }

    public void log(Level level, String message) {
        logger.log(level, message);
        handler.close();
    }

}
