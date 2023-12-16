package seoultech.itm.timntims.calendar

//import com.prolificinteractive.materialcalendarview.CalendarDay
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView
//import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import seoultech.itm.timntims.R
import seoultech.itm.timntims.databinding.ActivityLocalCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LocalCalendarActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    val binding by lazy  { ActivityLocalCalendarBinding.inflate(layoutInflater)}
    val eventDB: EventDB by lazy { EventDB.getInstance(this)}

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ddddd", "oncreate start")
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        calendarView = binding.localCalendar

        Log.d("ddddd", "before databaseReference")
        databaseReference = database.getReference("messages/roomExampleFirst")
        Log.d("ddddd", "after databaseReference")
        setupCalendar()
        Log.d("ddddd", "before databaseListener")
        setupDatabaseListener()
    }


    private fun setupDatabaseListener() {
        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var todoItemList : MutableList<TodoItem> = mutableListOf()

                lifecycleScope.launch (Dispatchers.IO) {
                    val values = snapshot.children.forEach {
                        val dateKey = it.key.toString()
                        val authorID = it.child("authorID").getValue().toString()
                        val contents = it.child("contents").getValue().toString()
                        val date = it.child("date").getValue().toString()
                        val dataType = it.child("dataType").getValue().toString()
                        val chatRoomID = it.child("chatRoomID").getValue().toString()
                        Log.d("ddddd", "$authorID , $dateKey")
                        val todoItem = TodoItem(eventID = dateKey,
                                                authorID = authorID,
                                                contents = contents,
                                                date = date,
                                                dataType = dataType,
                                                chatRoomID = chatRoomID)
                        todoItemList.add(todoItem)
                    }
                    val dao = eventDB.eventDAO().insertList(todoItemList)
                }
//                Log.d("ddddd", "$values")
//                val chatInfo = snapshot.getValue(TodoItem::class.java)
//                Log.d("ddddd", "$chatInfo")
//                chatInfo?.let {
//                    todoItemList.add(it)
//
//
//                }

                // This will be called every time there is a change in the 'messages' node
//                lifecycleScope.launch (Dispatchers.IO){
//                    for (value in values)
//                }
//                lifecycleScope.launch(Dispatchers.IO) {
//                    for (value in values)
//
//                    value.forEach { message ->
//                        eventDB.eventDAO().insertOrUpdate(message)
//                    }
//                }
                // 'messages' now contains all the message objects
                // Do something with your messages list
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

//    private fun setupCalendar() {
//        lifecycleScope.launch(Dispatchers.IO) {
//
//            databaseReference.addListenerForSingleValueEvent(object: ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val messages = snapshot.children.mapNotNull { it.getValue(TodoItem::class.java) }
//                    // 'messages' now contains all the message objects
//                    // Do something with your messages list
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    // Handle error
//                }
//            })
//
//
//        }
//        }

        private fun setupCalendar() {
            calendarView.setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    val clickedDayCalendar = eventDay.calendar
                    addDotToCalendar(clickedDayCalendar)

                    lifecycleScope.launch(Dispatchers.IO) {
                        val todoItems = getTodoItemsForDay(clickedDayCalendar)
                        showTodoListDialog(clickedDayCalendar, todoItems)
                    }
                }
            })
        }
//            calendarView.setOnDayClickListener(object : OnDayClickListener {
//                override fun onDayClick(eventDay: EventDay) {
//                    val clickedDayCalendar = eventDay.calendar
//                    addDotToCalendar(clickedDayCalendar)
//                    lifecycleScope.launch {
//                        val todoItems = getTodoItemsForDay(eventDay.calendar)
//                        showTodoListDialog(eventDay.calendar, todoItems)
//                    }
//                }
//            }
//            )
//        }
//        calendarView.setOnDayClickListener(object : OnDayClickListener {
//            override fun onDayClick(eventDay: EventDay) {
//                val clickedDayCalendar = eventDay.calendar
//                addDotToCalendar(clickedDayCalendar)
//
//                lifecycleScope.launch(Dispatchers.IO) {
//                  val todoItems = getTodoItemsForDay(clickedDayCalendar)
//                  withContext(Dispatchers.Main)  {
//                      showTodoListDialog(clickedDayCalendar, todoItems)
//                  }
////                    showTodoListDialog(clickedDayCalendar, todoItems)
//                }
//
//            }})



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

    private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>) {
        lifecycleScope.launch(Dispatchers.IO) {
            val itemsForDay = eventDB.eventDAO().getTodoItemsByDate(calendar.timeInMillis)
            val formattedItems = itemsForDay.map { todoItem ->
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                "Contents: ${todoItem.contents}\nType: ${todoItem.dataType}\nTime: ${timeFormat.format(
                    Date(todoItem.date)
                )}"
            }.toTypedArray()
            withContext(Dispatchers.Main) {
                AlertDialog.Builder(this@LocalCalendarActivity)
                    .setTitle("Schedule for ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)}")
                    .setItems(formattedItems) { dialog, which ->
                        // Handle item click
                    }
                    .setPositiveButton("Add") { dialog, which ->
                        showAddTodoDialog(calendar)
                    }
                    .setNegativeButton("Close", null)
                    .show()
            }
//        val items = todoItems.map{it}.toTypedArray()
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = todoItem.date
//        }
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val dateString = dateFormat.format(calendar.time)
//
//        val message = "Description: ${todoItem.description}\nDate: $dateString"
//
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val dateOnly = dateFormat.format(calendar.time)
//
//        AlertDialog.Builder(this)
//            .setTitle("${dateOnly}")
//            .setItems(items) { todoItems, which ->
//                todoItems.forEach()
//            }
//            .setPositiveButton("Add") { dialog, which ->
//                // Handle adding a new to-do item
//                showAddTodoDialog(calendar)
//            }
//            .setNegativeButton("Close", null)
//            .show()
        }
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
                val contents = input.text.toString()
                addNewTodoItem(calendar, contents)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addNewTodoItem(calendar: Calendar, contents: String) {
//        databaseReference.addChildEventListener(object = )
        //auth
        val newTodoItem = TodoItem(eventID = calendar.timeInMillis.toString(),
            authorID = auth.currentUser?.uid,
            contents = contents,
            date = calendar.timeInMillis.toString(),
            dataType = "note",
            chatRoomID = "roomExampleFirst")
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = eventDB.eventDAO()
            dao.insert(newTodoItem)
            withContext(Dispatchers.Main){
                Toast.makeText(this@LocalCalendarActivity, "Added Schedule", Toast.LENGTH_SHORT).show()
            }
        }

    }



}