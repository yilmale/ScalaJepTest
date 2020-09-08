package sparkInterface

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.stat.{MultivariateStatisticalSummary, Statistics}
import org.apache.spark.mllib.linalg._
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.stat.Statistics
import org.apache.spark.mllib.stat.test.ChiSqTestResult
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.Row



object SparkInit {
  def apply() : SparkSession = {
    val rspath : String = "/Users/yilmaz/Desktop/spark-3.0.0-bin-hadoop2.7"

    Logger.getRootLogger.setLevel(Level.INFO)

    val conf = new SparkConf().setMaster("local[2]")
    conf.set("spark.app.name", "Spark Session")
    conf.set("spark.driver.cores", "2")

    SparkSession
      .builder()
      .appName("Spark Session")
      .config(conf)
      .getOrCreate()
  }

}



object SparkIntegration {
  def apply(): Unit = {
    val rspath : String = "/Users/yilmaz/Desktop/spark-3.0.0-bin-hadoop2.7"

    Logger.getRootLogger.setLevel(Level.INFO)

    val conf = new SparkConf().setMaster("local[2]")
    conf.set("spark.app.name", "SparkApplicationName")
    conf.set("spark.driver.cores", "2")

    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config(conf)
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._


    // val sc = new SparkContext(conf)

    val sc = spark.sparkContext
    val lines = sc.textFile("Test.txt")
    val words = lines.flatMap(line => line.split(' '))
    val wordsKVRdd = words.map(x => (x,1))
    val count = wordsKVRdd.reduceByKey((x,y) => x + y).map(x => (x._2,x._1)).sortByKey(false).map(x => (x._2, x._1)).take(10)
    count.foreach(println)

    spark.read

    val df = spark.read.json(rspath+"/examples/src/main/resources/people.json")

    // Displays the content of the DataFrame to stdout
    df.show()

    val df1 = spark.read.csv("/Users/yilmaz/IdeaProjects/PDP/E.csv")
    val df3= df1.toDF("Factor1","Factor2")
    df3.show()

    // a vector composed of the frequencies of events
    val vec: Vector = Vectors.dense(0.1, 0.15, 0.2, 0.3, 0.25)

    // compute the goodness of fit. If a second vector to test against is not supplied
    // as a parameter, the test runs against a uniform distribution.
    val goodnessOfFitTestResult = Statistics.chiSqTest(vec)
    // summary of the test including the p-value, degrees of freedom, test statistic, the method
    // used, and the null hypothesis.
    println(s"$goodnessOfFitTestResult\n")

    // a contingency matrix. Create a dense matrix ((1.0, 2.0), (3.0, 4.0), (5.0, 6.0))
    val mat: Matrix = Matrices.dense(3, 2, Array(1.0, 3.0, 5.0, 2.0, 4.0, 6.0))

    // conduct Pearson's independence test on the input contingency matrix
    val independenceTestResult = Statistics.chiSqTest(mat)
    // summary of the test including the p-value, degrees of freedom
    println(s"$independenceTestResult\n")

    val obs: RDD[LabeledPoint] =
      sc.parallelize(
        Seq(
          LabeledPoint(1.0, Vectors.dense(1.0, 0.0, 3.0)),
          LabeledPoint(1.0, Vectors.dense(1.0, 2.0, 0.0)),
          LabeledPoint(-1.0, Vectors.dense(-1.0, 0.0, -0.5)
          )
        )
      ) // (label, feature) pairs.

    // The contingency table is constructed from the raw (label, feature) pairs and used to conduct
    // the independence test. Returns an array containing the ChiSquaredTestResult for every feature
    // against the label.
    val featureTestResults: Array[ChiSqTestResult] = Statistics.chiSqTest(obs)
    featureTestResults.zipWithIndex.foreach { case (k, v) =>
      println(s"Column ${(v + 1)} :")
      println(k)
    }  // summary of the test

  }
}
