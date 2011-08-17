package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.methods.Skills
import java.awt._
import nz.ubermouse.uberutils.methods.MySkills

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/4/11
 * Time: 2:19 PM
 * Package: nz.artedungeon.utils;
 */
class Skill(skillInt: Int)
{
  private val startime  : Long   = System.currentTimeMillis
  private val startxp   : Int    = Skills.getCurrentExp(skillInt)
  private val startLevel: Int    = MySkills.getRealLevel(skillInt)
  private val skillint  : Int    = 0
  private var lastxp    : Int    = startxp
  private var lastLevel : Int    = startLevel
  private val name      : String = Skills.SKILL_NAMES(skillint).substring(0, 1).toUpperCase +
                                   Skills.SKILL_NAMES(skillint).substring(1)

  def xpGained: Int = {
    Skills.getCurrentExp(skillint) - startxp
  }

  def levelsGained: Int = {
    curLevel - startLevel
  }

  def xpTL: Int = {
    MySkills.getExpToNextLevel(skillint)
  }

  def percentTL: Int = {
    MySkills.getPercentToNextLevel(skillint)
  }

  def xpPH: Int = {
    ((xpGained) * 3600000D / (System.currentTimeMillis - startime)).asInstanceOf[Int]
  }

  def curLevel: Int = {
    MySkills.getRealLevel(skillint)
  }

  def curXP: Int = {
    Skills.getCurrentExp(skillint)
  }

  def timeToLevel: String = {
    var TTL: String = "Calculating.."
    var ttlCalculations: Long = 0L
    if (xpPH != 0) {
      ttlCalculations = (xpTL * 3600000D).asInstanceOf[Long] / xpPH
      TTL = getTime(ttlCalculations)
    }
    TTL
  }

  def getName: String = {
    name
  }

  def drawSkill(g: Graphics2D, x: Int, y: Int) {
    g.drawString(name +
                 ": Gained " +
                 xpGained +
                 " P/H " +
                 xpPH +
                 " | TTL: " +
                 timeToLevel +
                 " | Level " +
                 curLevel +
                 " (" +
                 levelsGained +
                 ")", x, y)
  }

  private def getTime(millis: Long): String = {
    val time: Long = millis / 1000
    var seconds: String = Integer.toString((time % 60).asInstanceOf[Int])
    var minutes: String = Integer.toString(((time % 3600) / 60).asInstanceOf[Int])
    var hours: String = Integer.toString((time / 3600).asInstanceOf[Int])
    for (i <- 0 until 2) {
      {
        if (seconds.length < 2) {
          seconds = "0" + seconds
        }
        if (minutes.length < 2) {
          minutes = "0" + minutes
        }
        if (hours.length < 2) {
          hours = "0" + hours
        }
      }
    }
    hours + ":" + minutes + ":" + seconds
  }

  def getXpMinusLast: Int = {
    val returnXP: Int = curXP - lastxp
    lastxp = curXP
    returnXP
  }

  def getLevelMinusLast: Int = {
    val returnLevel: Int = curLevel - lastLevel
    lastLevel = curLevel
    returnLevel
  }

  def getSkill: Int = {
    skillint
  }
}

object Skill
{
  val skills: Array[Skill] = Array(new Skill(0),
                                   new Skill(1),
                                   new Skill(2),
                                   new Skill(3),
                                   new Skill(4),
                                   new Skill(5),
                                   new Skill(6),
                                   new Skill(7),
                                   new Skill(8),
                                   new Skill(9),
                                   new Skill(10),
                                   new Skill(11),
                                   new Skill(12),
                                   new Skill(13),
                                   new Skill(14),
                                   new Skill(15),
                                   new Skill(16),
                                   new Skill(17),
                                   new Skill(18),
                                   new Skill(19),
                                   new Skill(20),
                                   new Skill(21),
                                   new Skill(22),
                                   new Skill(23),
                                   new Skill(24))
}