package nz.ubermouse.uberutils.irc.actions;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 4:43 PM
 * Package: nz.uberutils.irc.actions;
 */
public interface Action
{
    public boolean matchesAction(String name);

    public void execute(String... actions);
}
