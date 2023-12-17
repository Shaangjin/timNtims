package seoultech.itm.timntims.calendar

//import com.prolificinteractive.materialcalendarview.CalendarDay
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView
//import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
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
    private lateinit var databaseReferenceForEvent: DatabaseReference

    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d("ddddd", "$chatId in oncreate" )
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        calendarView = binding.localCalendar
        val chatId = intent.getStringExtra("chatId") ?: "0000"
        Log.d("ddddd", "$chatId 52")
        databaseReferenceForEvent = database.getReference("messages/$chatId")
        Log.d("ddddd", "after databaseReference")
        clickCalendar(chatId)
        Log.d("ddddd", "before databaseListener")
        setupDatabaseListener(chatId)
        iconOnCalendar(chatId)
    }

//    private fun iconOnCalendar(chatId: String){
//        var todoListByChatID: MutableList<TodoItem> = mutableListOf()
//        lifecycleScope.launch(Dispatchers.IO) {
//            val dao = eventDB.eventDAO().getAll()
//            dao.forEach {
//                if (it.chatRoomID == chatId) {
//                    todoListByChatID.add(it)
//                }
//            }

//            val events: MutableList<EventDay> = ArrayList()
//            todoListByChatID.forEach { todoItem ->
//                Log.d("eeeee", "${todoItem.authorID}, ${todoItem.dataType}")
//                val itemCalendar = parseDateToCalendar(todoItem.date)
//                Log.d("eeeee", "${itemCalendar.time}")
//                when (todoItem.dataType) {
//                    "img" -> events.add(EventDay(itemCalendar, R.drawable.image_icon))
//                    "text" -> {
//                        when (todoItem.authorID) {
//
//                            "GPT" -> events.add(EventDay(itemCalendar, R.drawable.gpt))
//                            "SummarizerDeeplearing" -> events.add(EventDay(itemCalendar, R.drawable.notify_icon))
//                        }
//                    }
//
//                }
//            }
//
//            Log.d("eeeee", "${events.toString()}")
//            withContext(Dispatchers.Main) {
//
//                calendarView.setEvents(events)
//            }
//        }

    private fun iconOnCalendar(chatId: String) {
        var todoListByChatID: MutableList<TodoItem> = mutableListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = eventDB.eventDAO().getAll()
            dao.forEach {
                if (it.chatRoomID == chatId) {
                    todoListByChatID.add(it)
                }
            }

            val eventsMap: MutableMap<Calendar, MutableList<Int>> = mutableMapOf()
            todoListByChatID.forEach { todoItem ->
                val itemCalendar = parseDateToCalendar(todoItem.date)
                val icon = when {
                    todoItem.dataType == "img" -> R.drawable.image_icon
                    todoItem.authorID == "GPT" -> R.drawable.gpt
                    todoItem.authorID == "SummarizerDeeplearing" -> R.drawable.notify_icon
                    else -> null
                }
                if (icon != null) {
                    eventsMap.getOrPut(itemCalendar) { mutableListOf() }.add(icon)
                }
            }

            val events: MutableList<EventDay> = ArrayList()
            eventsMap.forEach { (calendar, icons) ->
                icons.forEach { icon ->
                    events.add(EventDay(calendar, icon))
                }
            }

            withContext(Dispatchers.Main) {
                calendarView.setEvents(events)
            }
        }
    }

    private fun parseDateToCalendar(dateString: String): Calendar {// 먼저 "yyyy-MM-dd" 형식으로 날짜 부분만 파싱합니다.
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString.split(" ")[0]) // " "로 분리하여 날짜 부분만 사용합니다.

        return Calendar.getInstance().apply {
            time = date ?: Date() // 파싱된 날짜를 설정하거나 실패한 경우 현재 날짜를 사용합니다.
            set(Calendar.HOUR_OF_DAY, 0) // 시간을 0으로 설정합니다.
            set(Calendar.MINUTE, 0) // 분을 0으로 설정합니다.
            set(Calendar.SECOND, 0) // 초를 0으로 설정합니다.
            set(Calendar.MILLISECOND, 0) // 밀리초도 0으로 설정합니다.
        }
    }

    //from firebase message data, import each chat room's data to localData
    private fun setupDatabaseListener(chatId: String) {
        iconOnCalendar(chatId)
        databaseReferenceForEvent.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var todoItemList : MutableList<TodoItem> = mutableListOf()

                lifecycleScope.launch (Dispatchers.IO) {
                    val values = snapshot.children.forEach {
                        val dateKey = it.key.toString()
                        val authorID = it.child("authorID").getValue().toString()
                        val contents = it.child("contents").getValue().toString()
                        val date = it.child("date").getValue().toString()
                        val dataType = it.child("dataType").getValue().toString()
                        val chatRoomID = it.child("chatroomID").getValue().toString()
                        Log.d("ddddd", "$it")
                        Log.d("ddddd", "$chatRoomID")
                        if((authorID == "GPT" ||authorID == "SummarizerDeeplearing") || dataType=="img"){
                        val todoItem = TodoItem(eventID = dateKey,
                                                authorID = authorID,
                                                contents = contents,
                                                date = date,
                                                dataType = dataType,
                                                chatRoomID = chatRoomID,
                                                futureTime = null)
                        todoItemList.add(todoItem)
                        }
                    }
                    val dao = eventDB.eventDAO().insertList(todoItemList)
                    dao
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
        iconOnCalendar(chatId)
    }
        //it respond to click each day on calendar
        private fun clickCalendar(chatId: String) {
            calendarView.setOnDayClickListener(object : OnDayClickListener {
                override fun onDayClick(eventDay: EventDay) {
                    val clickedDayCalendar = eventDay.calendar

                    lifecycleScope.launch(Dispatchers.IO) {
                        val todoItems = getTodoItemsForDay(clickedDayCalendar)
                        Log.d("ggggg", "clickCalendar $todoItems")
                        showTodoListDialog(clickedDayCalendar, todoItems, chatId)
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
        // Format the timeInMillis to "yyyy-MM-dd"
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = sdf.format(calendar.time)
        val dao = eventDB.eventDAO()
        val items = dao.getAll() // This should retrieve all items, not just by date
        // Filter items where the date matches the selected date
        Log.d("fffff", "${items.toString()}")
        val list = items.filter { todoItem ->
            val itemDate = todoItem.date.split(" ")[0]
            Log.d("fffff", "$itemDate")// Assuming 'date' is in "yyyy-MM-dd hh:mm:ss" format
            itemDate == selectedDate
        }.toMutableList()
        Log.d("fffff", "return value $list")
        return list
    }

    private suspend fun getTodoItems(calendar: Calendar): MutableList<TodoItem> {
        val dao = eventDB.eventDAO()
        var items = dao.getTodoItemsByDate(calendar.timeInMillis)
        return items
    }

//    private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>, chatId: String) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val itemsForDay = getTodoItemsForDay(calendar)
//
//                AlertDialog.Builder(this)
//                    .setTitle("Add To-Do")
//                    .setView(contentsInput)
//                    .setPositiveButton("Add") { dialog, which ->
//                        val contents = contentsInput.text.toString()
//                        val futureTime = futureTimeInput.text.toString()
//                        addNewTodoItem(calendar, contents, futureTime ,chatId)
//                    }
//                    .setNegativeButton("Cancel", null)
//                    .show()
//
//        }
//    }


    private fun showAddTodoDialog(calendar: Calendar, chatId: String) {
//        val futureTimeInput = EditText(this)
//        val contentsInput = EditText(this)
//        val lp = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.MATCH_PARENT
//        )
//        contentsInput.layoutParams = lp
//        futureTimeInput.layoutParams = lp
//
//         = getTodoItemsForDay(calendar)
//
//        AlertDialog.Builder(this)
//            .setTitle("Add To-Do")
//            .setView(getTodoItemsForDay())
//            .setPositiveButton("Add") { dialog, which ->
//                val contents = contentsInput.text.toString()
//                val futureTime = futureTimeInput.text.toString()
//                addNewTodoItem(calendar, contents, futureTime ,chatId)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
    }

    private fun addNewTodoItem(calendar: Calendar, contents: String, futureTime: String?, chatId : String) {
//        databaseReference.addChildEventListener(object = )
        //auth
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val saveTimeNow = Date()
        val newTodoItem = TodoItem(eventID = dateFormat.format(saveTimeNow),
            authorID = auth.currentUser?.uid,
            contents = contents,
            date = dateFormat.format(saveTimeNow),
            dataType = "note",
            chatRoomID = chatId,
            futureTime = futureTime)
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = eventDB.eventDAO()
            dao.insert(newTodoItem)
            withContext(Dispatchers.Main){
                Toast.makeText(this@LocalCalendarActivity, "Added Schedule", Toast.LENGTH_SHORT).show()
            }
            clickCalendar(chatId)
        }

    }

//    private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>, chatId: String) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val itemsForDay = eventDB.eventDAO().getTodoItemsByDate(calendar.timeInMillis)
//            Log.d("ggggg", "$itemsForDay")
//            val formattedItems = itemsForDay.map { todoItem ->
//                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//                "Contents: ${todoItem.contents}\nType: ${todoItem.dataType}\nTime: ${timeFormat.format(
//                    Date(todoItem.date)
//                )}"
//            }.toTypedArray()
//            withContext(Dispatchers.Main) {
//                AlertDialog.Builder(this@LocalCalendarActivity)
//                    .setTitle("Schedule for ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)}")
//                    .setItems(formattedItems) { dialog, which ->
//                        // Handle item click
//                    }
//                    .setPositiveButton("Add") { dialog, which ->
//                        showAddTodoDialog(calendar, chatId)
//                    }
//                    .setNegativeButton("Close", null)
//                    .show()
//            }
//
//        }
//    }


private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>, chatId: String) {
    lifecycleScope.launch(Dispatchers.IO) {

        Log.d("ggggg", "ongoing 325")
        val formattedItems = todoItems.map { todoItem ->
            Log.d("ggggg", "ongoing 327")
//            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            Log.d("ggggg", "ongoing 329")
            "Contents: ${todoItem.contents}\nType: ${todoItem.dataType}\nTime: ${todoItem.date}"
            Log.d("ggggg", "ongoing 331")
        }

        withContext(Dispatchers.Main) {
            val listView = ListView(this@LocalCalendarActivity)
            val adapter = ArrayAdapter(this@LocalCalendarActivity, android.R.layout.simple_list_item_1, formattedItems)
            Log.d("hhhhh", "${formattedItems.toString()}")
            listView.adapter = adapter

            listView.setOnItemClickListener { parent, view, position, id ->
                // 여기에 각 항목 클릭 시 수행할 동작을 구현합니다.
                val selectedItem = todoItems[position]
                Toast.makeText(this@LocalCalendarActivity, "Selected: ${selectedItem.contents}", Toast.LENGTH_SHORT).show()}

                val dialog = AlertDialog.Builder(this@LocalCalendarActivity)
                    .setTitle("${SimpleDateFormat("MM-dd", Locale.getDefault()).format(calendar.time)}")
                    .setView(listView)
                    .setPositiveButton("Add") { dialog, which ->
                        showAddTodoDialog(calendar, chatId)
                    }
                    .setNegativeButton("Close", null)
                    .create()

                dialog.show()
            }


        }
    }

//    private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>, chatId: String) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val formattedItems = todoItems.map { todoItem ->
//                "Contents: ${todoItem.contents}\nType: ${todoItem.dataType}\nTime: ${todoItem.date}"
//            }
//
//            withContext(Dispatchers.Main) {
//                val inflater = layoutInflater
//                val dialogView = inflater.inflate(R.layout.day_dialog, null)
//
//                val listView = dialogView.findViewById<ListView>(R.id.todoListView)
//                val adapter = ArrayAdapter(this@LocalCalendarActivity, android.R.layout.simple_list_item_1, formattedItems)
//                listView.adapter = adapter
//
//                listView.setOnItemClickListener { _, _, position, _ ->
//                    val selectedItem = todoItems[position]
//                    Toast.makeText(this@LocalCalendarActivity, "Selected: ${selectedItem.contents}", Toast.LENGTH_SHORT).show()
//                }
//
//                val dialog = AlertDialog.Builder(this@LocalCalendarActivity)
//                    .setTitle("${SimpleDateFormat("MM-dd", Locale.getDefault()).format(calendar.time)}")
//                    .setView(dialogView)
//                    .setPositiveButton("Add") { _, _ ->
//                        showAddTodoDialog(calendar, chatId)
//                    }
//                    .setNegativeButton("Close", null)
//                    .create()
//
//                dialog.show()
//            }
//        }
//    }


//    private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>, chatId: String) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val formattedItems = todoItems.map { todoItem ->
//                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
//                "Contents: ${todoItem.contents}\nType: ${todoItem.dataType}\nTime: ${todoItem.date}"
//            }
//
//            withContext(Dispatchers.Main) {
//                val dialogView = LayoutInflater.from(this@LocalCalendarActivity).inflate(R.layout.day_dialog, null)
//                val listView = ListView(this@LocalCalendarActivity)
//                val adapter = ArrayAdapter(this@LocalCalendarActivity, android.R.layout.simple_list_item_1, formattedItems)
//                listView.adapter = adapter
//
//                listView.setOnItemClickListener { _, _, position, _ ->
//                    val selectedItem = todoItems[position]
//                    Toast.makeText(this@LocalCalendarActivity, "Selected: ${selectedItem.contents}", Toast.LENGTH_SHORT).show()
//                }
//
//                // Set custom buttons
//                val addButton = dialogView.findViewById<Button>(R.id.dialog_add_button)
//                val closeButton = dialogView.findViewById<Button>(R.id.dialog_close_button)
//
//                addButton.setOnClickListener {
//                    showAddTodoDialog(calendar, chatId)
//                }
//
//                closeButton.setOnClickListener {
//                    null
//                }
//
//                val dialog = AlertDialog.Builder(this@LocalCalendarActivity)
//                    .setTitle("Schedule for ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)}")
//                    .setView(dialogView)
//                    .create()
//
//                dialog.show()
//            }
//        }
//    }






}