package controllers.util

import play.api.mvc.MultipartFormData.FilePart
import play.api.libs.Files.TemporaryFile

object FilePartExtensions {
  implicit class ContentTypeFilter(val file: FilePart[TemporaryFile]) extends AnyVal{
    def filterContentType(contentTypes: String*) = {
      val contentType = file.contentType.getOrElse("unknown")
      if (contentTypes.contains(contentType)) {
        Some(contentType)
      } else {
        None
      }
    }
  }
}
