package Backend;

import Backend.Controller.ConsoleController;
import Backend.Controller.GUIController;

import java.util.ResourceBundle;

/**
 * Hlavná trieda pre spustenie programu.
 *
 * @author Ivan Vykopal
 */
public class Main {

    /** Atribút bundle predstavuje súbor s aktuálnou jazykovou verziou. **/
    private static final ResourceBundle bundle = InternationalizationClass.getBundle();

    /**
     * Hlavná metóda pre spustenie programu.
     *
     * @param args argumenty programu
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            GUIController.runGUI();
        } else {
            if (args.length == 1) {
                switch (args[0]) {
                    case "console" :
                        ConsoleController.runConsole();
                        break;
                    case "gui" :
                            GUIController.runGUI();
                        break;
                    default:
                        System.out.println(bundle.getString("argumentErr1"));
                        break;
                }
            } else {
                System.out.println(bundle.getString("argumentErr2"));
            }
        }
    }
}