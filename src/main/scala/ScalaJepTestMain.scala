
import sparkInterface.SparkInit
import PythonUtilities._
import DataUtilities._


object  ScalaJepTestMain extends App {


  loadModule("./emaTest.py",interp)

  var result1 = executeModule("performExperiments")
  println(result1)

  println("---------------")

  var result3 = executeModule("simulate_ER1", Map(
    "n"->10,
    "p" -> 0.5,
    "replications" -> 10,
    "f" -> 0.6))

  println("largest connected component size = " + result3)


  var sp = SparkInit()

  val sc = sp.sparkContext

  data_To_CSV("./SHORTTHRUST.DAT","Thrust",sc)

 /*
  var combined = executeModule("tab_to_DF",List("./MGrain/part-00000","./MGrain/part-00001"))
  println(combined)
  println("--------------------")
  interp.eval("res = csv_to_DF(['./MGrain/part-00000','./MGrain/part-00000'])")
  var res = interp.getValue("res")
  println(res)
*/
  sp.close()

  //loadModule("./visualizationTest.py",interp)


}
