package nz.ubermouse.uberutils.helpers.tasks

import com.rsbuddy.script.task.LoopTask
import com.rsbuddy.event.listeners.MessageListener
import java.util.logging.Logger
import com.rsbuddy.script.methods.Environment
import java.io.{IOException, File}
import com.rsbuddy.event.events.MessageEvent
import nz.ubermouse.uberutils.helpers.Utils

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/18/11
 * Time: 5:59 PM
 * Package: nz.uberutils.helpers.tasks;
 */
class ImageThread(name: String, hourly: Boolean, levelup: Boolean, onFinishBool: Boolean) extends LoopTask with
                                                                                                  MessageListener
{

  import ImageThread._

  startTime = System.currentTimeMillis
  hour = 1
  firstRun = true

  private def log(text: AnyRef) {
    Logger.getLogger(name).info("" + text)
  }

  def this(name: String) {
    this (name, true, true, true)
  }

  override def onStart: Boolean = {
    try {
      val f: File = new File(Environment.getStorageDirectory.getCanonicalPath + "\\artebots\\" + name + "\\")
      if (!f.exists) f.mkdirs
    } catch {
      case ignored: IOException => {
      }
    }
    true
  }

  def loop = {
    if (hourly && !firstRun) {
      saveImage(hour + "h")
      log("Script has run for " + hour + "h's, saving screenshot")
      hour += 1;
    }
    if (firstRun) firstRun = false
    3600000
  }

  def messageReceived(messageEvent: MessageEvent) {
    if (levelup) {
      if (messageEvent.isAutomated) {
        if (messageEvent.getMessage.contains("You've just advanced")) {
          saveImage("levelup")
          log("You just leveled up! Saving screenshot")
        }
      }
    }
  }

  def saveImage(name: String) {
    try {
      Utils.saveImage(Environment.takeScreenshot(true), Environment.getStorageDirectory.getCanonicalPath +
                                                        "\\artebots\\" +
                                                        name +
                                                        "\\" +
                                                        name +
                                                        "-" +
                                                        name +
                                                        "-" +
                                                        String.valueOf(Utils.random(1, 9999999)) +
                                                        ".png", "png")
    } catch {
      case ignored: IOException => {
      }
    }
  }

  override def onFinish() {
    if (onFinishBool) {
      saveImage("end")
      log("Script stopping, saving screenshot")
    }
  }
}

object ImageThread
{
  private var startTime: Long    = 0L
  private var hour     : Int     = 0
  private var firstRun : Boolean = true
}