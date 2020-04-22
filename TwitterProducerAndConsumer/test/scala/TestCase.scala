
import org.junit._
import org.junit.Assert.assertEquals
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer}

import scala.io.Source
class TestCase {

  var lines = Seq[String]()

  /**
   *
   * Reading tweets retrieve from twitter from a file
   */
  @Before
    val mongoClient: MongoClient = MongoClient()
    val database: MongoDatabase = mongoClient.getDatabase("TwitterTestDB")
    val collection: MongoCollection[Document] = database.getCollection("Q2")
    if (mongoClient == null) print("Unable to connect to database")
    lines = Source.fromFile("test_tweets.txt").getLines.toSeq


  /**
   * Test whether the data format change according to our requirement or not in filter data
   * Test whether the tweet without date or location are dropped or not in filter data
   *
   */
  @Test
  def FilterData_Test_Date(): Unit = {
    val tweet1 = s""""Apr 16, 2020","When you die of corona virus and 1hour later they discover the cure in that hospital #Covid_19 ","California, USA""""
    val tweet2 = s""""Apr 10, 2020","@kylegriffin1 Hope it’s riddled w corona virus","Limerick, Ireland""""
    val tweet3 = s""""Mar 20, 2020","Perawat RS Siloam Surabaya Hastuti Yulistiorini meninggal karena Corona.Brumah skait tersebut selama 32 tahun","Indonesia""""
    val tweet4 = s""""Apr 10, 2020","@kylegriffin1 Hope it’s riddled w corona virus""""
    val tweet5 = s""""@kylegriffin1 Hope it’s riddled w corona virus","Limerick, Ireland""""
    assert("2020-04-16" == FilterData.filter(tweet1)._1)
    assert("2020-04-10" == FilterData.filter(tweet2)._1)
    assert("2020-03-20" == FilterData.filter(tweet3)._1)
    assert("2020-04-10" != FilterData.filter(tweet4)._1)
    assert("-1" == FilterData.filter(tweet5)._1)
  }

  /**
   * Test whether the cities in location is mapped to the correct corresponding country in filter data
   */
  @Test
  def FilterData_Test_Location(): Unit = {

    val tweet1 = s""""Apr 16, 2020","When you die of corona virus and 1hour later they discover the cure in that hospital #Covid_19 ","California, USA""""
    val tweet2 = s""""Apr 10, 2020","@kylegriffin1 Hope it’s riddled w corona virus","Limerick, India""""
    val tweet3 = s""""Mar 20, 2020","Perawat RS Siloam Surabaya Hastuti Yulistiorini meninggal karena Corona.Brumah skait tersebut selama 32 tahun","Bengaluru""""
    val tweet4 = s""""Apr 10, 2020","@kylegriffin1 Hope it’s riddled w corona virus","Indonesia""""
    val tweet5 = s""""Apr 10, 2020","@kylegriffin1 Hope it’s riddled w corona virus","Indian""""
    val tweet6 = s""""Apr 16, 2020","When you die of corona virus and 1hour later they discover the cure in that hospital🙂 #Covid_19 ","Madhya Pradesh""""
    assert("United States of America" == FilterData.filter(tweet1)._2)
    assert("India" == FilterData.filter(tweet2)._2)
    assert("India" == FilterData.filter(tweet3)._2)
    assert("Indonesia" == FilterData.filter(tweet4)._2)
    assert("India" != FilterData.filter(tweet5)._2)
    assert("India" == FilterData.filter(tweet6)._2)
  }

  /**
   * Testing a general tweet containing all type of information
   */
  @Test
  def test_Twitter_Tweet()={
    val jsonData = lines(0)
    val output = ExtractUsefulData.extractTweetFromJson(jsonData)
    val required: String = "\"Apr 06, 2017\",\"1/ Today we’re sharing our vision for the future of the Twitter API platform!\",\"Internet\""
    assertEquals(output,required)
  }

  /**
   *  Testing a tweet in which location field is absent
   */
  @Test
  def testTweetWithoutLocation()={
    val jsonData = lines(1)
    val output = ExtractUsefulData.extractTweetFromJson(jsonData)
    val required: String = "-1"
    assertEquals(output,required)
  }


  /**
   *  Testing a tweet whose length is more than 140 characters
   *  and full text is inside extended_tweet field
   */
  @Test
  def testTweetWithExtendedTweet()={
    val jsonData = lines(2)
    val output = ExtractUsefulData.extractTweetFromJson(jsonData)
    val required: String = "\"May 10, 2018\",\"Just another Extended Tweet with more than 140 characters, generated as a documentation example, showing that [\"truncated\": true] and the presence of an \"extended_tweet\" object with complete text and \"entities\" #documentation\",\"Internet\""
    assertEquals(output,required)
  }


  /**
   *  Testing a tweet which is a retweet,
   *  Original message is inside retweeted_status field
   */
  @Test
  def testTweetWithRetweetedStatus()={
    val jsonData = lines(3)
    val output = ExtractUsefulData.extractTweetFromJson(jsonData)
    val required: String = "\"May 10, 2018\",\"original message\",\"Internet\""
    assertEquals(output,required)
  }
}
