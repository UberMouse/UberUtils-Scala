package nz.ubermouse.uberutils.helpers.helpertraits

import com.rsbuddy.script.util.Filter
import com.rsbuddy.script.wrappers._
import org.rsbuddy.tabs.Inventory
import com.rsbuddy.script.methods.{Npcs, Players, Combat}
import nz.ubermouse.uberutils.methods.{MyInventory, MyMovement}
import nz.ubermouse.uberutils.helpers.{Utils, Potions, Wait}

trait MyPlayer
{
   /**
    * Location.
    *
    * @return the player location
    */
   def location: Tile = get.getLocation


   /**
    * Eat food.
    *
    * @param food the food ids
    */
   def eat(food: Array[Int]) {
      if (Inventory.containsOneOf(food: _*)) Inventory.getItem(food: _*).interact("Eat")
   }

   /**
    * Eat food.
    */
   def eat() {
      val food: Item = edibleItem
      if (food != null) food.click(true)
   }

   /**
    * Returns player hp percent.
    *
    * @return hp percent remaining
    */
   def hp: Int = Combat.getHealth

   /**
    * In area.
    *
    * @param area the area
    * @return true, if successful
    */
   def inArea(area: Area): Boolean = area.contains(location)

   /**
    * Attack.
    *
    * @param enemy the enemy
    * @return boolean true if enemy was attacked or false if attacking failed
    */
   def attack(enemy: Npc): Boolean = {
      MyMovement.turnTo(enemy)
      if (enemy.interact("Attack")) {
         Wait.While(!inCombat, 30){}
      }
      false
   }

   /**
    * Get prayer points.
    *
    * @return current prayer points
    */
   def prayer: Int = Combat.getPrayerPoints

   /**
    * In combat.
    *
    * @param multi in multi combat area
    * @return true, if in combat
    */
   def inCombat(multi: Boolean): Boolean = {
      if (multi) isInteracting && interacting.getHpPercent > 0
      else isInteracting &&
           interacting.getHpPercent > 0 &&
           (getInteractor != null && (interacting == getInteractor) || getInteractor == null)
   }

   /**
    * In combat.
    *
    * @return true, if in combat
    */
   def inCombat: Boolean = inCombat(false)


   /**
    * Checks if is interacting.
    *
    * @return true, if is interacting
    */
   def isInteracting: Boolean = get.getInteracting != null

   /**
    * Return interacting NPC as Npc.
    *
    * @return the Npc
    */
   def interacting: Npc = get.getInteracting.asInstanceOf[Npc]

   /**
    * Gets the player.
    *
    * @return the player RSPlayer
    */
   def get: Player = Players.getLocal.asInstanceOf[Player]

   /**
    * Checks if player is moving.
    *
    * @return true, if is moving
    */
   def isMoving: Boolean = get.isMoving

   /**
    * Generic level to eat at
    *
    * @return true, if player hp is too low
    */
   def needToEat: Boolean = hp < 50

   /**
    * Check if inventory contains potions
    *
    * @return true if any potions were found
    */
   def hasPotions: Boolean = Potions.values.exists(p => MyInventory.contains(p.getName))

   /**
    * Drink any potions in inventory for unboosted skills
    */
   def drinkPotions() {
      if (!hasPotions) return
      for (potion <- Potions.values.filter(p => Utils.isBoosted(p.skill) && MyInventory.contains(p.getName))) {
         MyInventory.getItem(potion.getName).click(true)
         Utils.sleep(Utils.random(400, 500))
      }
   }

   /**
    * Should repot
    *
    * @return true if should repot
    */
   def shouldPot: Boolean = Potions.values.filter(p => Utils.isBoosted(p.skill) && MyInventory.contains(p.getName)).length > 0


   /**
    * Gets edible item from Inventory
    *
    * @return Item if one is found, else null
    */
   def edibleItem: Item = MyInventory.getItems(true).find(isEdible(_)).asInstanceOf[Item]

   def isEdible(item: Item): Boolean = {
      if (item == null ||
          item.getComponent.getActions == null ||
          item.getComponent.getActions.length < 1 ||
          item.getComponent.getActions()(0) == null) return false
      item.getComponent.getActions()(0).contains("Eat")
   }

   /**
    * Gets npc interating with you
    *
    * @return npc interacting with local player
    */
   def getInteractor: Npc = {
      Npcs.getNearest(new Filter[Npc]
      {
         def accept(npc: Npc): Boolean = {
            if (npc.getInteracting == null) return false
            npc.getInteracting == get
         }
      })
   }

   def animation = get.getAnimation
}