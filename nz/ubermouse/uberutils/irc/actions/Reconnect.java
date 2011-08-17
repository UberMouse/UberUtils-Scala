package nz.ubermouse.uberutils.irc.actions;

import nz.ubermouse.uberutils.irc.Client;
import nz.ubermouse.uberutils.irc.pircbot.IrcException;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/6/11
 * Time: 12:08 PM
 * Package: nz.uberutils.irc.actions;
 */
public class Reconnect implements Action
{
    public boolean matchesAction(String name) {
        return name.equalsIgnoreCase("reconnect");
    }

    public void execute(String... actions) {
        Client.instance().disconnect();
        while (Client.instance().isConnected()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Client.instance().join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
    }
}
