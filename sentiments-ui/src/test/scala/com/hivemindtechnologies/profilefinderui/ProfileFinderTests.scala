package com.hivemindtechnologies.profilefinderui

import com.hivemindtechnologies.profilefinderui.modules.Main
import org.scalatest._
import org.scalatest.prop.PropertyChecks

class ProfileFinderTests extends PropSpec with PropertyChecks with Matchers {

  property("strip url suffix") {
    val urls =
      Table(
        ("url", "url stripped"),
        ("foo", "foo"),
        ("http://xing.com", "xing.com"),
        ("https://xing.com", "xing.com"),
        ("http://www.xing.com", "xing.com"),
        ("https://www.xing.com", "xing.com"),
        ("www.xing.com", "xing.com")
      )

    forAll(urls) { (url, expected) =>
      Main.stripUrlSuffix(url) shouldEqual expected
    }
  }
}
