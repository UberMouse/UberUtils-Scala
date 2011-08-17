package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Game
import com.rsbuddy.script.methods.Skills
import com.rsbuddy.script.methods.Widgets
import com.rsbuddy.script.task.Task
import nz.ubermouse.uberutils.helpers.Utils

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/11/11
 * Time: 1:04 PM
 */
object MySkills
{
   /**
    * Gets the index of the skill with a given name. This is not case
    * sensitive.
    *
    * @param statName The skill's name.
    * @return The index of the specified skill; otherwise -1.
    */
   def getIndex(statName: String): Int = Skills.SKILL_NAMES.indexWhere(s => s.equalsIgnoreCase(statName))


   /**
    * Gets the level at the given experience.
    *
    * @param exp The experience.
    * @return The level based on the experience given.
    * @see #XP_TABLE
    */
   def getLevelAt(exp: Int): Int = Skills.XP_TABLE.indexWhere(s => exp > s)

   /**
    * Gets the experience at the given level.
    *
    * @param lvl The level.
    * @return The level based on the experience given.
    */
   def getExpAt(lvl: Int): Int = {
      if (lvl > 120) {
         return 1
      }
      Skills.XP_TABLE(lvl - 1)
   }

   /**
    * Gets the experience required for the given level.
    *
    * @param lvl The level.
    * @return The level based on the experience given.
    */
   def getExpRequired(lvl: Int): Int = {
      if (lvl > 120) {
         return 1
      }
      Skills.XP_TABLE(lvl)
   }

   /**
    * Gets the skill name of an index.
    *
    * @param index The index.
    * @return The name of the skill for that index.
    */
   def getSkillName(index: Int): String = {
      if (index > Skills.SKILL_NAMES.length - 1) {
         return null
      }
      Skills.SKILL_NAMES(index)
   }

   /**
    * Gets the player's current level in a skill based on their experience in
    * that skill.
    *
    * @param index The index of the skill.
    * @return The real level of the skill.
    * @see #getRealLevel(int)
    */
   def getRealLevel(index: Int): Int = {
      if (index > Skills.SKILL_NAMES.length - 1) {
         return -1
      }
      Skills.getLevelAt(Skills.getCurrentExp(index))
   }

   /**
    * Gets the percentage to the next level in a given skill.
    *
    * @param index The index of the skill.
    * @return The percent to the next level of the provided skill or 0 if level
    *         of skill is 99.
    */
   def getPercentToNextLevel(index: Int): Int = {
      if (index > Skills.SKILL_NAMES.length - 1) {
         return -1
      }
      val lvl: Int = getRealLevel(index)
      getPercentToLevel(index, lvl + 1)
   }

   /**
    * Gets the percentage to the a level in a given skill.
    *
    * @param index  The index of the skill.
    * @param endLvl The level for the percent.
    * @return The percent to the level provided of the provided skill or 0 if level
    *         of skill is 99.
    */
   def getPercentToLevel(index: Int, endLvl: Int): Int = {
      if (index > Skills.SKILL_NAMES.length - 1) {
         return -1
      }
      val lvl: Int = getRealLevel(index)
      if (lvl == 120 || endLvl > 120) return 0
      val xpTotal: Int = Skills.XP_TABLE(endLvl) - Skills.XP_TABLE(lvl)
      if (xpTotal == 0) {
         return 0
      }
      val xpDone: Int = Skills.getCurrentExp(index) - Skills.XP_TABLE(lvl)
      100 * xpDone / xpTotal
   }

   /**
    * Gets the experience remaining until reaching the next level in a given
    * skill.
    *
    * @param index The index of the skill.
    * @return The experience to the next level of the skill.
    */
   def getExpToNextLevel(index: Int): Int = {
      if (index > Skills.SKILL_NAMES.length - 1) {
         return -1
      }
      val lvl: Int = getRealLevel(index)
      getExpToLevel(index, lvl + 1)
   }

   /**
    * Gets the experience remaining until reaching the a level in a given
    * skill.
    *
    * @param index  The index of the skill.
    * @param endLvl The level for the experience remaining.
    * @return The experience to the level provided of the skill.
    */
   def getExpToLevel(index: Int, endLvl: Int): Int = {
      if (index > Skills.SKILL_NAMES.length - 1) {
         return -1
      }
      val lvl: Int = getRealLevel(index)
      if (lvl == 120 || endLvl > 120) return 0
      Skills.XP_TABLE(endLvl) - Skills.getCurrentExp(index)
   }

   /**
    * Gets the total/overall level.
    *
    * @return The total/overall level.
    */
   def getTotalLevel: Int = {
      var total: Int = 0
      for (i <- 0 to Skills.SKILL_NAMES.length - 1)
         total += getRealLevel(i)
      total
   }

   /**
    * Moves the mouse over a given component in the stats tab.
    *
    * @param component The component index.
    * @return <tt>true</tt> if the mouse was moved over the given component
    *         index.
    */
   def doHover(component: Int): Boolean = {
      Game.openTab(Game.TAB_STATS)
      Task.sleep(Utils.random(10, 100))
      Widgets.getComponent(INTERFACE_TAB_STATS, component).hover
   }

   val SKILL_NAMES: Array[String] = Array("attack",
                                          "defence",
                                          "strength",
                                          "constitution",
                                          "range",
                                          "prayer",
                                          "magic",
                                          "cooking",
                                          "woodcutting",
                                          "fletching",
                                          "fishing",
                                          "firemaking",
                                          "crafting",
                                          "smithing",
                                          "mining",
                                          "herblore",
                                          "agility",
                                          "thieving",
                                          "slayer",
                                          "farming",
                                          "runecrafting",
                                          "hunter",
                                          "construction",
                                          "summoning",
                                          "dungeoneering",
                                          "-unused-")
   /**
    * A table containing the experiences that begin each level.
    */
   val XP_TABLE               : Array[Int] = Array(0,
                                                   83,
                                                   174,
                                                   276,
                                                   388,
                                                   512,
                                                   650,
                                                   801,
                                                   969,
                                                   1154,
                                                   1358,
                                                   1584,
                                                   1833,
                                                   2107,
                                                   2411,
                                                   2746,
                                                   3115,
                                                   3523,
                                                   3973,
                                                   4470,
                                                   5018,
                                                   5624,
                                                   6291,
                                                   7028,
                                                   7842,
                                                   8740,
                                                   9730,
                                                   10824,
                                                   12031,
                                                   13363,
                                                   14833,
                                                   16456,
                                                   18247,
                                                   20224,
                                                   22406,
                                                   24815,
                                                   27473,
                                                   30408,
                                                   33648,
                                                   37224,
                                                   41171,
                                                   45529,
                                                   50339,
                                                   55649,
                                                   61512,
                                                   67983,
                                                   75127,
                                                   83014,
                                                   91721,
                                                   101333,
                                                   111945,
                                                   123660,
                                                   136594,
                                                   150872,
                                                   166636,
                                                   184040,
                                                   203254,
                                                   224466,
                                                   247886,
                                                   273742,
                                                   302288,
                                                   333804,
                                                   368599,
                                                   407015,
                                                   449428,
                                                   496254,
                                                   547953,
                                                   605032,
                                                   668051,
                                                   737627,
                                                   814445,
                                                   899257,
                                                   992895,
                                                   1096278,
                                                   1210421,
                                                   1336443,
                                                   1475581,
                                                   1629200,
                                                   1798808,
                                                   1986068,
                                                   2192818,
                                                   2421087,
                                                   2673114,
                                                   2951373,
                                                   3258594,
                                                   3597792,
                                                   3972294,
                                                   4385776,
                                                   4842295,
                                                   5346332,
                                                   5902831,
                                                   6517253,
                                                   7195629,
                                                   7944614,
                                                   8771558,
                                                   9684577,
                                                   10692629,
                                                   11805606,
                                                   13034431,
                                                   14391160,
                                                   15889109,
                                                   17542976,
                                                   19368992,
                                                   21385073,
                                                   23611006,
                                                   26068632,
                                                   28782069,
                                                   31777943,
                                                   35085654,
                                                   38737661,
                                                   42769801,
                                                   47221641,
                                                   52136869,
                                                   57563718,
                                                   63555443,
                                                   70170840,
                                                   77474828,
                                                   85539082,
                                                   94442737,
                                                   104273167)
   val ATTACK                 : Int        = 0
   val DEFENSE                : Int        = 1
   val STRENGTH               : Int        = 2
   val CONSTITUTION           : Int        = 3
   val RANGE                  : Int        = 4
   val PRAYER                 : Int        = 5
   val MAGIC                  : Int        = 6
   val COOKING                : Int        = 7
   val WOODCUTTING            : Int        = 8
   val FLETCHING              : Int        = 9
   val FISHING                : Int        = 10
   val FIREMAKING             : Int        = 11
   val CRAFTING               : Int        = 12
   val SMITHING               : Int        = 13
   val MINING                 : Int        = 14
   val HERBLORE               : Int        = 15
   val AGILITY                : Int        = 16
   val THIEVING               : Int        = 17
   val SLAYER                 : Int        = 18
   val FARMING                : Int        = 19
   val RUNECRAFTING           : Int        = 20
   val HUNTER                 : Int        = 21
   val CONSTRUCTION           : Int        = 22
   val SUMMONING              : Int        = 23
   val DUNGEONEERING          : Int        = 24
   val INTERFACE_TAB_STATS    : Int        = 320
   val INTERFACE_ATTACK       : Int        = 1
   val INTERFACE_DEFENSE      : Int        = 22
   val INTERFACE_STRENGTH     : Int        = 4
   val INTERFACE_CONSTITUTION : Int        = 2
   val INTERFACE_RANGE        : Int        = 46
   val INTERFACE_PRAYER       : Int        = 70
   val INTERFACE_MAGIC        : Int        = 87
   val INTERFACE_COOKING      : Int        = 62
   val INTERFACE_WOODCUTTING  : Int        = 102
   val INTERFACE_FLETCHING    : Int        = 95
   val INTERFACE_FISHING      : Int        = 38
   val INTERFACE_FIREMAKING   : Int        = 85
   val INTERFACE_CRAFTING     : Int        = 78
   val INTERFACE_SMITHING     : Int        = 20
   val INTERFACE_MINING       : Int        = 3
   val INTERFACE_HERBLORE     : Int        = 30
   val INTERFACE_AGILITY      : Int        = 12
   val INTERFACE_THIEVING     : Int        = 54
   val INTERFACE_SLAYER       : Int        = 112
   val INTERFACE_FARMING      : Int        = 120
   val INTERFACE_RUNECRAFTING : Int        = 104
   val INTERFACE_HUNTER       : Int        = 136
   val INTERFACE_CONSTRUCTION : Int        = 128
   val INTERFACE_SUMMONING    : Int        = 144
   val INTERFACE_DUNGEONEERING: Int        = 152
}