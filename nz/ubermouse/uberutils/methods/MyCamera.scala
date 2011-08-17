package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Calculations
import com.rsbuddy.script.methods.Camera
import com.rsbuddy.script.util.Random
import com.rsbuddy.script.wrappers.GameObject
import com.rsbuddy.script.wrappers.Npc
import com.rsbuddy.script.wrappers.Tile
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 2:10 PM
 * Package: nz.artedungeon.utils;
 */
object MyCamera
{
   /**
    * Turns the Camera to specified <tt>Tile</tt>
    * @param tile the <tt>Tile</tt> to turn to
    * @param deviation max amount of deviation to add to angle
    */
   def turnTo(tile: Tile, dev: Int) {
      val angle: Int = Camera.getAngleTo(tile)
      val devation = if (dev < 16) 16 else dev
      val amount = if(angle > Camera.getCompassAngle) 15 else -15
         val times: Int = Camera.getAngleTo(Camera.getAngleTo(tile)) / 15
         for(i <- 0 to times) {
            if (!Calculations.isTileOnScreen(tile))
            Camera.setCompassAngle(Camera.getCompassAngle + amount)
         }
         Camera.setCompassAngle(Camera.getCompassAngle + Random.nextInt(15, dev))
   }

   /**
    * Turns the Camera to specified <tt>Tile</tt>
    * @param tile the <tt>Tile</tt> to turn to
    */
   def turnTo(tile: Tile) {
      turnTo(tile, 0)
   }

   /**
    * Turns the Camera to specified <tt>Tile</tt>
    * @param npc the <tt>Npc</tt> to turn to
    */
   def turnTo(npc: Npc) {
      turnTo(npc.getLocation)
   }

   /**
    * Turns the Camera to specified <tt>Tile</tt>
    * @param npc the <tt>Npc</tt> to turn to
    * @param deviation max amount of deviation to add to angle
    */
   def turnTo(npc: Npc, deviation: Int) {
      turnTo(npc.getLocation, deviation)
   }

   /**
    * Turns the Camera to specified <tt>Tile</tt>
    * @param object the <tt>GameObject</tt> to turn to
    */
   def turnTo(`object` : GameObject) {
      turnTo(`object`.getLocation)
   }

   /**
    * Turns the Camera to specified <tt>Tile</tt>
    * @param object the <tt>GameObject</tt> to turn to
    * @param deviation max amount of deviation to add to angle
    */
   def turnTo(`object` : GameObject, deviation: Int) {
      turnTo(`object`.getLocation, deviation)
   }
}