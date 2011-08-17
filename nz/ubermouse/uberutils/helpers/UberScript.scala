package nz.ubermouse.uberutils.helpers

import com.rsbuddy.event.listeners.PaintListener
import com.rsbuddy.script.ActiveScript
import com.rsbuddy.script.Manifest
import com.rsbuddy.script.methods.Game
import com.rsbuddy.script.methods.Mouse
import javax.swing._
import java.awt._
import java.awt.event._
import collection.mutable.{ListBuffer, HashMap}
import nz.ubermouse.uberutils.arte.ArteNotifier
import tasks.{IrcTask, ImageThread, PriceThread}
import nz.ubermouse.uberutils.paint.PaintController
import nz.ubermouse.uberutils.paint.abstracts.PComponent
import nz.ubermouse.uberutils.paint.components._

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 5:13 PM
 * Package: nz.uberutils.helpers;
 */
abstract class UberScript extends ActiveScript with
                                  PaintListener with
                                  KeyListener with
                                  MouseListener with
                                  MouseMotionListener
{

   import UberScript._

   protected       var showPaint       : Boolean       = true
   protected       var status          : String        = "Starting..."
   protected       val strategies                      = new ListBuffer[Strategy]
   protected       val skills                          = new ListBuffer[Skill]
   protected       val name            : String        = getClass.getAnnotation(classOf[Manifest]).name
   protected       var threadId        : Int           = 0
   protected       var infoColumnValues: Array[String] = new Array[String](0)
   protected       var infoColumnData  : Array[String] = new Array[String](0)
   protected final val frames                          = new HashMap[String, PFrame]
   protected final val buttons                         = new HashMap[String, PButton]
   protected final val checkBoxes                      = new HashMap[String, PCheckBox]
   protected       var paintType       : IPaint        = null
   var menuIndex   : Int = 0
   var subMenuIndex: Int = 0

   def togglePaint() {
      showPaint = !showPaint
   }

   protected def addFrame(name: String) {
      addFrame(name, frames.size, -1)
   }

   protected def addFrame(name: String, pIndex: Int, subMIndex: Int) {
      val frame: PFrame = new PFrame(name)
      {
         override def shouldPaint: Boolean = {
            if (subIndex != -1) return menuIndex == index && subMenuIndex == subIndex
            menuIndex == index
         }

         override def shouldHandleMouse: Boolean = shouldPaint

         private[helpers] var index   : Int = pIndex
         private[helpers] var subIndex: Int = subMIndex
      }
      frames.put(name, frame)
      PaintController.addComponent(frame)
   }

   protected def getFrame(name: String): PFrame = {
      frames(name)
   }

   protected def removeFrame(name: String) {
      val frame: PFrame = frames(name)
      PaintController.removeComponent(frame)
      frames.remove(name)
   }

   protected def addTab(name: String, x: Int, y: Int, primaryIndex: Int) {
      addTab(name, x, y, -1, -1, primaryIndex, -1)
   }

   protected def addTab(name: String, x: Int, y: Int, primaryIndex: Int, subIndex: Int) {
      addTab(name, x, y, -1, -1, primaryIndex, subIndex)
   }

   protected def addTab(name: String, x: Int, y: Int, height: Int, width: Int, primaryIndex: Int, subIndex: Int) {
      val button: PFancyButton = new PFancyButton(x, y, height, width, name, PFancyButton.ColorScheme.GRAPHITE)
      {
         override def onPress() {
            if (primaryIndex != -1) menuIndex = primaryIndex
            if (subIndex != -1) subMenuIndex = subIndex
         }

         override def mouseMoved(mouseEvent: MouseEvent) {
            var hovered: Boolean = false
            if (pointInButton(mouseEvent.getPoint)) hovered = true
            if (primaryIndex != -1 && menuIndex == primaryIndex) hovered = true
            if (subIndex != -1 && subMenuIndex == subIndex) hovered = true
            setHovered(hovered)
         }
      }
      buttons.put(name, button)
      PaintController.addComponent(button)
   }

   protected def removeTab(name: String) {
      val button: PButton = buttons(name)
      PaintController.removeComponent(button)
      buttons.remove(name)
   }

   protected def addOption(text: String, option: String, state: Boolean) {
      addOption(text, new PCheckBox(0, 0, state)
      {
         override def onPress() {
            Options.flip(option)
         }
      })
   }

   protected def addOption(text: String, option: String) {
      addOption(text, option, Options.getBoolean(option))
   }

   protected def addOption(text: String, box: PCheckBox) {
      checkBoxes.put(text, box)
   }

   protected def removeOption(text: String) {
      checkBoxes.remove(text)
   }

   final override def onStart: Boolean = {
      Options.setNode(name)
      getContainer.submit(new PriceThread)
      getContainer.submit(new ArteNotifier(threadId, true))
      getContainer.submit(new ImageThread(name))
      getContainer.submit(new IrcTask("#" + name))
      PaintController.reset()
      PaintController.addComponent(new PFancyButton(8, 450, "ArteBots", PFancyButton.ColorScheme.GRAPHITE)
      {
         override def onPress() {
            Utils.openURL("http://artebots.com")
         }
      })
      PaintController.addComponent(new PFancyButton(59, 450, "Feedback", PFancyButton.ColorScheme.GRAPHITE)
      {
         override def onPress() {
            Utils.openURL("http://rsbuddy.com/forum/showthread.php?t=" + threadId)
         }
      })
      PaintController.addComponent(new PFancyButton(118, 450, "Open Chat", PFancyButton.ColorScheme.GRAPHITE)
      {
         override def onPress() {
            IrcTask.instance.openGui()
            IrcTask.instance.connect()
         }
      })
      addFrame("info")
      addFrame("options")
      PaintController.addComponent(new PFancyButton(461, 481, 54, 24, "Hide", PFancyButton.ColorScheme.GRAPHITE)
      {
         override def onStart() {
            forceMouse = true
            forcePaint = true
         }

         override def onPress() {
            text = if (text == "Hide") "Show" else "Hide"
            togglePaint()
            PaintController.toggleEvents()
         }
      })
      addTab("Info", 440, 345, 73, -1, 0, -1)
      addTab("Options", 440, 365, 73, -1, 1, -1)
      PaintController.startTimer()
      onBegin
   }

   final override def onFinish() {
      Options.save()
      onEnd()
   }

   def onEnd() {
   }

   def onBegin: Boolean = {
      true
   }

   def loop: Int = {
      try {
         if (!Game.isLoggedIn) return 100
         Mouse.setSpeed(Utils.random(1, 2))
         miscLoop()
         for (strategy <- strategies) {
            if (strategy.isValid) {
               status = strategy.getStatus
               strategy.execute()
               return Utils.random(400, 500)
            }
         }
      } catch {
         case e: Exception => {
            if (Utils.isDevMode) e.printStackTrace()
         }
      }
      0
   }

   protected def miscLoop() {
   }

   def keyTyped(keyEvent: KeyEvent) {
      if (paintType != null)
         paintType.keyTyped(keyEvent)
   }

   def keyPressed(keyEvent: KeyEvent) {
      if (paintType != null)
         paintType.keyPressed(keyEvent)
   }

   def keyReleased(keyEvent: KeyEvent) {
      if (paintType != null)
         paintType.keyReleased(keyEvent)
   }

   def mouseClicked(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mouseClicked(mouseEvent)
   }

   def mousePressed(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mousePressed(mouseEvent)
   }

   def mouseReleased(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mouseReleased(mouseEvent)
   }

   def mouseEntered(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mouseEntered(mouseEvent)
   }

   def mouseExited(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mouseExited(mouseEvent)
   }

   def mouseDragged(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mouseDragged(mouseEvent)
   }

   def mouseMoved(mouseEvent: MouseEvent) {
      if (paintType != null)
         paintType.mouseMoved(mouseEvent)
   }

   def onRepaint(graphics: Graphics) {
      if (!Game.isLoggedIn) return
      try {
         val g: Graphics2D = graphics.asInstanceOf[Graphics2D]
         var clayout: PComponent = null
         try {
            clayout = new PColumnLayout(227,
                                          354,
                                          infoColumnValues,
                                          infoColumnData,
                                          new Font("Arial", 0, 9),
                                          PColumnLayout.ColorScheme.WHITE)
         } catch {
            case e: Exception => {
               e.printStackTrace()
            }
         }
         getFrame("options").removeComponent(firstLayout)
         getFrame("options").removeComponent(secondLayout)
         var secondColx: Int = -1
         var bestLength: Int = -1
         firstColumn.clear()
         secondColumn.clear()
         if (checkBoxes.size <= 6) firstColumn ++= checkBoxes
         else {
            for (i <- 0 to checkBoxes.size) {
               if (i <= 5) {
                  var text: String = null
                  val it = checkBoxes.keySet.iterator
                  for (j <- 0 to i)
                     it.next()

                  text = it.next().asInstanceOf[String]
                  val length: Int = SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont), text)
                  if (length > bestLength) bestLength = length
                  firstColumn += text -> checkBoxes(text)
               }
               else {
                  var text: String = null
                  val it = checkBoxes.keySet.iterator
                  for (j <- 0 to i)
                     it.next()
                  text = it.next().asInstanceOf[String]
                  secondColumn += text -> checkBoxes(text)
               }
            }
         }
         secondColx = 8 + bestLength
         firstLayout = new PCheckBoxLayout(8,
                                             362,
                                             firstColumn.keySet.toArray,
                                             firstColumn.values.toArray,
                                             new Font("Arial", 0, 11),
                                             PCheckBoxLayout.ColorScheme.WHITE)
         secondLayout = new PCheckBoxLayout(secondColx + 12,
                                              362,
                                              secondColumn.keySet.toArray,
                                              secondColumn.values.toArray,
                                              new Font("Arial", 0, 11),
                                              PCheckBoxLayout.ColorScheme.WHITE)
         getFrame("options").addComponent(firstLayout)
         getFrame("options").addComponent(secondLayout)
         if (showPaint) {
            val p: Paint = g.getPaint
            g.setPaint(new GradientPaint(0, 1000, new Color(55, 55, 55, 240), 512, 472, new Color(15, 15, 15, 240)))
            g.fillRect(7, 345, 505, 127)
            val loc: Point = Mouse.getLocation
            if (Mouse.isPressed) {
               g.fillOval(loc.x - 5, loc.y - 5, 10, 10)
               g.drawOval(loc.x - 5, loc.y - 5, 10, 10)
            }
            g.drawLine(0, loc.y + 1, 766, loc.y + 1)
            g.drawLine(0, loc.y - 1, 766, loc.y - 1)
            g.drawLine(loc.x + 1, 0, loc.x + 1, 505)
            g.drawLine(loc.x - 1, 0, loc.x - 1, 505)
            g.setPaint(p)
         }
         if (clayout != null) getFrame("info").addComponent(clayout)
         PaintController.onRepaint(graphics)
         if (clayout != null) getFrame("info").removeComponent(clayout)
         if (!showPaint) return
         paint(g)
         val infoTxt = name + " - " + "v" + getClass.getAnnotation(classOf[Manifest]).version
         g.drawString(infoTxt, 512 - SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont), infoTxt), 468)
         var offset: Int = 0
         for (skill <- skills) {
            if (skill.xpGained > 0) {
               val skillComp = new PSkill(8, 346 + offset, skill.getSkill, PSkill.ColorScheme.GRAPHITE)
               if (!getFrame("info").containsComponent(skillComp)) {
                  getFrame("info").addComponent(skillComp)
               }
               offset += 20
            }
         }
         if (Mouse.isPressed) {
            g.setColor(new Color(255, 252, 0, 150))
            g.setColor(new Color(255, 252, 0, 100))
         }
         else {
            g.setColor(new Color(255, 252, 0, 50))
         }
         g.setColor(new Color(0, 0, 0, 50))
      } catch {
         case ignored: Exception => {
            if (Utils.isDevMode) ignored.printStackTrace()
         }
      }
   }

   protected def paint(g: Graphics2D) {
   }
}

object UberScript
{
   private[helpers] final val firstColumn                   = new HashMap[String, PCheckBox]
   private[helpers] final val secondColumn                  = new HashMap[String, PCheckBox]
   private[helpers]       var firstLayout : PCheckBoxLayout = null
   private[helpers]       var secondLayout: PCheckBoxLayout = null
}