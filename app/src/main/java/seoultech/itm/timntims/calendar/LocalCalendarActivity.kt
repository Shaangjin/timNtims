package seoultech.itm.timntims.calendar

//import com.prolificinteractive.materialcalendarview.CalendarDay
//import com.prolificinteractive.materialcalendarview.MaterialCalendarView
//import com.prolificinteractive.materialcalendarview.OnDateSelectedListener

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
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
import seoultech.itm.timntims.adapter.TodoItemAdapter
import seoultech.itm.timntims.databinding.ActivityLocalCalendarBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class LocalCalendarActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var calendarView: CalendarView
    val binding by lazy  { ActivityLocalCalendarBinding.inflate(layoutInflater)}
    val eventDB: EventDB by lazy { EventDB.getInstance(this)}
    private lateinit var chatId: String // chatId를 클래스 멤버 변수로 선언

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReferenceForEvent: DatabaseReference
    val auth = Firebase.auth

    //sensor
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastUpdate: Long = 0
    private var last_x: Float = 0.0f
    private var last_y: Float = 0.0f
    private var last_z: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d("ddddd", "$chatId in oncreate" )
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)



        calendarView = binding.localCalendar
        chatId = intent.getStringExtra("chatId") ?: "0000"
        Log.d("ddddd", "$chatId 52")
        databaseReferenceForEvent = database.getReference("messages/$chatId")
        Log.d("ddddd", "after databaseReference")
        clickCalendar(chatId)
        Log.d("ddddd", "before databaseListener")
        setupDatabaseListener(chatId)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val mySensor = event.sensor
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (mySensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val curTime = System.currentTimeMillis()
            if ((curTime - lastUpdate) > 100) {
                val diffTime = (curTime - lastUpdate)
                lastUpdate = curTime

                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {
                    val chatId = intent.getStringExtra("chatId") ?: "0000"
                    iconOnCalendar(chatId)
                    vibrator?.let {
                        if (it.hasVibrator()) {
                            // Vibrate for 500 milliseconds using VibrationEffect.createOneShot
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val effect = VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE)
                                it.vibrate(effect)
                            } else {
                                // Deprecated in API 26, but required for older versions
                                it.vibrate(1000)
                            }
                        }
                    }
                }
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

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
    private fun showAddTodoDialog(calendar: Calendar, chatId: String) {
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

    private fun showTodoListDialog(calendar: Calendar, todoItems: MutableList<TodoItem>, chatId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            var stringForAdapter: MutableList<String> = mutableListOf()
            todoItems.forEach { todoItem ->
                val dataType = todoItem.dataType
                val date = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(todoItem.date) ?: Date())
                val contents = if (todoItem.contents.length > 20) todoItem.contents.substring(0, 20) + "..." else todoItem.contents
                stringForAdapter.add("Type: $dataType\nContents: $contents\nTime: $date")
            }

            withContext(Dispatchers.Main) {
//                val listView = ListView(this@LocalCalendarActivity)
//                val adapter = ArrayAdapter(this@LocalCalendarActivity, android.R.layout.simple_list_item_1, stringForAdapter)
//                listView.adapter = adapter
                val listView = ListView(this@LocalCalendarActivity)
                val adapter = TodoItemAdapter(this@LocalCalendarActivity, todoItems)
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val todoItem = todoItems[position]
                    val title = when (todoItem.authorID) {
                        "GPT", "SummarizerDeeplearing" -> todoItem.authorID
                        else -> todoItem.dataType
                    }
                    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(todoItem.date) ?: Date())
                    showFullTextDialog(this@LocalCalendarActivity, title, "\nContents: ${todoItem.contents}\n\nTime: $time")
                }

                val dialog = AlertDialog.Builder(this@LocalCalendarActivity)
                    .setTitle(SimpleDateFormat("MMM d'th'", Locale.getDefault()).format(calendar.time))
                    .setView(listView)
                    .setNegativeButton("Close", null)
                    .create()
                dialog.show()
            }
        }
    }

    private fun showFullTextDialog(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }

    companion object {
        private const val SHAKE_THRESHOLD = 600 // 흔들림 감지 임계값
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }




}