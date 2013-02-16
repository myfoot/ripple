package util.string

import org.specs2.mutable.Specification

class StringExtensionSpec extends Specification {
  import StringExtension._

  "#trimSpace" should {
    "文字列前後の半角全角スペースを取り除く" in {
      "  　hoge 　foo　   ".trimSpaces must equalTo("hoge 　foo")
    }
  }
}
