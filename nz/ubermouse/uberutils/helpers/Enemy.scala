package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.methods.Npcs
import com.rsbuddy.script.wrappers.Npc
import com.rsbuddy.script.util.Filter

object Enemy {
  /**
   * Get enemy hp percent.
   *
   * @return hp percent
   */
  def hp = if (npc != null) npc.getHpPercent else 0

  /**
   * Location.
   *
   * @return the enemy location
   */
  def location = if (npc != null) npc.getLocation else null


  /**
   * Checks if current enemy is valid.
   *
   * @return true, if is valid
   */
  def isValid = npc != null && hp > 0

  /**
   * Do action.
   *
   * @param action the action
   * @return true, if successful
   */
  def interact(action: String) = if (npc != null) npc.interact(action) else false

  /**
   * Pick enemy that passes supplied filter.
   *
   * @param names the valid names
   */
  def pickEnemy(f:Npc => Boolean): Npc = {
    Npcs.getNearest(new Filter[Npc] {
      def accept(npc: Npc) = {
        if (npc.getHpPercent == 0) false
        if (npc.getInteracting != null && npc.getInteracting != MyPlayer.get) false
        f(npc)
      }
    })
  }

  /**
   * Set enemy using ids.
   *
   * @param names the valid names
   */
  def setEnemy(f:Npc => Boolean) {
    npc = pickEnemy(f)
  }

  /**
   * Checks if is dead.
   *
   * @return true, if is dead
   */
  def isDead = hp == 0


  var npc: Npc = null
}