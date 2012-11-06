import play.api._
import play.api.db._
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.MySQLAdapter

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    SessionFactory.concreteFactory = Some( () => connection )
    def connection() = {
      Session.create(DB.getConnection()(app), new MySQLAdapter)
    }
  }
}
