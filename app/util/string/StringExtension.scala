package util.string

class StringExtension(value: String) {
  def trimSpaces() = value.replaceAll(StringExtension.trimRegex, "")
}

object StringExtension {
  private val trimRegex = "^(\\s|　)+|(\\s|　)+$"
  implicit def toStringExtension(value: String) = new StringExtension(value)
}

