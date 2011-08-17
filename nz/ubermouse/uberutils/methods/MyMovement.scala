package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Calculations
import com.rsbuddy.script.methods.Walking
import com.rsbuddy.script.util.Random
import com.rsbuddy.script.wrappers.GameObject
import com.rsbuddy.script.wrappers.Npc
import com.rsbuddy.script.wrappers.Tile

object MyMovement
{
   /**
    * Turn to object.
    *
    * @param object the object
    */
   def turnTo(`object` : GameObject) {
      if (`object` == null) return
      turnTo(`object`.getLocation)
   }

   /**
    * Turn to npc.
    *
    * @param npc the npc
    */
   def turnTo(npc: Npc) {
      if (npc == null) return
      turnTo(npc.getLocation)
   }

   /**
    * Turn to tile.
    *
    * @param tile the tile
    */
   def turnTo(tile: Tile) {
      if (tile == null) return
      if (!Calculations.isTileOnScreen(tile)) {
         if (Calculations.distanceTo(tile) > 6) Walking.getTileOnMap(tile).clickOnMap
         MyCamera.turnTo(tile, Random.nextInt(10, 25))
         if (!Calculations.isTileOnScreen(tile)) Walking.getTileOnMap(tile).clickOnMap
      }
   }

   /**
    * Reverse path.
    *
    * @param other the path to reverse
    * @return the reversed path
    */
   def reversePath(other: Array[Tile]): Array[Tile] = {
      val t: Array[Tile] = new Array[Tile](other.length)
      for(i <- 0 to  other.length) {
         t(i) = other(other.length - i - 1)
      }
      t
   }
}