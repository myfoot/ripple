package models.chat

import org.squeryl._
import PrimitiveTypeMode._
import models.CoreSchema._

object ChatRoomRepository {
  def find(name:String):Option[ChatRoom] = {
    transaction{
      chatRooms.where(room => room.name === name).headOption
    }
  }
  def find(id:Long):Option[ChatRoom] = {
    transaction{
      chatRooms.where(room => room.id === id).headOption
    }
  }
  def findOrCreate(name:String):ChatRoom = {
    transaction{
      find(name).getOrElse(chatRooms.insert(new ChatRoom(name)))
    }
  }
  def all:List[ChatRoom] = {
    transaction {
      chatRooms.toList
    }
  }
}
