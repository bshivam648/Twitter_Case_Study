import org.mongodb.scala.{Completed, Document, MongoCollection, Observable, Observer}

object InsertToDB {

  /**
   * insert the document to the mongoDB collection
   * @param doc document which needs to be inserted into the mongo collection
   * @param collection collection in which data is inserted
   */
  def insertToMongo(doc: Document, collection: MongoCollection[Document]): Unit = {

    val observable: Observable[Completed] = collection.insertOne(doc)
    //request observable to start streaming data
    observable.subscribe(new Observer[Completed] {
      override def onNext(result: Completed): Unit = println("Tweeet_Inserted")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
    collection.insertOne(doc)
  }
}
