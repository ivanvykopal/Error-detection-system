package Backend;

import Backend.Controller.ConsoleController;
import Backend.Controller.GUIController;

/**
 * Hlavná trieda pre spustenie programu.
 *
 * @author Ivan Vykopal
 */
public class Main {

    /**
     * Hlavná metóda pre spustenie programu.
     *
     * @param args argumenty programu
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            try {
                GUIController.runGUI(args);
            } catch (Exception e) {
                System.out.println("Pre spustenie grafického rozhranie je potrebné spúšťať program s verziou Javy " +
                        "podporujúcou JavaFX (Java do verzie 10 vrátane)!");
            }
        } else {
            if (args.length == 1) {
                switch (args[0]) {
                    case "console" :
                        ConsoleController.runConsole();
                        break;
                    case "gui" :
                        try {
                            GUIController.runGUI(args);
                        } catch (Exception e) {
                            System.out.println("Pre spustenie grafického rozhranie je potrebné spúšťať program s verziou Javy " +
                                    "podporujúcou JavaFX (Java do verzie 10 vrátane)!");
                        }
                        break;
                    default:
                        System.out.println("Nesprávny argument programu!");
                        break;
                }
            } else {
                System.out.println("Program požaduje len jeden argument!");
            }
        }
    }
}