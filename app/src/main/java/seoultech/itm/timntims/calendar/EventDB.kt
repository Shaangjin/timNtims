package seoultech.itm.timntims.calendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE= instance
                //return instance
                instance
            }
        }
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perform the actual migration. For example:
                // database.execSQL("ALTER TABLE TodoItem ADD COLUMN new_column INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE EventLog ADD COLUMN SenderID TEXT")
            }
        }
    }
}