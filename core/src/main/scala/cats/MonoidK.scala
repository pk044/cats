package cats

import simulacrum.typeclass
import scala.annotation.implicitNotFound

/**
 * MonoidK is a universal monoid which operates on kinds.
 *
 * This type class is useful when its type parameter F[_] has a
 * structure that can be combined for any particular type, and which
 * also has an "empty" representation. Thus, MonoidK is like a Monoid
 * for kinds (i.e. parametrized types).
 *
 * A MonoidK[F] can produce a Monoid[F[A]] for any type A.
 *
 * Here's how to distinguish Monoid and MonoidK:
 *
 *  - Monoid[A] allows A values to be combined, and also means there
 *    is an "empty" A value that functions as an identity.
 *
 *  - MonoidK[F] allows two F[A] values to be combined, for any A.  It
 *    also means that for any A, there is an "empty" F[A] value. The
 *    combination operation and empty value just depend on the
 *    structure of F, but not on the structure of A.
 */
@implicitNotFound("Could not find an instance of MonoidK for ${F}")
@typeclass trait MonoidK[F[_]] extends SemigroupK[F] { self =>

  /**
   * Given a type A, create an "empty" F[A] value.
   *
   * Example:
   * {{{
   * scala> import cats.implicits._
   * scala> MonoidK[List].empty[Long]
   * res0: List[Long] = List()
   * }}}
   */
  def empty[A]: F[A]

  /**
   * Given a type A, create a concrete Monoid[F[A]].
   */
  override def algebra[A]: Monoid[F[A]] =
    new Monoid[F[A]] {
      def empty: F[A] = self.empty
      def combine(x: F[A], y: F[A]): F[A] = self.combineK(x, y)
    }

  override def compose[G[_]]: MonoidK[λ[α => F[G[α]]]] =
    new ComposedMonoidK[F, G] {
      val F = self
    }
}

object MonoidK {

  /****************************************************************************/
  /* THE FOLLOWING CODE IS MANAGED BY SIMULACRUM; PLEASE DO NOT EDIT!!!!      */
  /****************************************************************************/
  /**
   * Summon an instance of [[MonoidK]] for `F`.
   */
  @inline def apply[F[_]](implicit instance: MonoidK[F]): MonoidK[F] = instance

  trait Ops[F[_], A] {
    type TypeClassType <: MonoidK[F]
    def self: F[A]
    val typeClassInstance: TypeClassType
  }
  trait AllOps[F[_], A] extends Ops[F, A] with SemigroupK.AllOps[F, A] {
    type TypeClassType <: MonoidK[F]
  }
  trait ToMonoidKOps {
    implicit def toMonoidKOps[F[_], A](target: F[A])(implicit tc: MonoidK[F]): Ops[F, A] {
      type TypeClassType = MonoidK[F]
    } = new Ops[F, A] {
      type TypeClassType = MonoidK[F]
      val self: F[A] = target
      val typeClassInstance: TypeClassType = tc
    }
  }
  object nonInheritedOps extends ToMonoidKOps
  object ops {
    implicit def toAllMonoidKOps[F[_], A](target: F[A])(implicit tc: MonoidK[F]): AllOps[F, A] {
      type TypeClassType = MonoidK[F]
    } = new AllOps[F, A] {
      type TypeClassType = MonoidK[F]
      val self: F[A] = target
      val typeClassInstance: TypeClassType = tc
    }
  }

  /****************************************************************************/
  /* END OF SIMULACRUM-MANAGED CODE                                           */
  /****************************************************************************/

}
