package com.example

import play.api.libs.ws.WSResponse
import play.api.libs.ws.ning.NingWSClient
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

// https://www.playframework.com/documentation/2.4.x/ScalaWS
object BasicClientWithTimeout extends App {
  // client must be manually shutdown using client.close() when processing has completed
  val wsClient = NingWSClient()
  val responseFuture: Future[WSResponse] = wsClient
    .url("http://content-api.springer.com/document/10.1007%2Fs00414-006-0114-x")
    .get()

  import FutureTimeout._
  (responseFuture withTimeout) onComplete {
    case Success(response) => {
      println("Response was " + response.body)
      wsClient.close()
    }
    case Failure(exception) => {
      println("bang: " + exception)
      wsClient.close()
    }
  }
}

sealed class TimeoutException extends RuntimeException

object FutureTimeout {

  import scala.concurrent.ExecutionContext.Implicits.global

  implicit class FutureTimeoutLike[T](f: Future[T]) {
    def withTimeout(ms: Long): Future[T] = Future.firstCompletedOf(List(f, Future {
      Thread.sleep(ms)
      throw new TimeoutException
    }))

    lazy val withTimeout: Future[T] = withTimeout(2000) // default 2s timeout
  }

}