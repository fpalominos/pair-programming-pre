package exercises.tradevalidationpipeline.validation.implicitexample

trait ValidatorI[E, A] {
  def validate(a: A): Either[E, A]
}

object ValidatorI {
  def apply[E, A](implicit v: ValidatorI[E, A]): ValidatorI[E, A] = v
  def instance[E, A](f: A => Either[E, A]): ValidatorI[E, A] =
    new ValidatorI[E, A] { def validate(a: A): Either[E, A] = f(a) }
  implicit class ValidatorIOps[E, A](private val a: A) extends AnyVal {
    def validate(implicit v: ValidatorI[E, A]): Either[E, A] = v.validate(a)
  }
}