#!/usr/bin/env amm

import ammonite.ops._
import ammonite.ops.ImplicitWd._

import $ivy.`org.reactivemongo::reactivemongo:0.16.1`
import $ivy.`org.slf4j:slf4j-simple:1.7.25`

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, ZoneOffset}

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.BSONDocument

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Properties

val MONGO_HOST = Properties.envOrElse("MONGO_HOST", "localhost")

val mongoUri = s"mongodb://${MONGO_HOST}:27017/test?rm.nbChannelsPerNode=10&heartbeatFrequencyMS=30000&rm.failover=default"

import ExecutionContext.Implicits.global // use any appropriate context

// Connect to the database: Must be done only once per application
val driver = MongoDriver()
val parsedUri = MongoConnection.parseURI(mongoUri)
val connection = parsedUri.map(driver.connection(_))

// Database and collections: Get references
val futureConnection = Future.fromTry(connection)
def db: Future[DefaultDB] = futureConnection.flatMap(_.database("test"))
def oupsCollection: Future[BSONCollection] = db.map(_.collection("oups-test"))

@main
def main(): Unit = {
  println("Starting to hammer mongo...")

  println(LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
  val startTime = System.currentTimeMillis()
  while (true) {
    val entry = System.currentTimeMillis().toString//.take(10)

    oupsCollection.flatMap(_.update(
      BSONDocument("entry" -> entry),
      BSONDocument(
        "$inc" -> BSONDocument("seen" -> 1),
      ),
      upsert = true
    ))

    Thread.sleep(5)

    // Record time every 30s
    if ((System.currentTimeMillis() - startTime) / 10 % 3000 == 0) {
      println(LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
    }
  }
}