package seoultech.itm.timntims.calendar

//import com.prolificinteractive.materialcalendarview.CalendarDay
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView
//import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import seoultech.itm.timntims.R
import seoultech.itm.timntims.databinding.ActivityLocalCalendarBinding
import java.util.Calendar

class LocalCalendarActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    val binding by lazy  { ActivityLocalCalendarBinding.inflate(layoutInflater)}
    val eventDB: EventDB by lazy { EventDB.getInstance(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        calendarView = binding.localCalendar
        setupCalendar()


    }

    private fun setupCalendar() {
        calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                addDotToCalendar(clickedDayCalendar)

                lifecycleScope.launch(Dispatchers.IO) {
                  val todoItems = getTodoItemsForDay(clickedDayCalendar)
                  withContext(Dispatchers.Main)  {
                      showTodoListDialog(clickedDayCalendar, todoItems)
                  }
//                    showTodoListDialog(clickedDayCalendar, todoItems)
                }

            }
        })
    }

    private fun addDotToCalendar(calendar: Calendar) {
        val events: MutableList<EventDay> = ArrayList()
        events.add(EventDay(calendar, R.drawable.image_icon))
        calendarView.setEvents(events)
    }

    private suspend fun getTodoItemsForDay(calendar: Calendar): MutableList<TodoItem> {


            val dao = eventDB.eventDAO()
            var items = dao.getTodoItemsByDate(calendar.timeInMillis)


            return items
    }

    private fun showTodoListDialog(calendar: Calendar, todoItems: List<TodoItem>) {
        val items = todoItems.map { it.description }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("To-Do List for ${calendar.time}")
            .setItems(items) { dialog, which ->
                // Handle item click, show edit and delete options
            }
            .setPositiveButton("Add") { dialog, which ->
                // Handle adding a new to-do item
                showAddTodoDialog(calendar)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showAddTodoDialog(calendar: Calendar) {
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

        AlertDialog.Builder(this)
            .setTitle("Add To-Do")
            .setView(input)
            .setPositiveButton("Add") { dialog, which ->
                val todoDescription = input.text.toString()
                addNewTodoItem(calendar, todoDescription)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addNewTodoItem(calendar: Calendar, description: String) {
        val newTodoItem = TodoItem(date = calendar.timeInMillis, description = description,
            chatRoomID = "1", senderID = "1", logType = 1)
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = eventDB.eventDAO()
            dao.insert(newTodoItem)
            withContext(Dispatchers.Main){
                Toast.makeText(this@LocalCalendarActivity, "To-Do added", Toast.LENGTH_SHORT).show()
            }

        }
    }



}