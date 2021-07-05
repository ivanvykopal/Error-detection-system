package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MainWindow extends JWindow {
    private JPanel panel1;
    private JButton analysis1;
    private JButton analysis2;
    private JLabel close;
    private JLabel minimalize;
    private JLabel hide;

    public MainWindow() {
        add(this.panel1);
        this.setSize(800,600);
        setVisible(true);

        addMouseMotionListener(new MouseMotionListener() {
            private int mx, my;

            @Override
            public void mouseMoved(MouseEvent e) {
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = MainWindow.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                MainWindow.this.setLocation(p);
            }
        });
    }

    public void analysis1AddListener(MouseListener listener) {
        analysis1.addMouseListener(listener);
    }

    public void analysis2AddListener(MouseListener listener) {
        analysis2.addMouseListener(listener);
    }

    public void closeAddListener(MouseListener listener) {
        close.addMouseListener(listener);
    }

    public void minimalizeAddListener(MouseListener listener) {
        minimalize.addMouseListener(listener);
    }

    public void hideAddListener(MouseListener listener) {
        hide.addMouseListener(listener);
    }

    public void mainWindowAddListener(MouseListener listener) {
        this.addMouseListener(listener);
    }

}