package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Objects
import com.rsbuddy.script.wrappers.GameObject
import com.rsbuddy.script.wrappers.Tile
/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/6/11
 * Time: 9:21 PM
 * Package: nz.artedungeon.utils;
 */
object MyObjects
{
   def getTopAt(tile: Tile, ids: Int*): GameObject = {
      val objects: Array[GameObject] = Objects.getAllAt(tile)
      for (o <- objects) {
         for (id <- ids) if (o.getId == id) return o
      }
      null
   }
}