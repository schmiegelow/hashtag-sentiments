package com.hivemindtechnologies.profilefinderui

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.PropertyChecks
import CsvEncoder._

class CsvEncoderTests extends PropSpec with PropertyChecks with Matchers {
  case class Foo(i: Int, b: Boolean, s: String)

  property("encode simple case class") {
    forAll { (a: Int, b: Boolean, c: String) =>
      val list = List(Foo(a, b, c))

      val expected = s"$a,${if (b) "true" else "false"},$c"

      writeCsv(list) shouldEqual expected
    }
  }

  case class Bar(name: String, city: Option[String])

  property("encode with option") {
    val list = List(
      Bar("name1", None),
      Bar("name2", Some("city2"))
    )

    val expected =
      """name1,
        |name2,city2""".stripMargin

    writeCsv(list) shouldEqual expected
  }
}
