package seoultech.itm.timntims.calendar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EventLog")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val eventID : Long = 0,
    @ColumnInfo(name= "Date") val date : Long,
    @ColumnInfo(name= "Description") val description : String,
    @ColumnInfo(name= "ChatRoomID") val chatRoomID : String,
    @ColumnInfo(name= "SenderID") val senderID : String,
    @ColumnInfo(name= "LogType") val logType : Int
)
