package seoultech.itm.timntims.calendar

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "EventLog")
data class TodoItem(
    @PrimaryKey(autoGenerate = false) val eventID : String,
    @ColumnInfo(name= "authorID") val authorID : String?,
    @ColumnInfo(name= "contents") val contents : String,
    @ColumnInfo(name= "date") val date : String,
    @ColumnInfo(name= "dataType") val dataType : String,
    @ColumnInfo(name= "chatRoomID") val chatRoomID: String
)
