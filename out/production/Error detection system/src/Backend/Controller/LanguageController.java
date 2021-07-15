package Backend.Controller;

import Backend.InternationalizationClass;
import Frontend.LanguageWindow;
import Frontend.MainWindow;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class LanguageController extends Controller {

    private final LanguageWindow window;

    private LanguageController(LanguageWindow window) {
        this.window = window;

        initController();
    }

    public static void createController(LanguageWindow window) {
        new LanguageController(window);
    }

    private void initController() {

        this.window.getClose().setToolTipText("Close");
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

        this.window.slovakBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setSlovakLanguage();
            }
        });

        this.window.englishBtnAddListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                setEnglishLanguage();
            }
        });
    }

    private void setSlovakLanguage() {
        InternationalizationClass.setBundle("lang/bundle_sk","lang/errors_sk","sk", "SK");
        MainController.createController(new MainWindow());
        this.window.dispose();
    }

    private void setEnglishLanguage() {
        InternationalizationClass.setBundle("lang/bundle_en", "lang/errors_en","en", "US");
        MainController.createController(new MainWindow());
        this.window.dispose();
    }
}
