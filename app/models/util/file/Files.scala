package models.util.file

import java.io.File

object Files {
  def deleteAfter[A](file: File)(f: File => A) = {
    try {
      f(file)
    } finally {
      if (!file.delete) file.deleteOnExit
    }
  }
}
