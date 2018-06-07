package com.hivemindtechnologies.profilefinderui

import shapeless.{::, Generic, HList, HNil}

trait CsvEncoder[A] {
  def encode(a: A): List[String]
}

object CsvEncoder {

  def apply[A](implicit enc: CsvEncoder[A]): CsvEncoder[A] = enc

  def instance[A](f: A => List[String]): CsvEncoder[A] =
    (a: A) => f(a)

  implicit def stringCsvEncoder: CsvEncoder[String]   = instance(s => List(s))
  implicit def intCsvEncoder: CsvEncoder[Int]         = instance(i => List(i.toString))
  implicit def booleanCsvEncoder: CsvEncoder[Boolean] = instance(b => List(b.toString))
  implicit def optionCsvEncoder[A](implicit csvEncoder: CsvEncoder[A]): CsvEncoder[Option[A]] = instance {
    case Some(a) => csvEncoder.encode(a)
    case None    => List("")
  }

  implicit val hnilEncoder: CsvEncoder[HNil] = instance(_ => Nil)

  implicit def hlistEncoder[H, T <: HList](implicit hEncoder: CsvEncoder[H], tEncoder: CsvEncoder[T]): CsvEncoder[H :: T] =
    instance {
      case (h :: t) => hEncoder.encode(h) ++ tEncoder.encode(t)
    }

  implicit def genericEncoder[A, R](
      implicit
      gen: Generic[A] { type Repr = R },
      enc: CsvEncoder[R]
  ): CsvEncoder[A] =
    instance(a => enc.encode(gen.to(a)))

  implicit def pairEncoder[A, B](
      implicit
      aEncoder: CsvEncoder[A],
      bEncoder: CsvEncoder[B]
  ): CsvEncoder[(A, B)] =
    (pair: (A, B)) => {
      val (a, b) = pair
      aEncoder.encode(a) ++ bEncoder.encode(b)
    }

  def writeCsv[A](values: List[A])(implicit enc: CsvEncoder[A]): String =
    values.map(value => enc.encode(value).mkString(",")).mkString("\n")
}
