package com.hivemindtechnologies.profilefinderui.modules

import cats.implicits._
import com.hivemindtechnologies.profilefinderui.http
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

sealed trait SearchStatus
case object NoSearch                                                            extends SearchStatus
case object InProgress                                                          extends SearchStatus
final case class Successful(term: String, profiles: Pagination[(Int, Profile)]) extends SearchStatus
final case class Failed(err: http.Error)                                        extends SearchStatus

sealed trait SearchBy
case object Domain  extends SearchBy
case object Company extends SearchBy

final case class SearchData(term: Option[String], searchBy: SearchBy) {
  def withTerm(term: String): SearchData =
    SearchData(term, this.searchBy)
}

object SearchData {
  def apply(term: String, searchBy: SearchBy): SearchData =
    SearchData(term.some.filter(_.trim.nonEmpty), searchBy)
}

final case class Source(url: String, name: String)

final case class Pagination[A](
    currentPage: Int,
    pageSize: Int,
    items: List[A]
)

final case class Profile(
    name: String,
    title: String,
    managementLevel: Option[String] = None,
    role: Option[String] = None,
    position: String,
    company: String,
    dunsNumber: Option[String] = None,
    legalForm: Option[String] = None,
    vatNumber: Option[String] = None,
    mainIndustry: Option[String] = None,
    street: Option[String] = None,
    postalCode: Option[String] = None,
    city: Option[String] = None,
    countryCode: Option[String] = None,
    phoneNumber: Option[String] = None,
    faxNumber: Option[String] = None,
    email: Option[String] = None,
    url: Option[String] = None,
    source: String
)

object Profile {
  implicit val profileDecoder: Decoder[Profile] = deriveDecoder
}
