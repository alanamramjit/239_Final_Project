import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.log4j.{Logger, Level}

object Metrics {
	def main(args: Array[String]) {

		Logger.getLogger("org").setLevel(Level.OFF)
		val conf = new SparkConf()
		conf.setMaster("local[1]")
		conf.setAppName("Metrics Application")
		val sc = new SparkContext(conf)
		val lines = sc.textFile("method_ids")

		val words = lines.map(line => (line.split(':')(0) + ":" + line.split(':')(1), line.split(':')(2)))
		val tokens = words.flatMapValues(word => word.split("_")).flatMapValues(word => word.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])"))
		tokens.saveAsTextFile("tokens")

		// Below are for the avg identifier length / per method 
		//val lengths = lines.map(line => (line.split(':')(0) + ":" + line.split(':')(1), line.split(':')(2).length)).mapValues(x => (x,1))
		//val totalcount = lengths.reduceByKey((x,y) => (x._1 + y._1 , x._2 + y._2))
		//val avg = totalcount.map(a => (a._1, a._2._1 * 1.0 / a._2._2))
		//avg.saveAsTextFile("avg_updated")
		

		// Below are for unique count
		//val unique = lines.distinct()
		//val uniqueCount = unique.map(line => (line.split(':')(0) + ":" + line.split(':'), 1)).reduceByKey(_ + _)
		//uniqueCount.saveAsTextFile("uni_updated")


		//GitPython inputs
		//val gitpy = sc.textFile("bugs.txt")
		//val fixes = gitpy.map(line => (line.split(':')(0), line.split(':')(1)))
		//val joined = fixes.join(avg).join(uniqueCount)
		//joined.map(item => (item._1+','+item._2._1._1+','+item._2._1._2+','+item._2._2)).repartition(1).saveAsTextFile("all")
	}
}
