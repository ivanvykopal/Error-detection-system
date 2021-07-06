package Backend.Controller;

import Backend.ProgramLogger;
import Frontend.Analysis1Window;
import Frontend.Analysis2Window;
import Frontend.MainWindow;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

/**
 * Trieda predstavujúca controller pre MainWindow.
 *
 * <p> V rámci tejto triedy sa spracovávajú stlačenia tlačitidiel pre dané okno.
 *
 * @see Controller
 *
 * @author Ivan Vykopal
 */
public class MainController extends Controller {
    private final MainWindow window;

    private MainController(MainWindow window) {
        this.window = window;

        initController();
    }

    public static void createController(MainWindow window) {
        new MainController(window);
    }

    private void initController() {
        this.window.analysis1BtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Analysis1Controller.createController(new Analysis1Window());
                window.setVisible(false);
            }
        });

        this.window.analysis2BtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Analysis2Controller.createController(new Analysis2Window());
                window.setVisible(false);
            }
        });

        this.window.closeAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                window.getClose().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/close-1.png"))));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                window.getClose().setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/Images/close.png"))));
            }
        });

    }

}