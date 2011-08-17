package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Mouse
import com.rsbuddy.script.wrappers.Item
import com.rsbuddy.script.wrappers.Npc
import org.rsbuddy.tabs.Inventory
import java.util.ArrayList

object MyInventory
{
   def getItems(cached: Boolean): Array[Item] = {
      if (getCount <= 0) return new Array[Item](0)
      val items: ArrayList[Item] = new ArrayList[Item]
      for (item <- if (cached) Inventory.getCachedItems else Inventory.getItems) {
         if (item != null && item.getId > 0) items.add(item)
      }
      items.toArray(new Array[Item](items.size))
   }

   def getItems: Array[Item] = {
      getItems(false)
   }

   def getCount: Int = {
      Inventory.getItems.filter(i => i.getId > 0).length
   }

   def getCount(name: String): Int = {
      Inventory.getItems.filter(i => i.getId > 0 && i.getName.contains(name)).length
   }

   def getCount(id: Int): Int = {
      Inventory.getItems.filter(i => i.getId > 0 && i.getId == id).length
   }

   def isFull: Boolean = {
      getCount == 28
   }

   def getItem(id: Int): Item = {
      Inventory.getItem(id)
   }

   /**
    * Check if inventory contains item
    *
    * @param id     id of item to check for
    * @param cached use cached inventory
    * @return true if item in inventory
    */
   def contains(id: Int, cached: Boolean): Boolean = {
      for (item <- getItems(cached)) {
         if (item.getId == id) return true
      }
      false
   }

   /**
    * Check if inventory contains item
    *
    * @param id id of item to check for
    * @return true if item in inventory
    */
   def contains(id: Int): Boolean = {
      contains(id, false)
   }

   /**
    * Check if inventory contains item
    *
    * @param name   name of item to check for
    * @param cached use cached inventory
    * @return true if item in inventory
    */
   def contains(name: String, cached: Boolean): Boolean = {
      for (item <- getItems(cached)) {
         if (name != null && item.getName.toLowerCase.contains(name.toLowerCase)) return true
      }
      false
   }

   /**
    * Check if inventory contains item
    *
    * @param name name of item to check for
    * @return true if item in inventory
    */
   def contains(name: String): Boolean = {
      contains(name, false)
   }

   /**
    * Get item in inventory
    *
    * @param name name of item to get
    * @return Item if found or null if no item was found
    */
   def getItem(name: String): Item = {
      for (i <- getItems) {
         if (i.getName.toLowerCase.contains(name.toLowerCase)) return i
      }
      null
   }

   def useItem(item: Item, npc: Npc) {
      item.interact("use")
      Mouse.click(npc.getModel.getNextPoint, true)
   }
}