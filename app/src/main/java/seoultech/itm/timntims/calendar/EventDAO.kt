package seoultech.itm.timntims.calendar

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface EventDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(todoItem: TodoItem)

    @Insert
    suspend fun insertList(todoItemList : MutableList<TodoItem>)

    @Delete
    fun delete(todoItem : TodoItem)

    @Query("DELETE FROM EventLog")
    fun deleteAll()

    @Query("SELECT * FROM EventLog")
    fun getAll(): MutableList<TodoItem>

    @Update
    fun updateEvent(todoItem : TodoItem)

    @Query("SELECT * FROM EventLog WHERE Date = :date")
    fun getTodoItemsByDate(date: Long): MutableList<TodoItem>

}