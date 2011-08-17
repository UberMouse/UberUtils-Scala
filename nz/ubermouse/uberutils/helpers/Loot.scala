package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.methods.Calculations
import com.rsbuddy.script.methods.GroundItems
import com.rsbuddy.script.util.Filter
import com.rsbuddy.script.wrappers.GroundItem
import com.rsbuddy.script.wrappers.Tile
import org.rsbuddy.tabs.Inventory
import java.awt._
import nz.ubermouse.uberutils.methods.{MyInventory, MyMovement}
import collection.mutable.{ListBuffer, HashMap}
import tasks.PriceThread

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/27/11
 * Time: 3:28 PM
 * Package: nz.artezombies.utils;
 */
object Loot
{
  private val lootCounts = new HashMap[Int, Int]
  private val lootNames  = new HashMap[Int, String]

  def takeLoot(filter: Filter[GroundItem]) {
    val allLoot = GroundItems.getLoaded(filter)
    val stackable = allLoot.filter(l => {
      l.getItem.getDefinition.getStackType == 1 && MyInventory.contains(l.getItem.getName)
    }).length > 0
    val count = MyInventory.getItems.filter(i => MyPlayer.isEdible(i)).length
    if (Inventory.isFull &&
        !stackable &&
        count <= 5) return
    val loot = if (stackable) allLoot
                              .filter(l => l.getItem.getDefinition.getStackType == 1)
                              .reduceLeft((elem, result) => {
      if (Calculations.distanceTo(elem) < Calculations.distanceTo(result))
        elem
      else
        result
    })
    else
      allLoot.reduceLeft((elem, result) => {
        if (Calculations.distanceTo(elem) < Calculations.distanceTo(result))
          elem
        else
          result
      })
    if (count > 5 && Inventory.isFull && !MyInventory.contains(loot.getItem.getName)) MyPlayer.eat()
    if (loot.getItem != null) {
      val name: String = loot.getItem.getName
      val id = loot.getItem.getId
      MyMovement.turnTo(loot.getLocation)
      if (loot.interact("Take " + name)) {
        if (lootCounts.contains(id))
          lootCounts += id -> (lootCounts(id) + 1)
        else {
          lootCounts += id -> 0
          lootNames += id -> name
        }
        totalPrice += {
          if (PriceThread.priceForId(id) > 0) PriceThread.priceForId(id) * loot.getItem.getStackSize else 0
        }
        val iCount: Int = Inventory.getCount
        Wait.While(iCount == Inventory.getCount) {}
      }
    }
    else {
      if (loot.interact("Take ")) {
        val iCount: Int = Inventory.getCount
        Wait.While(iCount == Inventory.getCount) {}
      }
    }
  }

  def takeLoot(ids: Int*) {
    takeLoot(new Filter[GroundItem]
    {
      def accept(t: GroundItem): Boolean = {
        if (ids.contains(t.getItem.getId)) true
        else false
      }
    })
  }

  def takeLootTile(tile: Tile, filter: Filter[GroundItem]) {
  }

  def takeLootTile(tile: Tile, ids: Int*) {
    takeLootTile(tile, new Filter[GroundItem]
    {
      def accept(t: GroundItem): Boolean = {
        if (ids.contains(t.getItem.getId)) true
        else false
      }
    })
  }

  def repaint(g: Graphics2D) {
    val items: Array[GroundItem] = GroundItems.getLoaded(paintFilter)
    val ids: Array[Int] = new Array[Int](items.length)
    var offset: Int = 0
    val tiles = new ListBuffer[Tile]
    var i: Int = 0
    while (i < items.length) {
      val item: GroundItem = items(i)
      if (!tiles.contains(item.getLocation)) tiles += item.getLocation
      ids(i) = item.getItem.getId
      i += 1;
    }
    for (tile <- tiles) {
      PaintUtils.drawTile(g, tile, new Color(0, 51, 204, 50), Color.WHITE)
      for (item <- GroundItems.getAllAt(tile) if (ids.contains(item.getItem.getId))) {
        lootInfo(g, item, offset)
        offset += 15
      }
      offset = 0
    }
  }

  def lootInfo(g: Graphics2D, loot: GroundItem, offset: Int): Unit = {
    if (!Calculations.isTileOnScreen(loot.getLocation)) return
    var lootString: String = loot.getItem.getName + " (" + loot.getItem.getStackSize + " * "
    lootString += {
      if ((PriceThread.priceForId(loot.getItem.getId) == -1)) "Calculating.."
      else if ((PriceThread.priceForId(loot.getItem.getId) == -2)) "Unknown"
      else PriceThread.priceForId(loot.getItem.getId)
    }
    lootString += ")"
    val color1: Color = new Color(0, 51, 204, 50)
    val color2: Color = new Color(0, 0, 0)
    val color3: Color = new Color(255, 255, 255)
    val stroke1: BasicStroke = new BasicStroke(1)
    val font1: Font = new Font("Arial", 0, 9)
    val fm: FontMetrics = g.getFontMetrics(font1)
    val stringWidth: Int = fm.stringWidth(lootString) + 6
    val point: Point = Calculations.tileToScreen(loot.getLocation, 0.5, 0.5, 0)
    g.setColor(color1)
    g.fillRect(point.x, point.y + offset, stringWidth, 15)
    g.setColor(color2)
    g.setStroke(stroke1)
    g.drawRect(point.x, point.y + offset, stringWidth, 15)
    g.setFont(font1)
    g.setColor(color3)
    g.drawString(lootString, point.x + 3, point.y + 10 + offset)
  }

  def isPaintValid: Boolean = {
    GroundItems.getLoaded(filter).length > 0
  }

  private def idToName(id: Int): String = {
    if (lootNames.contains(id)) lootNames.get(id).asInstanceOf[String] else null
  }

  def addLoot(id: Int, name: String, price: Int) {
    lootNames += id -> name
    lootCounts += id -> 0
    PriceThread.addPrice(id, price)
  }

  def setFilter(filter: Filter[GroundItem]) {
    Loot.filter = filter
  }

  def setPaintFilter(filter: Filter[GroundItem]) {
    Loot.paintFilter = filter
  }

  def shouldLoot: Boolean = {
    var stackable: Boolean = false
    for (g <- GroundItems.getLoaded(filter)) if (g.getItem.getDefinition.getStackType == 1 &&
                                                 MyInventory.contains(g.getItem.getName)) stackable = true
    var count: Int = 0
    try {
      for (i <- MyInventory.getItems) if (MyPlayer.isEdible(i)) ({
        count += 1;
        count
      })
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
    GroundItems.getNearest(filter) != null && (!Inventory.isFull || stackable || count > 5)
  }

  var totalPrice: Int = 0
  private var filter     : Filter[GroundItem] = new Filter[GroundItem]
  {
    def accept(groundItem: GroundItem): Boolean = {
      true
    }
  }
  private var paintFilter: Filter[GroundItem] = new Filter[GroundItem]
  {
    def accept(groundItem: GroundItem): Boolean = {
      true
    }
  }
}