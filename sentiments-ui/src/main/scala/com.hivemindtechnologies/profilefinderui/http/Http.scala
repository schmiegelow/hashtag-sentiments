package com.hivemindtechnologies.profilefinderui
package http

import cats.implicits._
import org.scalajs.dom.raw.XMLHttpRequest
import scalm.{Cmd, Task}

import scala.util.Try

object Http {

  /**
    * Tries to transforms a response body of type String to a value of type A.
    * @tparam A type of the successfully decoded response
    */
  type Decoder[A] = String => Either[String, A]

  /**
    * Send an HTTP request.
    * @param resultToMessage transforms a successful or failed response into a Msg
    * @param request the request
    * @tparam A type of the successfully decoded response
    * @tparam Msg a scalm Msg
    * @return A Cmd that describes the HTTP request
    */
  def send[A, Msg](resultToMessage: Either[http.Error, A] => Msg, request: Request[A]): Cmd[Msg] =
    Task
      .RunObservable[http.Error, XMLHttpRequest] { observer =>
        val xhr = new XMLHttpRequest
        try {
          request.headers.foreach(h => xhr.setRequestHeader(h.name, h.value))
          xhr.timeout = request.timeout.map(_.toMillis.toDouble).getOrElse(0)
          xhr.withCredentials = request.withCredentials
          xhr.open(request.method.toString.toUpperCase, request.url)
          xhr.onload = _ => observer.onNext(xhr)
          xhr.onerror = _ => observer.onError(NetworkError)
          xhr.ontimeout = _ => observer.onError(Timeout)
          request.body match {
            case EmptyBody =>
              // scalastyle:off null
              xhr.send(null)
            // scalastyle:on null
            case StringBody(contentType, body) =>
              xhr.setRequestHeader("Content-Type", contentType)
              xhr.send(body)
          }
        } catch {
          case ex: Throwable => observer.onError(BadUrl(ex.getMessage))
        }
        () =>
          xhr.abort()
      }
      .attempt(_.flatMap { xhr =>
        val response = Response(
          url = request.url,
          status = Status(xhr.status, xhr.statusText),
          headers = parseHeaders(xhr.getAllResponseHeaders()),
          body = xhr.responseText
        )
        if (xhr.status < 200 || 300 <= xhr.status) {
          Left(BadStatus(response))
        } else {
          request
            .expect(response)
            .leftMap(BadPayload(_, response))
        }
      })
      .map(resultToMessage)

  /**
    * Create a GET request and interpret the response body as String.
    * @param url the url
    * @return a GET request
    */
  def getString(url: String): Request[String] =
    Request(
      method = Get,
      headers = Nil,
      url = url,
      body = EmptyBody,
      expect = r => Right(r.body),
      timeout = None,
      withCredentials = false
    )

  /**
    * Create a GET request and try to decode the response body from String to A.
    * @param url the url
    * @param decoder tries to transform the body into some value of type A
    * @tparam A the type of the successfully decoded response
    * @return a GET request
    */
  def get[A](url: String, decoder: Decoder[A]): Request[A] =
    Request(
      method = Get,
      headers = Nil,
      url = url,
      body = EmptyBody,
      expect = r => decoder(r.body),
      timeout = None,
      withCredentials = false
    )

  /**
    * Create a POST request and try to decode the response body from String to A.
    * @param url the url
    * @param body the body of the POST request
    * @param decoder tries to transform the body into some value of type A
    * @tparam A the type of the successfully decoded response
    * @return a POST request
    */
  def post[A](url: String, body: Body, decoder: Decoder[A]): Request[A] =
    Request(
      method = Post,
      headers = Nil,
      url = url,
      body = body,
      expect = r => decoder(r.body),
      timeout = None,
      withCredentials = false
    )

  /**
    * Create a JSON body. This will automatically add the `Content-Type: application/json` header.
    * @param body the JSON value as String
    * @return a request body
    */
  def jsonBody(body: String): Body = StringBody("application/json", body)

  private def parseHeaders(headers: String): Map[String, String] = {
    headers
      .split("[\\u000d\\u000a]+")
      .flatMap(h =>
        Try {
          val Array(fst, scd) = h.split(":").map(_.trim()).slice(0, 2)
          (fst, scd)
        }.toOption)(collection.breakOut)
  }
}
