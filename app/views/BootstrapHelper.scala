package views

import views.html.twitterBootstrapField
import views.html.helper.FieldConstructor
 
object BootstrapHelper {
  implicit val bootstrapConstructor = FieldConstructor(twitterBootstrapField.f)
}
