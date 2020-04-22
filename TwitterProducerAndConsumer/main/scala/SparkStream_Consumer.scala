import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession}
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer}
import FilterData.filter
import InsertToDB.insertToMongo
import scala.collection.mutable


object SparkStream_Consumer {

//   Make a connection to a running mongoDB instance
  val mongoClient: MongoClient = MongoClient()
  //   Gets database
  val database: MongoDatabase = mongoClient.getDatabase("TwitterTestDB")
  //   Get a collection in the database
  val collection: MongoCollection[Document] = database.getCollection("New_Twitter_Tweets")
  if (mongoClient == null) print("Unable to connect to database")


  def main(args: Array[String]): Unit = {

//    Create a logger
    Logger.getLogger("org").setLevel(Level.ERROR)

//   Create a SparkSession
    val spark = SparkSession
      .builder
      .appName("TwitterTweetsStream")
      .master("local[*]")
      .getOrCreate()

//  Read stream form kafka
//  Subscribe to one topic
    val ds = spark.readStream.format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "Kafka_topic_tweets")
      .option("startingOffsets", "latest")  // "topicA":{"0":23,"1":-1},"topicB":{"0":-2}
      .load()

    val selectds = ds.selectExpr("CAST(value AS STRING)")




//    Write the output of the streaming Data to mongoDB
    val consumer = new ForeachWriter[Row] {
      /**
       *open the connection
       * @param partitionId
       * @param epochId
       * @return
       */
      def open(partitionId: Long, epochId: Long): Boolean = {
        true
      }

      /**
       * extract usefull data from json data
       * filter data based on country mapping
       * Write process data to mongo
       * @param record twitter record from kafka topic
       */
      def process(record: Row): Unit = {
        val extractdata = filter(record(0).toString)
        if (extractdata._1 != "-1") {
          println(extractdata._1, extractdata._2, extractdata._3)
          val Date = extractdata._1
          val location = extractdata._2
          val tweet = extractdata._3

          val doc: Document = Document("date" -> s"$Date", "location" -> s"$location", "tweet" -> s"$tweet")

          insertToMongo(doc, collection)
        }
      }

      /**
       * close the connection when no more tweets in kafka topic
       * the error thrown during processing data or null if there was no error
       * @param errorOrNull
       */
      def close(errorOrNull: Throwable): Unit = {
        println("Tweets Completed.")
      }
    }


    val writedf = selectds.writeStream
      .foreach(consumer)
      .start()
    writedf.awaitTermination()
  }
}


