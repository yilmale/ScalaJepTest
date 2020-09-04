import jep._

object  ScalaJepTestMain extends App{
  print("Hello")
  val interp = new SharedInterpreter()
  interp.eval("a=5")
  var a = interp.getValue("a")
  println(a)

  interp.runScript("./networkAPITest.py")
  var f = interp.getValue("f")
  println(f)

}
