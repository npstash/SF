package proc.content.ui;

import java.awt.Graphics;

import javax.swing.JPanel;

import proc.content.Paintable;
import proc.content.ContentEvent;
import proc.content.ContentListener;

/**
 * @author Nestor
 */
public class PaintablePanel extends JPanel implements
        ContentListener {

    private Paintable toPaint = null;

    public PaintablePanel() {
        super();
    }

    public synchronized void contentsChanged(ContentEvent e) {
        Object c = e.getContents();
        if (c != null) {
            if (c instanceof Paintable) {
                toPaint = (Paintable) e.getContents();
                repaint();
            }
        }
    }

    public synchronized void paint(Graphics g) {
        super.paint(g);
        if (toPaint != null) {
            toPaint.doPaint(g, getBounds());
        }
    }

}