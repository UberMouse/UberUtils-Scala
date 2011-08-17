package nz.ubermouse.uberutils.helpers.helpertraits

import com.rsbuddy.script.methods._
import com.rsbuddy.script.task.Task
import com.rsbuddy.script.util.Random
import com.rsbuddy.script.wrappers._
import com.rsbuddy.script.wrappers.Component
import javax.imageio.ImageIO
import java.awt._
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.logging.Logger
import org.rsbuddy.net.{GeItem, WorldData}
import java.net.URI
import nz.ubermouse.uberutils.methods.{MyInventory, MyEquipment}

trait Utils
{
   /**
    * Is item noted (Returns false for items no on GE
    *
    * @param id item id to check
    * @return true if item is noted
    */
   def isNoted(id: Int): Boolean = {
      try {
         return GeItem.lookup(id) == null
      } catch {
         case e: Exception => {
            e.printStackTrace()
         }
      }
      false
   }

   /**
    * Saves an Image to the location provided
    * @param image Image to save to disk
    * @param location location to save Image to disk
    */
   def saveImage(image: Image, location: String) {
      saveImage(image, location, null)
   }

   /**
    * Saves an Image to the location and filetype provided
    * @param image Image to save to disk
    * @param location location to save Image to disk
    * @param type filetype of image
    */
   def saveImage(image: Image, location: String, `type` : String) {
      val bufferedImage: BufferedImage = new BufferedImage(image.getWidth(null),
                                                           image.getHeight(null),
                                                           BufferedImage.TYPE_INT_RGB)
      val painter: Graphics2D = bufferedImage.createGraphics
      painter.drawImage(image, null, null)
      val outputImg: File = new File(location)
      if (!outputImg.exists) try {
         outputImg.createNewFile
      } catch {
         case e: IOException => {
            e.printStackTrace()
         }
      }
      try {
         ImageIO.write(bufferedImage, if ((`type` != null)) `type` else "jpg", outputImg)
      } catch {
         case e: IOException => {
            e.printStackTrace()
         }
      }
   }

   /**
    * Generate random number between two numbers, inclusive of lower bound and exclusive of upper
    * @param min min number to generate
    * @param max roof of generated number
    * @return random number between given parameters
    */
   def random(min: Int, max: Int) = Random.nextInt(min, max)

   /**
    * Finds if the current world is a members world or not.
    *
    * @return <tt>true</tt> if the current world is a members world.
    * @author UberMouse
    */
   def isWorldMembers: Boolean = {
      var world = 0
      try {
         world = Widgets.get(550).getComponent(19).getText.replaceAll("[^0-9]", "").toInt
      } catch {
         case e: NumberFormatException => {
            Game.openTab(Game.TAB_FRIENDS)
            Game.openTab(Game.TAB_INVENTORY)
            isWorldMembers
         }
      }
      WorldData.lookup(world).isMember
   }

   /**
    * Determines if the script is current running in Developer mode
    *
    * @return true if in dev mode
    */
   def isDevMode: Boolean = Environment.getUsername == null

   /**
    * Log text
    *
    * @param text info to be logged
    */
   def log(text: AnyRef) {
      log.info("" + text)
   }

   def getNearestNonWallTile(tile: Tile): Tile = getNearestNonWallTile(tile, false)

   def getNearestNonWallTile(tile: Tile, eightTiles: Boolean): Tile = {
      val checkTiles: Array[Tile] = getSurroundingTiles(tile, eightTiles)
      val flags: Array[Int] = getSurroundingCollisionFlags(tile, eightTiles)
      for (i <- 0 to flags.length) {
         if ((flags(i) & WALL) == 0) return checkTiles(i)
      }
      null
   }

   def getSurroundingTiles(tile: Tile): Array[Tile] = getSurroundingTiles(tile, false)

   def getSurroundingTiles(tile: Tile, eightTiles: Boolean): Array[Tile] = {
      val x: Int = tile.getX
      val y: Int = tile.getY
      val north: Tile = new Tile(x, y + 1)
      val east: Tile = new Tile(x + 1, y)
      val south: Tile = new Tile(x, y - 1)
      val west: Tile = new Tile(x - 1, y)
      var northEast: Tile = null
      var southEast: Tile = null
      var southWest: Tile = null
      var northWest: Tile = null
      if (eightTiles) {
         northEast = new Tile(x + 1, y + 1)
         southEast = new Tile(x + 1, y - 1)
         southWest = new Tile(x - 1, y - 1)
         northWest = new Tile(x - 1, y + 1)
         return Array[Tile](north, northEast, east, southEast, south, southWest, west, northWest)
      }
      Array[Tile](north, east, south, west)
   }

   def getDiagonalTiles(tile: Tile): Array[Tile] = {
      val x: Int = tile.getX
      val y: Int = tile.getY
      var northEast: Tile = null
      var southEast: Tile = null
      var southWest: Tile = null
      var northWest: Tile = null
      northEast = new Tile(x + 1, y + 1)
      southEast = new Tile(x + 1, y - 1)
      southWest = new Tile(x - 1, y - 1)
      northWest = new Tile(x - 1, y + 1)
      Array[Tile](northEast, southEast, southWest, northWest)
   }

   def getSurroundingCollisionFlags(tile: Tile): Array[Int] = getSurroundingCollisionFlags(tile, false)

   def getSurroundingCollisionFlags(tile: Tile, eightTiles: Boolean): Array[Int] = {
      val flags: Array[Array[Int]] = Walking.getCollisionFlags(Game.getFloorLevel)
      val x: Int = tile.getX
      val y: Int = tile.getY
      val xOff: Int = x - Game.getMapBase.getX - Walking.getCollisionOffset(Game.getFloorLevel).getX
      val yOff: Int = y - Game.getMapBase.getY - Walking.getCollisionOffset(Game.getFloorLevel).getY
      val fNorth: Int = flags(xOff)(yOff + 1)
      val fEast: Int = flags(xOff + 1)(yOff)
      val fSouth: Int = flags(xOff)(yOff - 1)
      val fWest: Int = flags(xOff - 1)(yOff)
      if (eightTiles) {
         val fNorthEast = flags(xOff + 1)(yOff + 1)
         val fSouthEast = flags(xOff + 1)(yOff - 1)
         val fSouthWest = flags(xOff - 1)(yOff - 1)
         val fNorthWest = flags(xOff - 1)(yOff + 1)
         return Array[Int](fNorth, fNorthEast, fEast, fSouthEast, fSouth, fSouthWest, fWest, fNorthWest)
      }
      Array[Int](fNorth, fEast, fSouth, fWest)
   }

   def getCollisionFlagAtTile(tile: Tile): Int = {
      if (!Walking.isLoaded(tile)) return -1
      val flags: Array[Array[Int]] = Walking.getCollisionFlags(Game.getFloorLevel)
      val x: Int = tile.getX
      val y: Int = tile.getY
      val xOff: Int = x - Game.getMapBase.getX - Walking.getCollisionOffset(Game.getFloorLevel).getX
      val yOff: Int = y - Game.getMapBase.getY - Walking.getCollisionOffset(Game.getFloorLevel).getY
      try {
         return flags(xOff)(yOff)
      } catch {
         case e: ArrayIndexOutOfBoundsException => {
            log("Tile: " + tile)
            log("xOff: " + xOff + " yOff: " + yOff)
            log("Mapbase: " + Game.getMapBase)
            log("Collision offset: " + Walking.getCollisionOffset(Game.getFloorLevel))
            log("Flags arraycount: " + flags.length + " Flag subarray length: " + flags(0).length)
         }
      }
      -1
   }

   def getLoadedTiles: Array[Tile] = {
      val flags: Array[Array[Int]] = Walking.getCollisionFlags(Game.getFloorLevel)
      val t: ArrayList[Tile] = new ArrayList[Tile]
      for (i <- 0 to flags.length) {
         val xOff: Int = i + Game.getMapBase.getX + Walking.getCollisionOffset(Game.getFloorLevel).getX
         for (j <- 0 to flags(i).length) {
            val yOff: Int =
               j +
               Game.getMapBase.getY +
               Walking.getCollisionOffset(Game.getFloorLevel).getY
            t.add(new Tile(xOff, yOff))
         }
      }
      t.toArray(new Array[Tile](t.size))
   }

   def playerHas(name: String): Boolean = playerHas(name, false)

   def playerHas(name: String, cached: Boolean): Boolean = MyInventory.contains(name) ||
                                                           MyEquipment.contains(name, cached)

   def openURL(url: String) {
      if (!java.awt.Desktop.isDesktopSupported)
         return
      val desktop: Desktop = java.awt.Desktop.getDesktop
      if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE))
         return
      try {
         desktop.browse(new URI(url))
      } catch {
         case ignored: Exception => {
         }
      }
   }

   def parseTime(millis: Long): String = {
      val time: Long = millis / 1000
      var seconds: String = Integer.toString((time % 60).asInstanceOf[Int])
      var minutes: String = Integer.toString(((time % 3600) / 60).asInstanceOf[Int])
      var hours: String = Integer.toString((time / 3600).asInstanceOf[Int])
      for (i <- 0 until 2) {
         {
            if (seconds.length < 2) {
               seconds = "0" + seconds
            }
            if (minutes.length < 2) {
               minutes = "0" + minutes
            }
            if (hours.length < 2) {
               hours = "0" + hours
            }
         }
      }
      hours + ":" + minutes + ":" + seconds
   }

   /**
    * Check if skill is boosted
    *
    * @param skill skill to check
    * @return true if skill is boosted
    */
   def isBoosted(skill: Int): Boolean = {
      isBoosted(skill, true)
   }

   /**
    * Check if skill is boosted
    *
    * @param skill      skill to check
    * @param boostEarly boost skill before it's fully unboosted
    * @return true if skill is boosted
    */
   def isBoosted(skill: Int, boostEarly: Boolean): Boolean = {
      if (boostEarly) Skills.getCurrentLevel(skill) >=
                             (Math.ceil(Skills.getRealLevel(skill) * 1.05 + 3).asInstanceOf[Int])
      else Skills.getCurrentLevel(skill) >= Skills.getRealLevel(skill)
   }

   /**
    * Sleep script
    *
    * @param min min time to sleep
    * @param max max time to sleep
    */
   def sleep(min: Int, max: Int) {
      Task.sleep(min, max)
   }

   /**
    * Sleep script
    *
    * @param time time to sleep
    */
   def sleep(time: Int) {
      Task.sleep(time)
   }

   /**
    * Debugs text along with function caller and line numbers.
    *
    * @param text the text
    */
   def debug(text: AnyRef) {
      if (true) {
         var className: String = Thread.currentThread.getStackTrace()(2).getClassName
         if (className.contains("$")) className = className.split("\\$")(1)
         val stackTraceElements: Array[StackTraceElement] = Thread.currentThread.getStackTrace
         val stacktrace: StackTraceElement = stackTraceElements(2)
         val methodName: String = stacktrace.getMethodName
         val lineNumber: Int = stacktrace.getLineNumber
         log.info("[" +
                  stackTraceElements(3).getClassName +
                  "#" +
                  stackTraceElements(3).getMethodName +
                  ":" +
                  stackTraceElements(3).getLineNumber +
                  "] -> [" +
                  className +
                  "#" +
                  methodName +
                  ":" +
                  lineNumber +
                  "] -> " +
                  text)
      }
   }

   /**
    * Returns a Widget containing the search text
    *
    * @param text the text to search for
    * @return Widget if found, else null
    */
   def getWidgetWithText(text: String): Component = {
      for (widget <- Widgets.getLoaded) {
         for (comp <- widget.getComponents) {
            if (comp.getText.toLowerCase.contains(text.toLowerCase)) return comp
            for (comp2 <- comp.getComponents) {
               if (comp2.getText.toLowerCase.contains(text.toLowerCase)) return comp2
            }
         }
      }
      null
   }

   /**
    * Perform p/h calculation
    *
    * @param num       number to calculate p/h on
    * @param startTime starttime to use
    * @return p/h calculation
    */
   def calcPH(num: Int, startTime: Long): Int = {
      ((num) * 3600000D / (System.currentTimeMillis - startTime)).asInstanceOf[Int]
   }

   /**
    * Click Item
    *
    * @param item to click
    */
   def clickItem(item: Item) {
      Mouse.move(item.getComponent.getCenter)
      Mouse.moveRandomly(0, 4)
      Mouse.click(true)
   }

   /**
    * Get random Tile in Area
    *
    * @param area Area to get Tile in
    * @return random Tile in Area
    */
   def getRandomTile(area: Area): Tile = {
      val tiles: Array[Array[Tile]] = area.getTiles
      val y: Int = random(0, tiles.length - 1)
      val x: Int = random(0, tiles(y).length - 1)
      tiles(x)(y)
   }

   private val WALL: Int    = 0x200000
   private val log : Logger = Logger.getAnonymousLogger
}