package nz.ubermouse.uberutils.irc;

import nz.ubermouse.uberutils.helpers.Utils;
import nz.ubermouse.uberutils.irc.actions.Action;
import nz.ubermouse.uberutils.irc.actions.Join;
import nz.ubermouse.uberutils.irc.actions.Nick;
import nz.ubermouse.uberutils.irc.actions.Reconnect;
import nz.ubermouse.uberutils.irc.gui.Gui;
import nz.ubermouse.uberutils.irc.pircbot.IrcException;
import nz.ubermouse.uberutils.irc.pircbot.NickAlreadyInUseException;
import nz.ubermouse.uberutils.irc.pircbot.PircBot;
import nz.ubermouse.uberutils.irc.pircbot.User;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 11:21 AM
 * Package: nz.uberutils.irc;
 */
public class Client extends PircBot
{
    public final String server;
    public final String channel;
    public String name;
    private final String defaultName;
    private static Client instance = null;
    private static Gui gui;
    private static final ArrayList<Action> actions = new ArrayList<Action>();

    private static final String prompt = "!";

    public static enum Actions {
        SAY, DISCONNECT, ACTION
    }

    public static Client instance() {
        return instance;
    }

    public Client(String server, String channel, String name) throws IOException, IrcException {
        setLogin("ArteBotter");
        actions.add(new Nick());
        actions.add(new Join());
        actions.add(new Reconnect());
        instance = this;
        this.server = server;
        this.channel = channel;
        String rand = "" + Utils.random(0, 9999);
        while (rand.length() < 4)
            rand = "0" + rand;
        defaultName = "ArteBotter" + rand;
        if (name != null)
            this.name = name;
        else
            this.name = defaultName;
        setName(this.name);
        setVersion("ArteIRC: 0.1");
        SwingUtilities.invokeLater(new createGui());
        new Thread(new OutputThread()).start();
    }

    public void join() throws IOException, IrcException {
        if (isConnected())
            return;
        new Thread(new Runnable() {
            public void run() {
                Gui.instance().addContentText("Joining server...\n");
                try {
                    connect(server);
                } catch (NickAlreadyInUseException e) {
                    setName(name += "_");
                    Gui.instance().changeNick(name);
                    try {
                        connect(server);
                    } catch (Exception ignored) {
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                Gui.instance().addContentText("Joining channel...\n");
                joinChannel(channel);
            }
        }).start();
    }

    public void doAction(String action) {
        doAction(action, "");
    }

    public void doAction(String action, String message) {
        switch (Actions.valueOf(action.toUpperCase())) {
            case SAY:
                sendMessage(channel, message);
                break;
            case DISCONNECT:
                disconnect();
                break;
            case ACTION:
                action = message;
                if (action.contains(" ")) {
                    action = action.split(" ")[0];
                    message = message.split(" ")[1];
                }
                for (Action a : actions)
                    if (a.matchesAction(action)) {
                        if (message.contains(" "))
                            a.execute(message.split(" "));
                        else
                            a.execute(message);

                    }
                break;
        }
    }

    protected void onAction(String sender, String login, String hostname, String target, String action) {

    }

    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (message.substring(0, 1).equals(prompt)) {
            message = message.replaceFirst(prompt, "");
            if (message.contains(" ")) {
                String[] split;
                split = message.split(" ");
                doAction(split[0], split[1]);
            } else
                doAction(message);
        }
    }

    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (message.substring(0, 1).equals(prompt)) {
            message = message.replaceFirst(prompt, "");
            if (message.contains(" ")) {
                String[] split;
                split = message.split(" ");
                doAction(split[0], split[1]);
            } else
                doAction(message);

        } else
            gui.addMessage(sender, message);
    }

    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        if (oldNick.equals(name)) {
            Gui.instance().changeNick(newNick);
            name = newNick;
            setName(name);
        }
        Gui.instance().setUserList(getUsers(channel));
        Gui.instance().addContentText(oldNick + " is now known as " + newNick + "\n");
    }

    protected void onJoin(String channel, String sender, String login, String hostname) {
        if (sender.equals(name))
            Gui.instance().addContentText("Joined channel!\n");
        else
            Gui.instance().addContentText(sender + " has joined the channel\n");
        Gui.instance().setUserList(getUsers(channel));
    }

    protected void onPart(String channel, String sender, String login, String hostname) {
        Gui.instance().setUserList(getUsers(channel));
        if (!sender.equals(name))
            Gui.instance().addContentText(sender + " has left the channel\n");
    }

    protected void onUserList(String channel, User[] users) {
        Gui.instance().setUserList(users);
    }

    protected void onDisconnect() {
        Gui.instance().setUserList(new User[0]);
        Gui.instance().addContentText("Disconnected from server\ntype /reconnect to rejoin the server\n");
    }

    protected void onKick(String channel,
                          String kickerNick,
                          String kickerLogin,
                          String kickerHostname,
                          String recipientNick,
                          String reason) {
        Gui.instance().setUserList(new User[0]);
        Gui.instance()
                .addContentText("Kicked from channel by " +
                        kickerNick +
                        " because " +
                        reason +
                        "\ntype /join to rejoin the channel\n");
    }


    class showGui implements Runnable {
        public void run() {
            if (gui == null) {
                try {
                    gui = new Gui();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            gui.contentPane.setText("");
            gui.userListPane.setText("");
            gui.setVisible(true);
            gui.setTitle("ArteBots irc: " + channel);
        }
    }

    class createGui implements Runnable {
        public void run() {
            try {
                if (gui == null)
                    gui = new Gui();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class dispose implements Runnable {
        public void run() {
            gui.dispose();
        }
    }

    public void showGui() {
        SwingUtilities.invokeLater(new showGui());
    }

    public void closeGui() {
        SwingUtilities.invokeLater(new dispose());
    }
}
