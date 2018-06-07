package com.hivemindtechnologies.profilefinderui.modules

import cats.data.NonEmptyList
import cats.implicits._
import com.hivemindtechnologies.profilefinderui.native.FileSaver
import com.hivemindtechnologies.profilefinderui.http.Http
import com.hivemindtechnologies.profilefinderui.{Routes, http}
import io.circe.parser.decode
import org.scalajs.dom.document
import scalm._
import com.hivemindtechnologies.profilefinderui.CsvEncoder._

object Main extends App {

  def main(args: Array[String]): Unit = {
    val _ = Scalm.start(this, document.body)
  }

  // MODEL

  private val defaultLimit    = 1000
  private val defaultPageSize = 25

  val sources = NonEmptyList(
    Source(
      Routes.BaseUrls.aws,
      "Profile Finder (AWS)"
    ),
    List(
      Source(
        Routes.BaseUrls.bisnode,
        "Internal (Bisnode)"
      ))
  )

  case class Model(searchStatus: SearchStatus = NoSearch,
                   selectedProfile: Option[Int] = None,
                   searchData: SearchData = SearchData(None, Company),
                   pageSize: Int = defaultPageSize,
                   selectedSource: String = sources.head.url,
                   sources: NonEmptyList[Source] = sources)

  def init: (Model, Cmd[Msg]) = (Model(), Cmd.Empty)

  sealed trait Msg
  final case object Empty                                                            extends Msg
  final case class SearchInput(term: String)                                         extends Msg
  case object StartSearch                                                            extends Msg
  final case class SearchResult(res: Either[http.Error, Pagination[(Int, Profile)]]) extends Msg
  final case class ToggleDetails(id: Int)                                            extends Msg
  final case class ToggleSearch(by: SearchBy)                                        extends Msg
  final case class SelectSource(source: String)                                      extends Msg
  case object Next                                                                   extends Msg
  case object Previous                                                               extends Msg
  case object Export                                                                 extends Msg
  final case class ResultsPerPage(n: Int)                                            extends Msg

  // VIEW

  def view(model: Model): Html[Msg] = View.view(model)

  // UPDATE

  // scalastyle:off cyclomatic.complexity method.length
  def update(msg: Msg, model: Model): (Model, Cmd[Msg]) =
    msg match {
      case StartSearch =>
        (model.copy(selectedProfile = None, searchStatus = InProgress),
         searchRequest(source = model.selectedSource, searchData = model.searchData, pageNumber = 0, pageSize = model.pageSize, limit = defaultLimit))

      case SearchInput(term) =>
        (model.copy(searchData = model.searchData.withTerm(term = term)), Cmd.Empty)

      case SearchResult(Right(page)) =>
        (model.copy(searchStatus = Successful(model.searchData.term.getOrElse(""), page), selectedProfile = None), Cmd.Empty)

      case SearchResult(Left(ex)) =>
        (model.copy(searchStatus = Failed(ex)), Cmd.Empty)

      case Empty => (model, Cmd.Empty)

      case ToggleDetails(id) =>
        val selectedProfile =
          if (model.selectedProfile.contains(id)) {
            None
          } else {
            Some(id)
          }
        (model.copy(selectedProfile = selectedProfile), Cmd.Empty)

      case ToggleSearch(searchBy) =>
        (model.copy(searchData = model.searchData.copy(searchBy = searchBy)), Cmd.Empty) // todo: lenses

      case SelectSource(source) =>
        (model.copy(selectedSource = source), Cmd.Empty)

      case Next =>
        model.searchStatus match {
          case Successful(_, page) =>
            (model.copy(selectedProfile = None, searchStatus = InProgress),
             searchRequest(source = model.selectedSource,
                           searchData = model.searchData,
                           pageNumber = page.currentPage + 1,
                           pageSize = model.pageSize,
                           limit = defaultLimit))
          case _ => (model, Cmd.Empty)
        }

      case Previous =>
        model.searchStatus match {
          case Successful(_, page) =>
            (model.copy(selectedProfile = None, searchStatus = InProgress),
             searchRequest(source = model.selectedSource,
                           searchData = model.searchData,
                           pageNumber = page.currentPage - 1,
                           pageSize = model.pageSize,
                           limit = defaultLimit))
          case _ => (model, Cmd.Empty)
        }

      case Export =>
        model.searchStatus match {
          case Successful(_, page) =>
            val csv = writeCsv(page.items)
            (model, FileSaver.saveAs(_ => Empty, csv, "export.csv"))

          case _ => (model, Cmd.Empty)
        }

      case ResultsPerPage(n) =>
        if (model.pageSize != n) update(StartSearch, model.copy(pageSize = n))
        else (model, Cmd.Empty)
    }
  // scalastyle:on cyclomatic.complexity method.length

  // HTTP

  def decodeProfiles(json: String): Either[String, List[Profile]] = {
    decode[List[Profile]](json)
      .leftMap(_.getMessage)
  }

  def toMsg(pageNumber: Int, pageSize: Int): Either[http.Error, List[Profile]] => SearchResult =
    response =>
      SearchResult(
        response.map(
          profiles =>
            Pagination(currentPage = pageNumber,
                       pageSize = pageSize,
                       items = profiles.zipWithIndex.map(_.swap).slice(pageNumber * pageSize, pageNumber * pageSize + pageSize))))

  def stripUrlSuffix(url: String): String =
    url.replaceAll("^https?://|(www\\.)?", "")

  def searchRequest(source: String, searchData: SearchData, pageNumber: Int, pageSize: Int, limit: Int): Cmd[Msg] = {
    val normalizedPageNumber =
      if (pageNumber > 0) pageNumber
      else 0
    val url = searchData match {
      case SearchData(None, _) =>
        s"$source/profiles?limit=$limit"
      case SearchData(Some(term), by) =>
        by match {
          case Domain =>
            val st =
              stripUrlSuffix(term)
            s"$source${Routes.profilesSearch}?domain=$st&limit=$limit"
          case Company =>
            val st = term.replace("%", "%25")
            s"$source${Routes.profilesSearch}?company=$st&limit=$limit"
        }
    }

    Http.send(toMsg(normalizedPageNumber, pageSize), Http.get(url, decodeProfiles))
  }

  // SUBSCRIPTION

  def subscriptions(model: Model): Sub[Msg] = Sub.Empty
}
