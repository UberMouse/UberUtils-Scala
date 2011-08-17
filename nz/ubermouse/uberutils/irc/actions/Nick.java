package nz.ubermouse.uberutils.irc.actions;


import nz.ubermouse.uberutils.irc.Client;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 4:50 PM
 * Package: nz.uberutils.irc.actions;
 */
public class Nick implements Action
{
    public boolean matchesAction(String name) {
        return name.equalsIgnoreCase("nick");
    }

    public void execute(String... actions) {
        Client.instance().changeNick(actions[0]);
    }
}
