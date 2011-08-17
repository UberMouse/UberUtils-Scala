package nz.ubermouse.uberutils.irc.actions;

import nz.ubermouse.uberutils.irc.Client;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/6/11
 * Time: 12:09 PM
 * Package: nz.uberutils.irc.actions;
 */
public class Join implements Action
{
    public boolean matchesAction(String name) {
        return name.equalsIgnoreCase("join");
    }

    public void execute(String... actions) {
        Client.instance().joinChannel(Client.instance().channel);
    }
}
