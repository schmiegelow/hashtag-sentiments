package com.hivemindtechnologies.profilefinderui.native

import org.scalajs.dom.raw.Blob
import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.annotation.JSImport.Namespace

@JSImport("file-saver", Namespace)
@js.native
object JsFileSaver extends js.Object {
  def saveAs(blob: Blob, name: String, no_auto_bom: Boolean = false): Unit = js.native
}

import scalm.{Cmd, Task}

object FileSaver {
  def saveAs[Msg](toMsg: Either[String, Unit] => Msg, content: String, fileName: String): Cmd[Msg] = {
    Task
      .RunObservable[String, Unit] { observer =>
        try {
          val blob = new Blob(js.Array(content))
          observer.onNext(JsFileSaver.saveAs(blob, fileName))
        } catch {
          case ex: Throwable => observer.onError(ex.getMessage)
        }
        () =>
          ()
      }
      .attempt(toMsg)
  }
}
