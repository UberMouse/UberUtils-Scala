package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.methods.Game
import com.rsbuddy.script.methods.Mouse
import com.rsbuddy.script.methods.Settings
import com.rsbuddy.script.methods.Widgets
import com.rsbuddy.script.util.Timer
import org.rsbuddy.tabs.Attack
import nz.ubermouse.uberutils.methods.{MyEquipment, MyInventory}

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/19/11
 * Time: 8:17 PM
 * Package: nz.uberutils.helpers;
 */
object SpecialAttack
{
  /**
   * Should use special attack
   *
   * @return true if special attack should be used
   */
  def shouldSpec: Boolean = canSpec ||
                            (useSecondaryWeapon &&
                             (MyInventory.contains(primaryWeapon) || MyInventory.contains(offHand)))


  /**
   * Can use special attack
   *
   * @return true if special attack can be used
   */
  def canSpec: Boolean = {
    getSpecialEnergy >= specEnergy
  }

  def setSpecValues(weapon: String) {
    for (i <- 0 to weapons.length) {
      for (j <- 0 to weapons(i).length) {
        if (weapons(i)(j).equalsIgnoreCase(weapon)) {
          specAt = amountUsage(i)
          specEnergy = amountUsage(i)
        }
      }
    }
  }

  def doSpecial() {
    if (useSecondaryWeapon && MyInventory.contains(specialWeapon)) {
      Game.openTab(Game.TAB_INVENTORY)
      Mouse.move(MyInventory.getItem(specialWeapon).getComponent.getCenter)
      Mouse.click(true)
      Wait.While(MyInventory.contains(specialWeapon)) {}
    }
    else if (getSpecialEnergy >= specEnergy && !Attack.isSpecialEnabled) {
      val fail: Timer = new Timer(4000)
      while (getSpecialEnergy >= specEnergy && fail.isRunning && MyPlayer.inCombat) {
        Game.openTab(Game.TAB_ATTACK)
        if (!Attack.isSpecialEnabled) {
          Widgets.getComponent(884, 4).click
          Wait.For(Attack.isSpecialEnabled) {}
        }
        Utils.sleep(100)
      }
    }
    else if (useSecondaryWeapon && MyInventory.contains(primaryWeapon)) {
      Game.openTab(Game.TAB_INVENTORY)
      Mouse.move(MyInventory.getItem(primaryWeapon).getComponent.getCenter)
      Mouse.click(true)
      Wait.While(MyInventory.contains(primaryWeapon)) {}
    }
    else if (useSecondaryWeapon && MyInventory.contains(offHand)) {
      Game.openTab(Game.TAB_INVENTORY)
      Mouse.move(MyInventory.getItem(offHand).getComponent.getCenter)
      Mouse.click(true)
      Wait.While(MyInventory.contains(offHand)) {}
    }
  }

  /**
   * Get current special energy
   *
   * @return special energy (0-100)
   */
  def getSpecialEnergy: Int = {
    Settings.get(SETTING_SPECIAL_ENERGY) / 10
  }

  /**
   * Call to have the SpecialAttack class automatically setup up the special attack values and weapons
   */
  def setUpWeapons() {
    var prim: String = null
    specAt = 101
    specEnergy = 100
    Game.openTab(Game.TAB_EQUIPMENT)
    if (MyEquipment.getItem(MyEquipment.WEAPON).getId > -1) {
      prim = MyEquipment.getItem(MyEquipment.WEAPON).getName
      primaryWeapon = prim
      val name: String = if (prim.contains(">")) prim.split(">")(1) else prim
      setSpecValues(name)
    }
    Game.openTab(Game.TAB_INVENTORY)
    for (item <- MyInventory.getItems) {
      val name: String = if (item.getName.contains(">")) item.getName.split(">")(1) else prim
      for (i <- 0 to weapons.length) {
        for (j <- 0 to weapons(i).length) {
          if (weapons(i)(j).equalsIgnoreCase(name)) {
            specialWeapon = item.getName
            specAt = amountUsage(i)
            specEnergy = amountUsage(i)
            useSecondaryWeapon = true
          }
        }
      }
    }
  }

  var primaryWeapon     : String  = null
  var specialWeapon     : String  = null
  var offHand           : String  = null
  var specEnergy        : Int     = 0
  var specAt            : Int     = 101
  var useSecondaryWeapon: Boolean = false
  private val SETTING_SPECIAL_ENERGY: Int                  = 300
  private val amountUsage           : Array[Int]           = Array(10, 25, 33, 35, 45, 50, 55, 60, 80, 85, 100)
  private val weapons               : Array[Array[String]] = Array(Array("Rune thrownaxe", "Rod of ivandis"),
                                                                   Array("Dragon Dagger",
                                                                         "Dragon dagger (p)",
                                                                         "Dragon dagger (p+)",
                                                                         "Dragon dagger (p++)",
                                                                         "Dragon Mace",
                                                                         "Dragon Spear",
                                                                         "Dragon longsword",
                                                                         "Rune claws"),
                                                                   Array("Dragon Halberd"),
                                                                   Array("Magic Longbow"),
                                                                   Array("Magic Composite Bow"),
                                                                   Array("Dragon Claws",
                                                                         "Granite Maul",
                                                                         "Darklight",
                                                                         "Barrelchest Anchor",
                                                                         "Armadyl Godsword"),
                                                                   Array("Magic Shortbow"),
                                                                   Array("Dragon Scimitar",
                                                                         "Dragon 2H Sword",
                                                                         "Zamorak Godsword",
                                                                         "Korasi's sword"),
                                                                   Array("Dorgeshuun Crossbow",
                                                                         "Bone Dagger",
                                                                         "Bone Dagger (p+)",
                                                                         "Bone Dagger (p++)"),
                                                                   Array("Brine Sabre"),
                                                                   Array("Bandos Godsword",
                                                                         "Dragon Battleaxe",
                                                                         "Dragon Hatchet",
                                                                         "Seercull Bow",
                                                                         "Excalibur",
                                                                         "Enhanced excalibur",
                                                                         "Ancient Mace",
                                                                         "Saradomin Godsword"))
}