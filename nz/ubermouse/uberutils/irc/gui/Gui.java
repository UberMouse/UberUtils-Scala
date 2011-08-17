/*
 * Created by JFormDesigner on Sat Jun 04 15:45:21 NZST 2011
 */

package nz.ubermouse.uberutils.irc.gui;

import nz.ubermouse.uberutils.irc.Client;
import nz.ubermouse.uberutils.irc.OutputThread;
import nz.ubermouse.uberutils.irc.pircbot.User;
import nz.ubermouse.uberutils.swing.JColorTextPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Taylor Lodge
 */
public class Gui extends JFrame implements ComponentListener
{
    private static Gui instance = null;

    public static Gui instance() {
        return instance;
    }


    public Gui() throws Exception {
        if (Client.instance() == null)
            throw new Exception("IRC Client not created! Client instance must not be null upon GUI creation.");
        instance = this;
        initComponents();
        myInitComponents();
        addComponentListener(this);
    }

    private void myInitComponents() {
        contentPane.setEditable(false);
        userListPane.setEditable(false);
        contentPanelScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });
        changeNick(Client.instance().name);
    }

    public void changeNick(String newNick) {
        usernameLbl.setText(newNick + ": ");
    }

    private void inputFieldKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            addMessage(Client.instance().name, inputField.getText());
            inputField.setText("");
        }
    }

    public void addMessage(String username, String text) {
        if (username.equals(Client.instance().name) && text.substring(0, 1).equals("/")) {
            text = text.replaceFirst("/", "");
            Client.instance().doAction("action", text);
        }
        else {
            Color color;
            if (text.contains(Client.instance().name) && !username.equals(Client.instance().name)) {
                Toolkit.getDefaultToolkit().beep();
                color = Color.RED;
            }
            else
                color = Color.WHITE;
            addContentText(color, username + ": " + text + "\n");
            if (username.equals(Client.instance().name))
                OutputThread.addToQueue(text);
        }
    }

    public void addContentText(Color c, String text) {
        contentPane.append(c, text);
    }

    public void addContentText(String text) {
        addContentText(Color.WHITE, text);
    }

    public void setUserList(User[] users) {
        userListPane.setText("");
        for (User u : users) {
            Color c;
            if (u.isOp())
                c = Color.MAGENTA;
            else
                c = Color.WHITE;
            userListPane.append(c, u.getNick() + "\n");
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        contentPanelScroll = new JScrollPane();
        contentPane = new JColorTextPane();
        userPanel = new JPanel();
        usernameLbl = new JLabel();
        inputField = new JTextField();
        userListPanel = new JPanel();
        useListScroll = new JScrollPane();
        userListPane = new JColorTextPane();
        //======== this ========
        Container contentPane2 = getContentPane();
        contentPane2.setLayout(new BorderLayout());

        //======== contentPanelScroll ========
        {
            contentPanelScroll.setAutoscrolls(true);

            //---- contentPane ----
            contentPane.setMinimumSize(new Dimension(200, 200));
            contentPane.setPreferredSize(new Dimension(500, 400));
            contentPanelScroll.setViewportView(contentPane);
        }
        contentPane2.add(contentPanelScroll, BorderLayout.CENTER);

        //======== userPanel ========
        {
            userPanel.setLayout(new BorderLayout());

            //---- usernameLbl ----
            usernameLbl.setText("Username: ");
            usernameLbl.setLabelFor(inputField);
            userPanel.add(usernameLbl, BorderLayout.WEST);

            //---- inputField ----
            inputField.setMinimumSize(new Dimension(200, 20));
            inputField.setPreferredSize(new Dimension(400, 20));
            inputField.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e) {
                    inputFieldKeyPressed(e);
                }
            });
            userPanel.add(inputField, BorderLayout.CENTER);
        }
        contentPane2.add(userPanel, BorderLayout.SOUTH);

        //======== userListPanel ========
        {
            userListPanel.setLayout(new BorderLayout());

            //======== useListScroll ========
            {

                //---- userListPane ----
                userListPane.setPreferredSize(new Dimension(150, 200));
                useListScroll.setViewportView(userListPane);
            }
            userListPanel.add(useListScroll, BorderLayout.CENTER);
        }
        contentPane2.add(userListPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane    contentPanelScroll;
    public JColorTextPane contentPane;
    private JPanel         userPanel;
    public  JLabel         usernameLbl;
    public  JTextField     inputField;
    private JPanel         userListPanel;
    private JScrollPane    useListScroll;
    public  JColorTextPane userListPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void componentResized(ComponentEvent componentEvent) {
    }

    public void componentMoved(ComponentEvent componentEvent) {
    }

    public void componentShown(ComponentEvent componentEvent) {
    }

    public void componentHidden(ComponentEvent componentEvent) {
        setVisible(false);
        dispose();
    }
}
