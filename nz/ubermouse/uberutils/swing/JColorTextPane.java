package nz.ubermouse.uberutils.swing;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 2:27 PM
 * Package: nz.uberutils.swing;
 */
public class JColorTextPane extends JTextPane
{
    public void append(Color fg, String s) {
        append(fg, null, s);
    }

    public void append(Color fg, Color bg, String text) {
        try {
            StyledDocument doc = (StyledDocument) getDocument();
            Style style = doc.addStyle("StyleName", null);
            if (bg != null)
                StyleConstants.setBackground(style, bg);
            StyleConstants.setForeground(style, fg);
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ignored) {
        }
    }

    public void append(String s) {
        append(Color.WHITE, null, s);
    }
}
