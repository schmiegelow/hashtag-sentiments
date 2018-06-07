package com.hivemindtechnologies.profilefinderui.html

import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.raw.{Event, HTMLInputElement, HTMLSelectElement, KeyboardEvent}
import scalm.{Attr, Elem, Html}
import scalm.Html.{attr, onEvent, tag, ⊥}

object Extensions {
  def link[M](url: String)(attrs: Attr[M]*)(children: Elem[M]*): Html[M] =
    tag("a")(attr("href", url) :: attrs.toList: _*)(children: _*)
  // scalastyle:off method.name
  def `class`(name: String): Attr[⊥] = attr("class", name)
  def `type`(value: String): Attr[⊥] = attr("type", value)
  // scalastyle:on method.name

  def id(id: String): Attr[⊥] = attr("id", id)
  def onEnter[M](msg: M, default: M): Attr[M] =
    onEvent("keypress",
      (e: KeyboardEvent) =>
        if (e.keyCode == KeyCode.Enter) msg
        else default)

  def placeholder(text: String): Attr[⊥] = attr("placeholder", text)

  def onInput[M](msg: String => M): Attr[M] =
    onEvent("input", (e: dom.Event) => msg(e.target.asInstanceOf[HTMLInputElement].value))

  def onSelectChange[M](toMessage: String => M): Attr[M] =
    onEvent("change", (e: Event) => {
      val element = e.srcElement.asInstanceOf[HTMLSelectElement]
      toMessage(element.options(element.selectedIndex).value)
    })
}
