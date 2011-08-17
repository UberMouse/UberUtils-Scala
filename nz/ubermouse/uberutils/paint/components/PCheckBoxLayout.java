package nz.ubermouse.uberutils.paint.components;


import nz.ubermouse.uberutils.paint.abstracts.PComponent;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/29/11
 * Time: 2:13 PM
 * Package: nz.uberutils.paint.components;
 */
public class PCheckBoxLayout extends PComponent
{
    private final int         offsetIncrement;
    private final String[]    leftColumn;
    private final PCheckBox[] rightColumn;
    private final ColorScheme scheme;
    private       Font        font;

    public PCheckBoxLayout(int x, int y, int offsetIncrement, String[] leftColumn, PCheckBox[] rightColumn, Font font) {
        this(x, y, offsetIncrement, leftColumn, rightColumn, font, null);
    }

    public PCheckBoxLayout(int x, int y, int offsetIncrement, String[] leftColumn, PCheckBox[] rightColumn) {
        this(x, y, offsetIncrement, leftColumn, rightColumn, null, null);
    }

    public PCheckBoxLayout(int x, int y, String[] leftColumn, PCheckBox[] rightColumn) {
        this(x, y, 5, leftColumn, rightColumn, null, null);
    }

    public PCheckBoxLayout(int x, int y, String[] leftColumn, PCheckBox[] rightColumn, Font font) {
        this(x, y, 5, leftColumn, rightColumn, font);
    }

    public PCheckBoxLayout(int x,
                           int y,
                           int offsetIncrement,
                           String[] leftColumn,
                           PCheckBox[] rightColumn,
                           Font font,
                           ColorScheme scheme) {
        this.x = x;
        this.y = y;
        this.offsetIncrement = offsetIncrement;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
        this.font = font;
        this.scheme = scheme;
    }

    public PCheckBoxLayout(int x,
                           int y,
                           int offsetIncrement,
                           String[] leftColumn,
                           PCheckBox[] rightColumn,
                           ColorScheme scheme) {
        this(x, y, offsetIncrement, leftColumn, rightColumn, null, scheme);
    }

    public PCheckBoxLayout(int x, int y, String[] leftColumn, PCheckBox[] rightColumn, ColorScheme scheme) {
        this(x, y, 5, leftColumn, rightColumn, null, scheme);
    }

    public PCheckBoxLayout(int x, int y, String[] leftColumn, PCheckBox[] rightColumn, Font font, ColorScheme scheme) {
        this(x, y, 5, leftColumn, rightColumn, font, scheme);
    }

    @Override
    public void repaint(Graphics2D g) {
        if (font == null)
            font = g.getFont();
        if (scheme != null)
            g.setColor(scheme.text);
        g.setFont(font);
        String largestString = "";
        int offset = 0;
        for (String str : leftColumn) {
            if (str == null)
                continue;
            if (str.length() > largestString.length())
                largestString = str;

        }
        offset = 0;
        Rectangle2D bounds = g.getFont().getStringBounds(largestString, g.getFontRenderContext());
        for (int i = 0, rightColumnLength = rightColumn.length; i < rightColumnLength; i++) {
            PCheckBox box = rightColumn[i];
            if (box != null) {
                box.setX((int) (x + bounds.getWidth() + offsetIncrement));
                box.setY((y + offset) - (box.getHeight() / 2) - 5);
                Color c = g.getColor();
                box.repaint(g);
                g.setColor(c);
                g.drawString(leftColumn[i], x, y + offset);
                offset += box.getHeight() + 5;
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        for (PCheckBox box : rightColumn)
            box.mouseClicked(e);
    }

    public void mouseMoved(MouseEvent e) {
        for (PCheckBox box : rightColumn)
            box.mouseMoved(e);
    }

    public static enum ColorScheme
    {
        GRAPHITE(new Color(51, 51, 51)),
        LIME(new Color(255, 153, 51)),
        HOT_PINK(new Color(110, 255, 110)),
        WHITE(new Color(220, 220, 220));
        private Color text;

        ColorScheme(Color text) {
            this.text = text;
        }
    }
}
