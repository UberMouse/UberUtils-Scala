package nz.ubermouse.uberutils.methods

import com.rsbuddy.script.methods.Combat
import com.rsbuddy.script.methods.Widgets
import nz.ubermouse.uberutils.helpers.{Utils, Wait}

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/10/11
 * Time: 11:57 AM
 * Package: nz.uberutils.methods;
 */
object MyCombat
{
   def toggleQuickPrayers() {
      if (Widgets.getComponent(749, 5).click) {
         Wait.For(Combat.isQuickPrayerOn){}
         Utils.sleep(600, 700)
      }
   }
}