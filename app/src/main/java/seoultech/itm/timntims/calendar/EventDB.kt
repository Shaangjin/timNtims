package seoultech.itm.timntims.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoItem::class], version = 1)
abstract class EventDB: RoomDatabase() {
    abstract fun eventDAO(): EventDAO

    companion object {
        //singleton prevents multiple instance of database at the same time

        @Volatile
        private var INSTANCE: EventDB? = null

        fun getInstance(context: Context): EventDB{
            //if the INSTANCE is not null, then return it
            //else, create new database!
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDB::class.java,
                    "event_database"
                ).build()
                INSTANCE= instance
                //return instance
                instance
            }
        }
    }
}