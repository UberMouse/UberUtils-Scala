package nz.ubermouse.uberutils.arte

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/27/11
 * Time: 7:37 PM
 * Package: nz.ubermouse.uberutils.arte; */

import com.rsbuddy.event.events.MessageEvent
import com.rsbuddy.script.methods.Players
import com.rsbuddy.script.util.Timer
import javax.imageio.ImageIO
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import com.rsbuddy.script.task.{Task, LoopTask}

class ArteNotifier(threadID: Int,
                   purchaseURL: String,
                   lite: Boolean,
                   onLevelUp: Boolean,
                   onHourly: Boolean,
                   onHalfHourly: Boolean,
                   onNameHeard: Boolean,
                   onDeath: Boolean,
                   onFinishBool: Boolean) extends LoopTask
{

  import ArteNotifier._

  purchaseUrl = purchaseURL
  setNotifs(onLevelUp, onHourly, onHalfHourly, onNameHeard, onDeath, onFinishBool)

  def this(threadID: Int) {
    this (threadID, "", false, false, false, false, false, false, false)
  }

  def this(threadID: Int,
           onLevelUp: Boolean,
           onHourly: Boolean,
           onHalfHourly: Boolean,
           onNameHeard: Boolean,
           onDeath: Boolean,
           onFinish: Boolean) {
    this (threadID, "", false, onLevelUp, onHourly, onHalfHourly, onNameHeard, onDeath, onFinish)
  }

  def this(threadID: Int, onLevelUp: Boolean) {
    this (threadID, "", false, onLevelUp, false, false, false, false, false)
  }

  def this(feedbackURL: String) {
    this (-1, "", false, false, false, false, false, false, false)
  }

  override def onStart: Boolean = {
    var url: URL = null
    var urll: URL = null
    var br: BufferedReader = null
    var brl: BufferedReader = null
    try {
      url = new URL("http://artebots.com/notify/message")
      br = new BufferedReader(new InputStreamReader(url.openStream))
      ArteNotifier.messageOfTheDay = br.readLine
      urll = new URL("http://artebots.com/notify/location")
      brl = new BufferedReader(new InputStreamReader(urll.openStream))
      ArteNotifier.messageUrl = brl.readLine
      ArteNotifier.addToTray()
      ArteNotifier.sendNotification("Starting script.", TrayIcon.MessageType.INFO)
    } catch {
      case e: Exception => {
        e.printStackTrace()
        return false
      }
    }
    ArteNotifier.startMillis = System.currentTimeMillis
    hrs = 0
    bhrs = 0
    true
  }

  private def getState = {
    ArteNotifier.State.SLEEP
  }

  def loop: Int = {
    try {
      import ArteNotifier._
      if (System.currentTimeMillis - ArteNotifier.startMillis >= 29000 && !gns) {
        sendNotification(ArteNotifier.messageOfTheDay, TrayIcon.MessageType.INFO)
        gns = true
      }
      if (System.currentTimeMillis - ArteNotifier.startMillis >= 59000 && !gnu) {
        messageUrl = "http://artebots.com"
        gnu = true
      }
      if (!hTimer.isRunning && notifs(Notifiers.HOURLY) && hItem.getState) {
        hrs += 1;
        hrs match {
          case 1 =>
            sendNotification("Running for 1 hour!", TrayIcon.MessageType.INFO)
          case _ =>
            sendNotification("Running for " + hrs + " hours!", TrayIcon.MessageType.INFO)
        }
        hTimer.reset()
      }
      if (!bTimer.isRunning && notifs(Notifiers.BIHOURLY) && bItem.getState) {
        bhrs += 1;
        bhrs
        bhrs match {
          case 1 =>
            sendNotification("Running for 30 minutes!", TrayIcon.MessageType.INFO)
          case _ =>
            var bihours: String = if (bhrs % 2 == 0) Integer.toString(bhrs / 2) else Integer.toString(bhrs / 2) + ".5"
            if (bihours == "1") sendNotification("Running for 1 hour!", TrayIcon.MessageType.INFO)
            else sendNotification("Running for " + bihours + " hours!", TrayIcon.MessageType.INFO)
        }
        bTimer.reset()
      }
      if (died && notifs(Notifiers.DEATHS) && dItem.getState) {
        sendNotification("Your character has died.", TrayIcon.MessageType.WARNING)
        died = false
      }
      if (leveled && notifs(Notifiers.LEVEL_UPS) && lItem.getState) {
        sendNotification("Gained " + skill + " level! You are now " + lvl + "!", TrayIcon.MessageType.INFO)
        leveled = false
      }
      if (name && notifs(Notifiers.NAME_HEARD) && nItem.getState) {
        sendNotification("Your name was said by " + sender + ".", TrayIcon.MessageType.WARNING)
        name = false
      }
      getState match {
        case State.SLEEP =>
          return 1
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
    0
  }

  override def onFinish() {
    if (notifs(Notifiers.ON_FINISH) && oItem.getState) {
      sendNotification("Script has stopped.", TrayIcon.MessageType.INFO)
      Task.sleep(5000)
    }
    try {
      SystemTray.getSystemTray.remove(trayIcon)
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
  }

  def messageReceived(e: MessageEvent) {
    val s: String = e.getMessage
    if (e.isAutomated) {
      if (s.contains("You've just advanced a")) {
        skill = s.substring(21, s.indexOf("level") - 1)
        lvl = s.substring(s.indexOf("level "), s.indexOf("."))
        leveled = lItem.getState
      }
      if (s.contains("Oh dear you are dead.")) {
        died = dItem.getState
      }
    }
    if (s.contains(Players.getLocal.getName) && !(e.getSender == Players.getLocal.getName)) {
      name = nItem.getState
      sender = e.getSender
    }
  }

  private[arte] var skill  : String  = null
  private[arte] var lvl    : String  = null
  private[arte] var sender : String  = null
  private[arte] var leveled: Boolean = false
  private[arte] var died   : Boolean = false
  private[arte] var name   : Boolean = false
  private[arte] var hrs    : Int     = 0
  private[arte] var bhrs   : Int     = 0
}

object ArteNotifier
{
  def addToTray(): Boolean = {
    try {
      if (SystemTray.isSupported) {
        val tray: SystemTray = SystemTray.getSystemTray
        var url: URL = null
        var image: Image = null
        url = new URL("http://wildimp.com/arte/server_connect.png")
        image = ImageIO.read(url)
        val listener: ActionListener = new ActionListener
        {
          def actionPerformed(e: ActionEvent) {
            val s: String = ((e.getSource).asInstanceOf[MenuItem]).getLabel
            if (s.equalsIgnoreCase("Leave feedback")) openURL(feedbackUrl)
            if (s.equalsIgnoreCase("Visit ArteBots.com")) openURL(artebots)
            if (s.equalsIgnoreCase("Purchase Pro")) openURL(purchaseUrl)
          }
        }
        val popup: PopupMenu = new PopupMenu
        val sub: Menu = new Menu("Set notifications...")
        lItem = new CheckboxMenuItem("Level-ups", true)
        hItem = new CheckboxMenuItem("Hourly", true)
        bItem = new CheckboxMenuItem("Bihourly", true)
        nItem = new CheckboxMenuItem("Name heard", true)
        dItem = new CheckboxMenuItem("Deaths", true)
        oItem = new CheckboxMenuItem("On finish", true)
        sub.add(lItem)
        sub.add(hItem)
        sub.add(bItem)
        sub.add(nItem)
        sub.add(dItem)
        sub.add(oItem)
        val sItem: MenuItem = new MenuItem("-")
        val fItem: MenuItem = new MenuItem("Leave feedback")
        fItem.addActionListener(listener)
        var pItem: MenuItem = null
        if (isLiteScript) {
          pItem = new MenuItem("Purchase Pro")
          pItem.addActionListener(listener)
        }
        val aItem: MenuItem = new MenuItem("Visit ArteBots.com")
        aItem.addActionListener(listener)
        popup.add(sub)
        popup.add(sItem)
        popup.add(fItem)
        if (isLiteScript) popup.add(pItem)
        popup.add(aItem)
        trayIcon = new TrayIcon(image, "Artemis Productions", popup)
        iconListener = new ActionListener
        {
          def actionPerformed(e: ActionEvent) {
            openURL(messageUrl)
          }
        }
        trayIcon.addActionListener(iconListener)
        tray.add(trayIcon)
        return true
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
    false
  }

  def sendNotification(text: String, messageType: TrayIcon.MessageType) {
    try {
      trayIcon.displayMessage("Artemis Productions", text, messageType)
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
  }

  def setNotifs(levelups: Boolean,
                hourly: Boolean,
                bihourly: Boolean,
                nameHeard: Boolean,
                deaths: Boolean,
                onFinish: Boolean) {
    notifs(Notifiers.LEVEL_UPS) = levelups
    notifs(Notifiers.BIHOURLY) = bihourly
    notifs(Notifiers.HOURLY) = !bihourly && hourly
    notifs(Notifiers.NAME_HEARD) = nameHeard
    notifs(Notifiers.DEATHS) = deaths
    notifs(Notifiers.ON_FINISH) = onFinish
  }

  def openURL(url: String) {
    if (!Desktop.isDesktopSupported) {
      return
    }
    val desktop: Desktop = Desktop.getDesktop
    if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
      return
    }
    try {
      val uri: URI = new URI(url)
      desktop.browse(uri)
    } catch {
      case ignored: Exception => {
      }
    }
  }

  private var feedbackUrl    : String           = "http://rsbuddy.com/forum/showthread.php?t="
  private var purchaseUrl    : String           = "http://artebots.com"
  private val isLiteScript   : Boolean          = false
  private var messageUrl     : String           = "http://artebots.com"
  private var messageOfTheDay: String           = "No news from Artemis Productions today!"
  private val artebots       : String           = "http://artebots.com"
  private var trayIcon       : TrayIcon         = null
  private var startMillis    : Long             = 0L
  private var lastMillis     : Long             = 0L
  private var gns            : Boolean          = false
  private var gnu            : Boolean          = false
  private var gnh            : Boolean          = false
  private var iconListener   : ActionListener   = null
  private val hTimer         : Timer            = new Timer(3600000)
  private val bTimer         : Timer            = new Timer(1800000)
  private val notifs         : Array[Boolean]   = new Array[Boolean](6)
  private var lItem          : CheckboxMenuItem = null
  private var hItem          : CheckboxMenuItem = null
  private var bItem          : CheckboxMenuItem = null
  private var nItem          : CheckboxMenuItem = null
  private var dItem          : CheckboxMenuItem = null
  private var oItem          : CheckboxMenuItem = null

  object Notifiers
  {
    val LEVEL_UPS : Int = 0
    val HOURLY    : Int = 1
    val BIHOURLY  : Int = 2
    val NAME_HEARD: Int = 3
    val DEATHS    : Int = 4
    val ON_FINISH : Int = 5
  }

  object State
  {
    val SLEEP = 1
  }

}

