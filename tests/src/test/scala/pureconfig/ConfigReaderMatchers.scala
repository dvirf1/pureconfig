package pureconfig

import scala.reflect.ClassTag

import org.scalatest._
import org.scalatest.matchers.{ MatchResult, Matcher }
import pureconfig.error._

trait ConfigReaderMatchers { this: FlatSpec with Matchers =>

  def failWith(reason: FailureReason): Matcher[ReaderResult[Any]] =
    matchPattern { case Left(ConfigReaderFailures(ConvertFailure(`reason`, _, _), Nil)) => }

  def failWith(
    reason: FailureReason,
    path: String,
    location: Option[ConfigValueLocation] = None): Matcher[ReaderResult[Any]] =
    be(Left(ConfigReaderFailures(ConvertFailure(reason, location, path), Nil)))

  def failWith(failure: ConfigReaderFailure): Matcher[ReaderResult[Any]] =
    be(Left(ConfigReaderFailures(failure, Nil)))

  def failWithType[Reason <: FailureReason: ClassTag]: Matcher[ReaderResult[Any]] =
    matchPattern { case Left(ConfigReaderFailures(ConvertFailure(_: Reason, _, _), Nil)) => }

  def failWithType[Failure <: ConfigReaderFailure: ClassTag](implicit dummy: DummyImplicit): Matcher[ReaderResult[Any]] =
    matchPattern { case Left(ConfigReaderFailures(_: Failure, Nil)) => }

  def failLike(pf: PartialFunction[ConfigReaderFailure, MatchResult]) =
    new Matcher[ReaderResult[Any]] with Inside with PartialFunctionValues {

      def apply(left: ReaderResult[Any]): MatchResult = {
        inside(left) { case Left(ConfigReaderFailures(failure, Nil)) => pf.valueAt(failure) }
      }
    }
}
