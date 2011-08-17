package nz.ubermouse.uberutils.helpers

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 11:51 AM
 * Package: nz.uberfalconry;
 */
trait Strategy
{
  def execute()

  def isValid: Boolean

  def getStatus: String
}