package nz.ubermouse.uberutils.irc;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 11:55 AM
 * Package: nz.uberutils.irc;
 */
public class OutputThread implements Runnable
{
    public static        boolean           stop      = false;
    private static final ArrayList<String> sendQueue = new ArrayList<String>();

    public void run() {
        while (!stop) {
            try {
                if (sendQueue.size() > 0) {
                    String message = sendQueue.get(0);
                    Client.instance().doAction("say", message);
                    sendQueue.remove(0);
                }
                Thread.sleep(750);
            } catch (Exception ignored) {
            }
        }
    }

    public static void addToQueue(String message) {
        sendQueue.add(message);
    }
}
