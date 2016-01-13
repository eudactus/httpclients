package com.example

import org.http4s.dsl._
import org.http4s.client._
import org.http4s.client.blaze.defaultClient

import scalaz.{-\/, \/-}

object BasicClient extends App {
  val req = GET(uri("http://content-api.springer.com/document/10.1007%2Fs00414-006-0114-x"))
  val responseBody = defaultClient.prepAs[String](req)

  responseBody.attemptRun match {
    case \/-(body) => println(body)
    case -\/(exception) => println("bang: " + exception)
  }
}
