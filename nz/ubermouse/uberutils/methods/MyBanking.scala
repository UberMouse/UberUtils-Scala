package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Keyboard
import com.rsbuddy.script.methods.Mouse
import com.rsbuddy.script.task.Task
import com.rsbuddy.script.util.Random
import com.rsbuddy.script.util.Timer
import com.rsbuddy.script.wrappers.Component
import com.rsbuddy.script.wrappers.Item
import org.rsbuddy.tabs.Inventory
import org.rsbuddy.widgets.Bank
import java.awt._
import nz.ubermouse.uberutils.wrappers.BankItem

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 6:38 PM
 * Package: nz.uberutils.methods;
 */
object MyBanking
{
   def makeInventoryCount(i: Item, count: Int): Boolean = {
      if (!Bank.isOpen || i == null) return false
      if (count != 0) {
         val invCount: Int = Inventory.getCount(i.getId)
         if (invCount != count) {
            if (invCount < count) withdraw(i, count - invCount)
            else deposit(i, invCount - count)
         }
         else
            true
      }
      else {
         if (Bank.getCount(i.getId) > 0) withdraw(i.getId, 0)
         else Inventory.getCount(i.getId) > 0
      }
   }

   def makeInventoryCount(id: Int, count: Int): Boolean = {
      makeInventoryCount(Bank.getItem(id), count)
   }

   def makeInventoryCount(name: String, count: Int): Boolean = {
      makeInventoryCount(getItem(name), count)
   }

   /**
    * If bank is open, deposits specified amount of an item into the bank.
    *
    * @param id     The Name of the item.
    * @param number The amount to deposit. 0 deposits All. 1,5,10 deposit
    *               corresponding amount while other numbers deposit X.
    * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
    */
   def deposit(id: Int, number: Int): Boolean = {
      deposit(Inventory.getItem(id), number)
   }

   /**
    * If bank is open, deposits specified amount of an item into the bank.
    *
    * @param name   The Name of the item.
    * @param number The amount to deposit. 0 deposits All. 1,5,10 deposit
    *               corresponding amount while other numbers deposit X.
    * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
    */
   def deposit(name: String, number: Int): Boolean = {
      deposit(MyInventory.getItem(name), number)
   }

   /**
    * If bank is open, deposits specified amount of an item into the bank.
    *
    * @param iItem  The Item.
    * @param number The amount to deposit. 0 deposits All. 1,5,10 deposit
    *               corresponding amount while other numbers deposit X.
    * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
    */
   def deposit(iItem: Item, number: Int): Boolean = {
      if (Bank.isOpen) {
         if (number < 0)
            throw new IllegalArgumentException("number < 0 (" + number + ")")
         val invCount: Int = Inventory.getCount(true)
         if (iItem == null) return false
         var item: Component = iItem.getComponent
         val itemCount: Int = Inventory.getCount(true, iItem.getId)
         if (item == null)
            return true
         number match {
            case 0 =>
               if (item.interact(if (itemCount > 1) "Deposit-All" else "Deposit")) return true
               else return false
            case 1 =>
               if (item.interact("Deposit")) return true
               else return false
            case 5 =>
               if (item.interact("Deposit-" + number)) return true
               else return false
            case _ =>
               if (!item.interact("Deposit-" + number)) {
                  if (item.interact("Deposit-X")) {
                     Task.sleep(Random.nextInt(1000, 1300))
                     Keyboard.sendText(String.valueOf(number), true)
                  }
                  else return false
               }
         }
         var i: Int = 0
         while (i < 1500) {
            Task.sleep(20)
            var cInvCount: Int = Inventory.getCount(true)
            if (cInvCount < invCount || cInvCount == 0) return true
            i += 20
         }
      }
      false
   }

   /**
    * Tries to withdraw an item. 0 is All. 1,5,10 use Withdraw 1,5,10 while
    * other numbers Withdraw X.
    *
    * @param itemId The ID of the item.
    * @param count  The number to withdraw.
    * @return <tt>true</tt> on success.
    */
   def withdraw(itemId: Int, count: Int): Boolean = {
      withdraw(Bank.getItem(itemId), count)
   }

   /**
    * Tries to withdraw an item. 0 is All. 1,5,10 use Withdraw 1,5,10 while
    * other numbers Withdraw X.
    *
    * @param name  The name of the item.
    * @param count The number to withdraw.
    * @return <tt>true</tt> on success.
    */
   def withdraw(name: String, count: Int): Boolean = {
      withdraw(getItem(name), count)
   }

   /**
    * Tries to withdraw an item. 0 is All. 1,5,10 use Withdraw 1,5,10 while
    * other numbers Withdraw X.
    *
    * @param rsi   The Item to withdraw.
    * @param count The number to withdraw.
    * @return <tt>true</tt> on success.
    */
   def withdraw(rsi: Item, count: Int): Boolean = {
      if (Bank.isOpen) {
         if (count < 0) throw new IllegalArgumentException("count (" + count + ") < 0")
         if (rsi == null) return false
         var item: Component = rsi.getComponent
         if (item == null) return false
         if (item.getRelLocation == new Point(0, 0)) {
            Bank.getWidget.getComponent(Bank.COMPONENT_BANK_TABS(0)).click
            Task.sleep(1000, 1300)
         }
         val container: Component = Bank.getWidget.getComponent(93)
         if (!container.getViewportRect.contains(item.getBoundingRect)) {
            val p: Point = container.getAbsLocation
            val r: Rectangle = container.getViewportRect
            Mouse
            .move(Random.nextGaussian(p.x, p.y + r.width, r.width / 2),
                  Random.nextGaussian(p.y, p.y + r.height, r.height / 2))
            val limit: Timer = new Timer(5000)
            while (!container.getViewportRect.contains(item.getBoundingRect) && limit.isRunning) {
               Mouse.scroll(item.getAbsLocation.y < container.getAbsLocation.y)
               Task.sleep(20, 150)
            }
         }
         if (!container.getBoundingRect.contains(item.getBoundingRect)) return false
         val invCount: Int = Inventory.getCount(true)
         count match {
            case 0 =>
               item.interact("Withdraw-All")
            case 1 =>
               item.click(true)
            case 5 =>
            case 10 =>
               item.interact("Withdraw-" + count)
            case _ =>
               val exactAction: String = "Withdraw-" + count
               var hasAction: Boolean = false
               for (action <- rsi.getComponent.getActions) {
                  if (action != null && action.equalsIgnoreCase(exactAction)) {
                     hasAction = true
                  }
               }
               if (!hasAction || !item.interact("Withdraw-" + count)) {
                  if (item.interact("Withdraw-X")) {
                     Task.sleep(Random.nextInt(1000, 1300))
                     Keyboard.sendText(String.valueOf(count), true)
                  }
               }
         }
         var i: Int = 0
         while (i < 1500) {
            Task.sleep(20)
            val newInvCount: Int = Inventory.getCount(true)
            if (newInvCount > invCount || Inventory.isFull) return true
            i += 20
         }
      }
      false
   }

   /**
    * Gets the first item with the provided Name in the bank.
    *
    * @param name Name of the item to get.
    * @return The component of the item; otherwise null.
    */
   def getItem(name: String): Item = {
      if (Bank.isOpen) {
         val items: Array[Item] = Bank.getItems
         if (items != null) {
            for (item <- items) {
               if (item.getName.equalsIgnoreCase(name) ||
                   item.getName.toLowerCase.contains(name.toLowerCase)) return item
            }
         }
      }
      null
   }

   def depositAllExcept(bis: BankItem*) {
      if (!Bank.isOpen) return
      var stop: Boolean = false
      while (!stop && Bank.isOpen) {
         stop = true
         for (i <- MyInventory.getItems) {
            if (!bis.exists(bi => i.getName.contains(bi.getName)) && !bis.exists(bi => bi.getId == i.getId)) {
               deposit(i.getId, 0)
               stop = false
            }
         }
      }
   }

   def doBanking(bis: BankItem*): Boolean = {
      if (!Bank.isOpen) Bank.open
      MyBanking.depositAllExcept(bis: _*)
      for (bi <- bis) {
         if (!MyInventory.contains(bi.getId) && !MyInventory.contains(bi.getName)) {
            if (bi.getId != -1) MyBanking.makeInventoryCount(bi.getId, bi.getQuantity)
            else MyBanking.makeInventoryCount(bi.getName, bi.getQuantity)
         }
      }
      var close: Boolean = true
      for (bi <- bis) {
         if (bi.getId != -1) {
            if (!MyInventory.contains(bi.getId)) close = false
         }
         else if (!MyInventory.contains(bi.getName)) close = false
      }
      var count: Int = 0
      for (bi <- bis) count += bi.getQuantity
      if (MyInventory.getCount > count) close = false
      if (close) {
         Bank.close
         return true
      }
      false
   }
}