package com.hivemindtechnologies.profilefinderui
package modules

import cats.implicits._
import com.hivemindtechnologies.profilefinderui.html.Extensions._
import com.hivemindtechnologies.profilefinderui.http._
import com.hivemindtechnologies.profilefinderui.modules.Main._
import scalm.{Elem, Html}
import scalm.Html._

import scala.util.Try

object View {

  sealed trait Description
  final case class WithLabel(label: String, content: String) extends Description
  final case class Content(content: String)                  extends Description

  private def toTextOrLink(content: String): Html[⊥] = {
    val urlPattern =
      """^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$""".r

    if (urlPattern.pattern.matcher(content).matches) {
      link(content)(`class`("form-control-plaintext"))(text(content))
    } else {
      div(`class`("form-control-plaintext"))(text(content))
    }
  }

  private def description(description: Description): Html[⊥] =
    description match {
      case WithLabel(label: String, content: String) =>
        div(`class`("form-group row"))(
          div(`class`("col-sm-6 col-form-label"))(
            text(label)
          ),
          div(`class`("col-sm-6"))(
            toTextOrLink(content)
          )
        )

      case Content(content: String) =>
        div(`class`("form-group row"))(
          div(`class`("col-sm-12 form-control-plaintext"))(
            text(content)
          )
        )
    }

  private def card: ((String, List[Html[⊥]])) => Html[⊥] = {
    case (caption, List()) =>
      div(`class`("col-sm-4"))(
        div(`class`("card"))(
          div(`class`("card-body"))(
            tag("h5")(`class`("card-title"))(text(caption)),
            tag("i")(`class`("card-text"))(
              text("No data available.")
            )
          )
        )
      )
    case (caption, elements) =>
      div(`class`("col-sm-4"))(
        div(`class`("card"))(
          div(`class`("card-body"))(
            tag("h5")(`class`("card-title"))(text(caption)),
            tag("p")(`class`("card-text"))(
              elements: _*
            )
          )
        )
      )
  }

  private def profileView(p: Profile): Html[⊥] = {
    div(`class`("container"))(
      div(`class`("row"))(
        Map(
          "Address" -> List(
            Option(p.company).map(Content),
            p.street.filter(_.nonEmpty).map(Content),
            (p.postalCode, p.city)
              .mapN((code, city) => s"$code $city")
              .filter(_.trim.nonEmpty)
              .map(Content),
            p.countryCode.filter(_.nonEmpty).map(Content)
          ).flatten.map(description),
          "Contact" -> List(
            p.phoneNumber.filter(_.nonEmpty).map(WithLabel("Phone", _)),
            p.faxNumber.filter(_.nonEmpty).map(WithLabel("Fax", _)),
            p.email.filter(_.nonEmpty).map(WithLabel("Email", _)),
            p.url.filter(_.nonEmpty).map(WithLabel("Web", _))
          ).flatten.map(description),
          "Company" -> List(
            p.dunsNumber.filter(_.nonEmpty).map(WithLabel("DUNS number", _)),
            p.legalForm.filter(_.nonEmpty).map(WithLabel("Legal form", _)),
            p.vatNumber.filter(_.nonEmpty).map(WithLabel("VAT number", _)),
            p.mainIndustry.filter(_.nonEmpty).map(WithLabel("Main industry", _))
          ).flatten.map(description)
        ).map(card).toList: _*
      ),
      div(`class`("row"))(
        div(`class`("col center"))(
          tag("small")()(toTextOrLink(p.source))
        ))
    )
  }

  private def profilesView(page: Pagination[(Int, Profile)], selected: Option[Int]): Html[Msg] = {

    val ps = page.items

    def row(id: Int, p: Profile, icon: String) =
      tag("tr")()(
        tag("td")()(text(s"${id + 1}")),
        tag("td")()(text(p.name)),
        tag("td")()(text(p.company)),
        tag("td")()(text(p.position)),
        tag("td")()(text(p.source)),
        tag("td")(onClick(ToggleDetails(id)), `class`("expand-collapse"))(tag("i")(`class`(icon))())
      )

    def rowWithDetails(id: Int, p: Profile) =
      if (selected.contains(id)) {
        List(
          row(id, p, "fas fa-chevron-up"),
          tag("tr")(`class`("profile-details"))(
            tag("td")(attr("colspan", "5"))(profileView(p))
          )
        )
      } else {
        List(row(id, p, "fas fa-chevron-down"))
      }

    div()(
      tag("table")(`class`("table"))(
        tag("thead")()(
          tag("tr")()(
            tag("th")()(text("#")),
            tag("th")()(text("Name")),
            tag("th")()(text("Company")),
            tag("th")()(text("Position")),
            tag("th")()(text("Source")),
            tag("th")()()
          )),
        tag("tbody")()(ps.flatMap(p => rowWithDetails(p._1, p._2)): _*)
      )
    )
  }

  private def toolbar(page: Pagination[(Int, Profile)]): Html[Msg] =
    div(`class`("row toolbar"))(
      div(`class`("col"))(
        div()(
          if (page.items.head._1 > 0) tag("i")(`class`("fas fa-angle-double-left paging pointer"), onClick(Previous))()
          else tag("i")(`class`("fas fa-angle-double-left paging"))(),
          div(`class`("page-numbers paging"))(text(s"${page.items.head._1 + 1} - ${page.items.last._1 + 1}")),
          if (page.items.last._1 == page.pageSize * (page.currentPage + 1) - 1) {
            tag("i")(`class`("fas fa-angle-double-right paging pointer"), onClick(Next))()
          } else tag("i")(`class`("fas fa-angle-double-right paging"))()
        )
      ),
      div(`class`("col-8 block"))(
        label(`class`("grey-text label"))(text("Items per page:")),
        selectResultsPerPage(page.pageSize)
      ),
      div(`class`("col"))(
        button(`class`("btn btn-light float-right"), `type`("button"), onClick(Export))(text("Export view"))
      )
    )

  private def searchToggle(model: Model): Html[Msg] = div()(
    div(`class`("custom-control custom-radio custom-control-inline search-toggle"))(
      radio("search-by",
            model.searchData.searchBy == Domain,
            `type`("radio"),
            `class`("custom-control-input"),
            id("domainRadio"),
            onClick(ToggleSearch(Domain))),
      label(`class`("custom-control-label"), attr("for", "domainRadio"), style("line-height: 25px"))(text("Search by domain"))
    ),
    div(`class`("custom-control custom-radio custom-control-inline"))(
      radio("search-by",
            model.searchData.searchBy == Company,
            `type`("radio"),
            `class`("custom-control-input"),
            id("companyRadio"),
            onClick(ToggleSearch(Company))),
      label(`class`("custom-control-label"), attr("for", "companyRadio"), style("line-height: 25px"))(text("Search by company name"))
    )
  )

  private def sourcePicker(model: Model): Html[Msg] =
    div(`class`("source-picker"))(
      tag("form")()(
        div(`class`("form-group row"))(
          label(`class`("col-sm-2 col-form-label grey-text"))(text("Source:")),
          div(`class`("col-sm-10"))(
            tag("select")(`class`("custom-select form-control"), onSelectChange(SelectSource))(
              model.sources.map { source =>
                tag("option")(attr("value", source.url))(text(source.name))
              }.toList: _*
            )
          )
        )
      )
    )

  private def selectResultsPerPage(pageSize: Int): Html[Msg] =
    tag("select")(`class`("custom-select page-size"), onSelectChange(str => Try(str.toInt).map(ResultsPerPage).getOrElse(Empty)))(
      tag("option")(attr("value", "25"), cond(pageSize == 25)(attr("selected", "selected")))(text("25")),
      tag("option")(attr("value", "50"), cond(pageSize == 50)(attr("selected", "selected")))(text("50")),
      tag("option")(attr("value", "100"), cond(pageSize == 100)(attr("selected", "selected")))(text("100")),
      tag("option")(attr("value", "1000"), cond(pageSize == 1000)(attr("selected", "selected")))(text("all"))
    )

  private def navBar(model: Model): Html[Msg] =
    tag("nav")(`class`("navbar navbar-expand-lg navbar-light bg-light"))(
      tag("span")(`class`("navbar-brand mb-0 h1"))(
        tag("img")(attr("src", "hedgehog.png"), attr("width", "45"), attr("height", "45"))(),
        text("  Profile Finder")
      ),
      sourcePicker(model)
    )

  private def errorMsg(err: http.Error): Elem[Nothing] =
    err match {
      case NetworkError => text("There is a problem with the network.")
      case BadPayload(_, _) => text("The body of the response could not be parsed correctly.")
      case BadUrl(_) => text("The provided URL is not valid.")
      case Timeout => text("It took too long to get a response.")
      case BadStatus(r) => text(s"The status code ${r.status.code} of the response indicates a failure.")
    }

  private def result(model: Model): Html[Msg] =
    model.searchStatus match {
      case Successful(term, page) if page.items.isEmpty =>
        div(`class`("search-result grey-text"))(text(s"""We couldn't find anything for "$term""""))
      case Successful(_, page) =>
        div(`class`("search-result"))(
          toolbar(page),
          profilesView(page, model.selectedProfile)
        )
      case Failed(err) =>
        div(`class`("search-result grey-text"))(errorMsg(err))
      case InProgress =>
        div(`class`("search-result"))(tag("i")(`class`("fa fa-spinner fa-spin"))())
      case NoSearch =>
        div()()
    }

  private def search(model: Model): Html[Msg] =
    div()(
      div(`class`("input-group mb-3 profile-id-search-box"))(
        input(
          onEnter(StartSearch, Empty),
          onInput(SearchInput),
          `type`("text"),
          `class`("form-control"),
          placeholder(s"enter ${model.searchData.searchBy match {
            case Domain  => "domain"
            case Company => "company"
          }} name ..."),
          attr("autofocus", "autofocus")
        ),
        div(`class`("input-group-append"))(
          button(`class`("btn btn-outline-secondary"), `type`("button"), onClick(StartSearch))(text("Search"))
        )
      ),
      searchToggle(model),
      result(model)
    )

  def view(model: Model): Html[Msg] =
    div()(
      navBar(model),
      div(`class`("profile-id-client-template"))(
        div(`class`("container"))(
          div(`class`("row"))(
            div(`class`("col"))(
              h1()(text("Search for profiles.")),
              tag("p")(`class`("lead"))(text("This search let's you find people. Just type in a search term, and start a search.")),
              search(model)
            )
          )
        )
      )
    )
}
