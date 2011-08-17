package nz.ubermouse.uberutils.helpers.tasks

import com.rsbuddy.script.task.LoopTask
import com.rsbuddy.script.methods.Environment
import java.io.IOException
import nz.ubermouse.uberutils.irc.Client
import nz.ubermouse.uberutils.irc.pircbot.IrcException
import nz.ubermouse.uberutils.irc.gui.Gui

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/4/11
 * Time: 8:40 PM
 * Package: nz.uberutils.helpers.tasks;
 */
class IrcTask(server: String, channel: String, name: String) extends LoopTask
{

  import IrcTask._

  instanceVar = this

  def this(channel: String, name: String) {
    this ("irc.strictfp.com", channel, name)
  }

  def this(channel: String) {
    this ("irc.strictfp.com", channel, Environment.getUsername)
  }

  def loop: Int = {
    100
  }

  override def onFinish() {
    Client.instance.closeGui()
  }

  def openGui() {
    if (Client.instance != null) Client.instance.showGui()
  }

  def connect() {
    new Thread(new Runnable
    {
      def run() {
        try {
          new Client(server, channel, name)
        } catch {
          case e: IOException => {
            e.printStackTrace()
          }
          case e: IrcException => {
            e.printStackTrace()
          }
        }
      }
    }).start()
    while (Client.instance == null) {

    }
    Client.instance.showGui()
    try {
      while (Gui.instance == null || Gui.instance.userListPane == null) {}
      Client.instance.join()
    } catch {
      case e: IOException => {
        e.printStackTrace()
      }
      case e: IrcException => {
        e.printStackTrace()
      }
    }
  }
}

object IrcTask
{
  var instanceVar: IrcTask = null

  def instance: IrcTask = {
    instanceVar
  }
}