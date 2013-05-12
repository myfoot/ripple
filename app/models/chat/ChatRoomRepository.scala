package models.chat

import org.squeryl._
import PrimitiveTypeMode._
import models.CoreSchema._
import models.util.ValidatorTypes._
import models.chat.action.Talk
import scala.util.Try
import util.redis.RedisClient
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonAST.JValue

object ChatRoomRepository {
  def find(name:String):Option[ChatRoom] = {
    chatRooms.where(room => room.name === name).headOption
  }
  def find(id:Long):Option[ChatRoom] = {
    chatRooms.where(room => room.id === id).headOption
  }
  def all:List[ChatRoom] = {
    chatRooms.toList
  }
  def insert(chatRoom:ChatRoom): Either[Map[Symbol, ErrorNames], Any] = {
    chatRoom.validate match {
      case Left(x) => Left(x)
      case result @ Right(_) => {
        chatRooms.insert(chatRoom)
        result
      }
    }
  }
  def insert(chatRoom: ChatRoom, talk: Talk): Try[Talk] = {
    RedisClient.withClient{ client =>
      Try {
        client.lpush(chatRoom.talkKey, compact(render(talk.toJson)))
        talk
      }
    }
  }
  def talks(chatRoom: ChatRoom, count: Int, page: Int) : Seq[JValue] = RedisClient.withClient{ client =>
    val _page = if (page > 0) page - 1 else 0
    Try(client.lrange(chatRoom.talkKey, _page * count, _page * count + count - 1)).map{ talksJson =>
      talksJson.map(parse(_))
    }.getOrElse(Nil)
  }

}
