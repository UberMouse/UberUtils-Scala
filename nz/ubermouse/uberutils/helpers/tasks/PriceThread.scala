package nz.ubermouse.uberutils.helpers.tasks

import com.rsbuddy.script.task.LoopTask
import collection.mutable.{HashMap, ListBuffer}
import org.rsbuddy.net.GeItem
import nz.ubermouse.uberutils.helpers.Utils

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/27/11
 * Time: 3:48 PM
 * Package: nz.artezombies.utils;
 */
class PriceThread extends LoopTask
{

  import PriceThread._

  def loop: Int = {
    try {
      if (priceQueue.size > 0) {
        val id = priceQueue.remove(0)
        val item = GeItem.lookup(if (Utils.isNoted(id)) id - 1 else id)
        if (item == null) {
          prices.put(id, -2)
          return 50
        }
        prices.put(id, item.getGuidePrice)
      }
    } catch {
      case ignored: Exception => {
      }
    }
    50
  }
}

object PriceThread
{
  def priceForId(id: Int): Int = {
    if (prices.contains(id)) prices(id)
    else {
      if (!priceQueue.contains(id)) priceQueue += id
      -1
    }
  }

  def addPrice(id: Int, price: Int) = prices += id -> price


  private val prices    : HashMap[Int, Int] = new HashMap[Int, Int]
  private val priceQueue: ListBuffer[Int]   = new ListBuffer[Int]
}

