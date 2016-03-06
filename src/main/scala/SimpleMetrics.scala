import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object SimpleMetrics {
	def main(args: Array[String]) {
		val conf = new SparkConf().setAppName("Simple Application")
		val sc = new SparkContext(conf)
		val lines = sc.textFile("formatted")
		// Below are for the avg identifier length
		val lengths = lines.map(line => (line.split(':')(0), line.split(':')(1).length)).mapValues(x => (x,1))
		val totalcount = lengths.reduceByKey((x,y) => (x._1 + y._1 , x._2 + y._2))
		val avg = totalcount.map(a => (a._1, a._2._1 * 1.0 / a._2._2))
		avg.saveAsTextFile("avg-length")
		// Below are for unique count
		val unique = lines.distinct()
		val uniqueCount = unique.map(line => (line.split(':')(0), 1)).reduceByKey(_ + _)
		uniqueCount.saveAsTextFile("unique-count")
		// GitPython inputs
		val gitpy = sc.textFile("gitpy")
		val fixes = gitpy.map(line => line.split(':'))
		val avg-rel = avg.join(fixes)
		val uni-rel = unique.join(fixes)

	}
}
