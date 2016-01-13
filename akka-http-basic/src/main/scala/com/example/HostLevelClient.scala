package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Success, Failure, Try}

object HostLevelClient extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  // The requestContext is used to identify which request a response is associated with given that
  // the pool client flow can return responses in any order. The type can be anything but we're using Int
  val requestContext: Int = 42

  val endpointHost = "content-api.springer.com"
  val uri = "/document/10.1007%2Fs00414-006-0114-x"

  // construct a pool client flow with context type `Int`
  val http = Http()
  val poolClientFlow = http.cachedHostConnectionPool[Int](endpointHost)
  val responseFuture: Future[(Try[HttpResponse], Int)] =
    Source.single(HttpRequest(uri = uri) -> requestContext)
      .via(poolClientFlow)
      .runWith(Sink.head)

  val bodyFuture: Future[ByteString] = for {
    responseTuple <- responseFuture
    responseTry = responseTuple._1
    if (responseTry.isSuccess)
    response = responseTry.get
    body <- response.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
  } yield body

  bodyFuture onComplete {
    case Success(body) => println("body was " + body.decodeString("UTF-8"))
    case Failure(exception) => println("bang: " + exception)
  }
}
