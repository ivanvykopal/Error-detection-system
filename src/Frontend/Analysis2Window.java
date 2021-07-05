package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Analysis2Window extends JWindow {
    private JButton analysis1;
    private JButton analysis2;
    private JLabel close;
    private JLabel minimalize;
    private JLabel hide;
    private JPanel panel;

    public Analysis2Window() {
        add(this.panel);
        this.setSize(800, 600);
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
                Point p = Analysis2Window.this.getLocation();
                p.x += e.getXOnScreen() - mx;
                p.y += e.getYOnScreen() - my;
                mx = e.getXOnScreen();
                my = e.getYOnScreen();
                Analysis2Window.this.setLocation(p);
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