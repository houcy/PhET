package edu.colorado.phet.therampscala

case class ScalaRampObject(name: String, mass: Double, imageFilename: String)

object RampDefaults {
  val MIN_X = -10
  val MAX_X = 10
  val objects = ScalaRampObject("File Cabinet", 200.0, "cabinet.gif") ::
          ScalaRampObject("Sleepy Dog", 25.0,"ollie.gif") ::
          ScalaRampObject("Small Crate", 150, "crate.gif")::
          ScalaRampObject("Custom Crate", 150, "crate.gif")::
          ScalaRampObject("Refrigerator", 400, "fridge.gif")::
          ScalaRampObject("Textboox", 10, "phetbook.gif")::
          ScalaRampObject("Big Crate", 300, "crate.gif")::
          ScalaRampObject("Mystery Object", 300, "crate.gif")::
  Nil
  val objectsPerRow = 4

  
}