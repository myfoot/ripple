package models.chat

import org.squeryl._
import PrimitiveTypeMode._
import models.CoreSchema._
import models.util.ValidatorTypes._

object ChatRoomRepository {
  def find(name:String):Option[ChatRoom] = {
    inTransaction{
      chatRooms.where(room => room.name === name).headOption
    }
  }
  def find(id:Long):Option[ChatRoom] = {
    inTransaction{
      chatRooms.where(room => room.id === id).headOption
    }
  }
  def findOrCreate(name:String):ChatRoom = {
    inTransaction{
      find(name).getOrElse(chatRooms.insert(new ChatRoom(name)))
    }
  }
  def all:List[ChatRoom] = {
    inTransaction {
      chatRooms.toList
    }
  }
  def insert(chatRoom:ChatRoom): Either[Map[Symbol, ErrorNames], Any] = {
    chatRoom.validate match {
      case Left(x) => Left(x)
      case result @ Right(_) => {
        inTransaction {
          chatRooms.insert(chatRoom)
        }
        result
      }
    }
  }
}
