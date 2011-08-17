package nz.ubermouse.uberutils.helpers

import com.rsbuddy.script.task.Task

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 3:55 AM
 * Package: nz.arteminer.helpers; */
object Wait
{
  def For(callback: => Boolean, timeout: Int)(f: => Unit): Boolean = {
    var times = 0;
    while (times < timeout && !callback) {
      times += 1
      Task.sleep(100)
    }
    times != timeout
  }

  def For(callback: => Boolean)(f: => Unit): Boolean = {
    For(callback, 10) {
                        f
                      }
  }


  def While(callback: => Boolean, timeout: Int)(f: => Unit): Boolean = {
    var times = 0;
    while (times < timeout && callback) {
      times += 1
      Task.sleep(100)
    }
    times != timeout
  }

  def While(callback: => Boolean)(f: => Unit): Boolean = {
    While(callback, 10) {
                          f
                        };
  }
}