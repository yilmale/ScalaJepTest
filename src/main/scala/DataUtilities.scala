import org.apache.spark.SparkContext

object DataUtilities {
  def data_To_CSV(inputFilePath: String, outputFilePath: String, sc: SparkContext): Unit = {
    val readFile = sc.textFile(inputFilePath)

    val parseRows = readFile.map(row => row.split("\\s+").toList.drop(1))
    val outputCsvRDD = parseRows.map(row => row.mkString(","))
    outputCsvRDD.saveAsTextFile(outputFilePath)
  }

}
