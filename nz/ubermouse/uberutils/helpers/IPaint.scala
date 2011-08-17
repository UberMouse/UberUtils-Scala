package nz.ubermouse.uberutils.helpers

import java.awt._
import java.awt.event.KeyListener
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import collection.mutable.ListBuffer

trait IPaint extends MouseListener with MouseMotionListener with KeyListener
{
   val skills = new ListBuffer[Skill]
   def paint(g: Graphics): Boolean
}