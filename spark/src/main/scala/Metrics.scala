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
		val ids = sc.textFile("method_ids")

		// Below are for the avg identifier length
		val id_lengths = ids.map(line => (line.split(':')(0), line.split(':')(1).length)).mapValues(x => (x,1))
		val id_totalcount = id_lengths.reduceByKey((x,y) => (x._1 + y._1 , x._2 + y._2))
		val id_avg = id_totalcount.map(a => (a._1, a._2._1 * 1.0 / a._2._2))
		//id_avg.saveAsTextFile("id_avg_len")
		
		// Below are for unique count
		val id_unique = ids.distinct()
		val id_uniqueCount = id_unique.map(line => (line.split(':')(0), 1)).reduceByKey(_ + _)
		//id_uniqueCount.saveAsTextFile("id_unique")

		val methods = sc.textFile("method_map")

		// Below are for the avg methodentifier length
		val method_lengths = methods.map(line => (line.split(':')(0), line.split(':')(2).length)).mapValues(x => (x,1))
		val method_totalcount = method_lengths.reduceByKey((x,y) => (x._1 + y._1 , x._2 + y._2))
		val method_avg = method_totalcount.map(a => (a._1, a._2._1 * 1.0 / a._2._2))
		//method_avg.saveAsTextFile("method_avg_len")
		
		// Below are for unique count
		val method_unique = methods.distinct()
		val method_uniqueCount = method_unique.map(line => (line.split(':')(0), 1)).reduceByKey(_ + _)

		//Tokenize identifiers
		val words = ids.map(line => (line.split(':')(0), line.split(':')(1)))
		val tokens = words.flatMapValues(word => word.split("_")).flatMapValues(word => word.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Z][a-z])|(?<=[a-zA-Z])(?=[0-9])|(?<=[0-9])(?=[a-z])"))
		val tokens_lengths = tokens.map(pair => (pair._1, pair._2.length)).mapValues(x => (x,1))
		val tokens_totalcount = tokens_lengths.reduceByKey((x,y) => (x._1 + y._1 , x._2 + y._2))
		val tokens_avg = tokens_totalcount.map(a => (a._1, a._2._1 * 1.0 / a._2._2))
		//tokens.saveAsTextFile("tokens")
		val tokens_uniqueCount = tokens.distinct().mapValues(x => 1).reduceByKey(_ + _)

		//GitPython inputs
		val gitpy = sc.textFile("bugs.txt")
		val fixes = gitpy.map(line => (line.split(':')(0), line.split(':')(1)))
		val joined = fixes.join(id_avg).join(id_uniqueCount).join(method_avg).join(method_uniqueCount).join(tokens_avg).join(tokens_uniqueCount)
		joined.saveAsTextFile("joined")
		//val file_bugs_idavg_iduni = joined.map(item => (item._1+','+item._2._1._1+','+item._2._1._2+','+item._2._2))
		//file_bugs_idavg_iduni.saveAsTextFile("all")
	}
}
