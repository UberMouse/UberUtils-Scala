package nz.ubermouse.uberutils.methods

import org.rsbuddy.tabs.Equipment
import com.rsbuddy.script.methods.Widgets
import com.rsbuddy.script.wrappers.Item

object MyEquipment
{
   def getItems: Array[Item] = Equipment.getItems.filter(i => i.getId > 0)

   private def getCount: Int = {
      getItems.length
   }

   def getCachedItems: Array[Item] = {
      Equipment.getCachedItems
   }

   /**
    * Gets the item from Equipment cache.
    *
    * @param EquipmentIndex the Equipment index
    * @return the item
    */
   def getItem(EquipmentIndex: Int): Item = {
      new Item(Widgets.getComponent(INTERFACE_Equipment, EquipmentIndex))
   }

   /**
    * Check if cached equipment contains item
    *
    * @param name name of item to check for
    * @return true if equipment contains item
    */
   def contains(name: String): Boolean = {
      contains(name, false)
   }

   /**
    * Check if equipment contains item
    *
    * @param name   name of item to check for
    * @param cached check cached equipment or not
    * @return true if equipment contains item
    */
   def contains(name: String, cached: Boolean): Boolean = {
      for (item <- if (cached) getCachedItems else Equipment.getItems) {
         if (item.getName.toLowerCase.contains(name.toLowerCase)) return true
      }
      false
   }

   val ITEM_SLOTS         : Int = Equipment.NUM_SLOTS
   val INTERFACE_Equipment: Int = Equipment.WIDGET
   val HELMET             : Int = Equipment.HELMET
   val CAPE               : Int = Equipment.CAPE
   val NECK               : Int = Equipment.NECK
   val WEAPON             : Int = Equipment.WEAPON
   val BODY               : Int = Equipment.BODY
   val SHIELD             : Int = Equipment.SHIELD
   val LEGS               : Int = Equipment.LEGS
   val HANDS              : Int = Equipment.HANDS
   val FEET               : Int = Equipment.FEET
   val RING               : Int = Equipment.RING
   val AMMO               : Int = Equipment.AMMO
}