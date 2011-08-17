package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.util.Random

object Utils extends helpertraits.Utils
{
   override def random(min:Int, max:Int) = Random.nextInt(min, max)
}