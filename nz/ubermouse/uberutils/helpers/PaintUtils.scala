package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.methods.Calculations
import com.rsbuddy.script.wrappers.GameObject
import com.rsbuddy.script.wrappers.Npc
import com.rsbuddy.script.wrappers.Tile
import com.rsbuddy.script.wrappers.Model
import java.awt._

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 8:52 PM
 * Package: nz.uberutils.helpers;
 */
object PaintUtils
{
  /**
   * To be used to draw a certain model on screen
   *
   * @param g Graphics
   * @param o The RSObject you want to 'paint'
   * @param c The Color of the fill By Kimbers
   */
  def drawModel(g: Graphics, o: GameObject, c: Color, s: String, tc: Color) {
    val model: Array[Polygon] = o.getModel.getTriangles
    val point: Point = Calculations.tileToScreen(o.getLocation)
    for (p <- model) {
      g.setColor(c)
      g.fillPolygon(p)
      g.setColor(c.darker)
      g.drawPolygon(p)
    }
    g.setColor(tc)
    g.drawString(s, point.x - 75, point.y - 35)
  }

  /**
   * To be used to draw a certain model on screen
   *
   * @param g Graphics
   * @param o The RSNPC you want to 'paint'
   * @param c The Color of the fill By Kimbers
   */
  def drawModel(g: Graphics, o: Npc, c: Color, s: String, tc: Color) {
    val model: Array[Polygon] = o.getModel.getTriangles
    val point: Point = Calculations.tileToScreen(o.getLocation)
    for (p <- model) {
      g.setColor(c)
      g.fillPolygon(p)
      g.setColor(c.darker)
      g.drawPolygon(p)
    }
    g.setColor(tc)
    g.drawString(s, point.x - 75, point.y - 35)
  }

  /**
   * Draws a tile with the passed color on the passed instance of
   * <code>Graphics</code>.
   *
   * @param render       The instance of <code>Graphics</code> you want to draw on.
   * @param tile         The instance of the tile you want to draw.
   * @param color        The color you want the drawn tile to be.
   * @param outlineColor The color you want the outline of the tile to be
   * @author Gnarly
   */
  def drawTile(render: Graphics, tile: Tile, color: Color, outlineColor: Color) {
    val southwest: Point = Calculations.tileToScreen(tile, 0, 0, 0)
    val southeast: Point = Calculations.tileToScreen(new Tile(tile.getX + 1, tile.getY), 0, 0, 0)
    val northwest: Point = Calculations.tileToScreen(new Tile(tile.getX, tile.getY + 1), 0, 0, 0)
    val northeast: Point = Calculations.tileToScreen(new Tile(tile.getX + 1, tile.getY + 1), 0, 0, 0)
    if (Calculations.isPointOnScreen(southwest) &&
        Calculations.isPointOnScreen(southeast) &&
        Calculations.isPointOnScreen(northwest) &&
        Calculations.isPointOnScreen(northeast)) {
      render.setColor(outlineColor)
      render
      .drawPolygon(Array[Int](northwest.getX.asInstanceOf[Int],
                              northeast.getX.asInstanceOf[Int],
                              southeast.getX.asInstanceOf[Int],
                              southwest.getX.asInstanceOf[Int]),
                   Array[Int](northwest.getY.asInstanceOf[Int],
                              northeast.getY.asInstanceOf[Int],
                              southeast.getY.asInstanceOf[Int],
                              southwest.getY.asInstanceOf[Int]),
                   4)
      render.setColor(color)
      render
      .fillPolygon(Array[Int](northwest.getX.asInstanceOf[Int],
                              northeast.getX.asInstanceOf[Int],
                              southeast.getX.asInstanceOf[Int],
                              southwest.getX.asInstanceOf[Int]),
                   Array[Int](northwest.getY.asInstanceOf[Int],
                              northeast.getY.asInstanceOf[Int],
                              southeast.getY.asInstanceOf[Int],
                              southwest.getY.asInstanceOf[Int]),
                   4)
    }
  }
}