package nz.ubermouse.uberutils.paint.paints

import com.rsbuddy.script.methods.Game
import com.rsbuddy.script.methods.Mouse
import javax.swing._
import java.awt._
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.HashMap
import java.util.Iterator
import nz.ubermouse.uberutils.paint.PaintController
import nz.ubermouse.uberutils.paint.components._
import nz.ubermouse.uberutils.helpers.{Skill, Options, Utils, IPaint}
import collection.mutable.ListBuffer

class UberPaint(name: String, threadId: Int, version: Double) extends IPaint
{

   import UberPaint._

   protected var showPaint: Boolean = true
   var infoColumnValues: Array[String] = new Array[String](0)
   var infoColumnData  : Array[String] = new Array[String](0)
   protected final val frames     = new HashMap[String, PFrame]
   protected final val buttons    = new HashMap[String, PButton]
   protected final val checkBoxes = new HashMap[String, PCheckBox]
   var menuIndex   : Int = 0
   var subMenuIndex: Int = 0

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


   def togglePaint() {
      showPaint = !showPaint
   }

   def keyTyped(keyEvent: KeyEvent) {
      PaintController.keyTyped(keyEvent)
   }

   def keyPressed(keyEvent: KeyEvent) {
      PaintController.keyPressed(keyEvent)
   }

   def keyReleased(keyEvent: KeyEvent) {
      PaintController.keyReleased(keyEvent)
   }

   def mouseClicked(mouseEvent: MouseEvent) {
      PaintController.mouseClicked(mouseEvent)
   }

   def mousePressed(mouseEvent: MouseEvent) {
      PaintController.mousePressed(mouseEvent)
   }

   def mouseReleased(mouseEvent: MouseEvent) {
      PaintController.mouseReleased(mouseEvent)
   }

   def mouseEntered(mouseEvent: MouseEvent) {
      PaintController.mouseEntered(mouseEvent)
   }

   def mouseExited(mouseEvent: MouseEvent) {
      PaintController.mouseExited(mouseEvent)
   }

   def mouseDragged(mouseEvent: MouseEvent) {
      PaintController.mouseDragged(mouseEvent)
   }

   def mouseMoved(mouseEvent: MouseEvent) {
      PaintController.mouseMoved(mouseEvent)
   }

   def paint(graphics: Graphics): Boolean = {
      if (!Game.isLoggedIn) return false
      try {
         val g: Graphics2D = graphics.asInstanceOf[Graphics2D]
         var clayout: PColumnLayout = null
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
         if (checkBoxes.size <= 6) firstColumn.putAll(checkBoxes)
         else {
            {
               var i: Int = 0
               while (i < checkBoxes.size) {
                  {
                     if (i <= 5) {
                        var text: String = null
                        val it = checkBoxes.keySet.iterator
                        var j = 0
                        while (j < i) {
                           it.next
                           j += 1
                        }
                        text = it.next.asInstanceOf[String]
                        var length: Int = SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont), text)
                        if (length > bestLength) bestLength = length
                        firstColumn.put(text, checkBoxes.get(text))
                     }
                     else {
                        var text: String = null
                        val it: Iterator[_] = checkBoxes.keySet.iterator
                        var j: Int = 0
                        while (j < i) {
                           it.next
                           j += 1
                        }
                        text = it.next.asInstanceOf[String]
                        secondColumn.put(text, checkBoxes.get(text))
                     }
                  }
                  ({
                     i += 1;
                     i
                  })
               }
            }
         }
         secondColx = 8 + bestLength
//         firstLayout = new PCheckBoxLayout(8,
//                                             362,
//                                             firstColumn.keySet.toArray(new Array[String](if (firstColumn.size > 6) 6
//                                             else firstColumn.size)),
//                                             firstColumn
//                                             .values
//                                             .toArray(new Array[PCheckBoxLayout](if (firstColumn.size > 6) 6
//                                             else firstColumn.size)),
//                                             new Font("Arial", 0, 11),
//                                             PCheckBoxLayout.ColorScheme.WHITE)
//         secondLayout = new PCheckBoxLayout(secondColx + 12,
//                                              362,
//                                              secondColumn
//                                              .keySet
//                                              .toArray(new Array[String](if (secondColumn.size > 6) 6
//                                              else secondColumn.size)),
//                                              secondColumn
//                                              .values
//                                              .toArray(new Array[PCheckBoxLayout](if (secondColumn.size > 6) 6
//                                              else secondColumn.size)),
//                                              new Font("Arial", 0, 11),
//                                              PCheckBoxLayout.ColorScheme.WHITE)
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
         if (!showPaint) return false
         val infoTxt: String = name + " - " + "v" + version
         g.drawString(infoTxt, 510 - SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont), infoTxt), 468)
         var offset: Int = 0
         for (skill: Skill <- skills) {
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
      true
   }

   def addFrame(name: String) {
      addFrame(name, frames.size, -1)
   }

   def addFrame(name: String, pIndex: Int, subMIndex: Int) {
      val frame = new PFrame(name)
      {
         override def shouldPaint: Boolean = {
            if (subIndex != -1) return menuIndex == index && subMenuIndex == subIndex
            menuIndex == index
         }

         override def shouldHandleMouse: Boolean = {
            shouldPaint
         }

         private[paints] var index    = pIndex
         private[paints] var subIndex = subMIndex
      }
      frames.put(name, frame)
      PaintController.addComponent(frame)
   }

   def getFrame(name: String) = {
      frames.get(name)
   }

   def removeFrame(name: String) {
      val frame = frames.get(name)
      PaintController.removeComponent(frame)
      frames.remove(name)
   }

   def addTab(name: String, x: Int, y: Int, primaryIndex: Int) {
      addTab(name, x, y, -1, -1, primaryIndex, -1)
   }

   def addTab(name: String, x: Int, y: Int, primaryIndex: Int, subIndex: Int) {
      addTab(name, x, y, -1, -1, primaryIndex, subIndex)
   }

   def addTab(name: String, x: Int, y: Int, width: Int, height: Int, primaryIndex: Int, subIndex: Int) {
      val button: PFancyButton = new PFancyButton(x, y, width, height, name, PFancyButton.ColorScheme.GRAPHITE)
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

   def removeTab(name: String) {
      val button = buttons.get(name)
      PaintController.removeComponent(button)
      buttons.remove(name)
   }

   def addOption(text: String, option: String, state: Boolean) {
      addOption(text, new PCheckBox(0, 0, state)
      {
         override def onPress() {
            Options.flip(option)
         }
      })
   }

   def addOption(text: String, option: String) {
      addOption(text, option, Options.getBoolean(option))
   }

   def addOption(text: String, box: PCheckBox) {
      checkBoxes.put(text, box)
   }

   def removeOption(text: String) {
      checkBoxes.remove(text)
   }
}

object UberPaint
{
   private[paints] val firstColumn                   = new HashMap[String, PCheckBox]
   private[paints] val secondColumn                  = new HashMap[String, PCheckBox]
   private[paints] var firstLayout : PCheckBoxLayout = null
   private[paints] var secondLayout: PCheckBoxLayout = null
}