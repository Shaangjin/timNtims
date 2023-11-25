package seoultech.itm.timntims

import android.annotation.SuppressLint
import java.io.FileInputStream
import java.io.FileOutputStream

import android.view.View
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import seoultech.itm.timntims.databinding.ActivityCalendar3Binding


class CalendarActivity3 : AppCompatActivity() {
    var userID: String = "userID"
    lateinit var fname: String
    lateinit var str: String
    lateinit var calendarView: CalendarView
    lateinit var updateBtn: Button
    lateinit var deleteBtn:Button
    lateinit var saveBtn:Button
    lateinit var diaryTextView: TextView
    lateinit var diaryContent:TextView
    lateinit var title:TextView
    lateinit var contextEditText: EditText

    private val binding by lazy {ActivityCalendar3Binding.inflate(layoutInflater)}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // UI값 생성
        calendarView=binding.calendarView
        diaryTextView=binding.diaryTextView
        saveBtn=binding.saveBtn
        deleteBtn=binding.deleteBtn
        updateBtn=binding.updateBtn
        diaryContent=binding.diaryContent
        title=binding.title
        contextEditText=binding.contextEditText

        title.text = "Team Calendar"

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            diaryTextView.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            contextEditText.visibility = View.VISIBLE
            diaryContent.visibility = View.INVISIBLE
            updateBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
            diaryTextView.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            contextEditText.setText("")
            checkDay(year, month, dayOfMonth, userID)
        }

        saveBtn.setOnClickListener {
            saveDiary(fname)
            contextEditText.visibility = View.INVISIBLE
            saveBtn.visibility = View.INVISIBLE
            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            str = contextEditText.text.toString()
            diaryContent.text = str
            diaryContent.visibility = View.VISIBLE
        }
    }

    // 달력 내용 조회, 수정
    fun checkDay(cYear: Int, cMonth: Int, cDay: Int, userID: String) {
        //저장할 파일 이름설정
        fname = "" + userID + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt"

        var fileInputStream: FileInputStream
        try {
            fileInputStream = openFileInput(fname)
            val fileData = ByteArray(fileInputStream.available())
            fileInputStream.read(fileData)
            fileInputStream.close()
            str = String(fileData)
            contextEditText.visibility = View.INVISIBLE
            diaryContent.visibility = View.VISIBLE
            diaryContent.text = str
            saveBtn.visibility = View.INVISIBLE
            updateBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            updateBtn.setOnClickListener {
                contextEditText.visibility = View.VISIBLE
                diaryContent.visibility = View.INVISIBLE
                contextEditText.setText(str)
                saveBtn.visibility = View.VISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                diaryContent.text = contextEditText.text
            }
            deleteBtn.setOnClickListener {
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                contextEditText.setText("")
                contextEditText.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                removeDiary(fname)
            }
            if (diaryContent.text == null) {
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                diaryTextView.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                contextEditText.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // 달력 내용 제거
    @SuppressLint("WrongConstant")
    fun removeDiary(readDay: String?) {
        var fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS)
            val content = ""
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    // 달력 내용 추가
    @SuppressLint("WrongConstant")
    fun saveDiary(readDay: String?) {
        var fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS)
            val content = contextEditText.text.toString()
            fileOutputStream.write(content.toByteArray())
            fileOutputStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //gooogle calendar
    fun readCalendar() {
        val a = CalendarContract.Calendars.NAME
        val b = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        val c = CalendarContract.Calendars.ACCOUNT_NAME
        val d = CalendarContract.Calendars.ACCOUNT_TYPE
        val e = CalendarContract.Calendars.CALENDAR_COLOR

        val projection = arrayOf(a, b, c, d, e)

        val cursor = contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection, null,null,null)

        val aa = cursor!!.getColumnIndex(a)
        val bb = cursor.getColumnIndex(b)
        val cc = cursor.getColumnIndex(c)
        val dd = cursor.getColumnIndex(d)
        val ee = cursor.getColumnIndex(e)


        while (cursor.moveToNext()) {
            val aaa = cursor.getString(aa)
            val bbb = cursor.getString(bb)
            val ccc = cursor.getString(cc)
            val ddd = cursor.getString(dd)
            val eee = cursor.getString(ee)

            val msg = "TAG" +
                    "\nCalendars.NAME: $bbb" +
                    "\nCalendars.CALENDAR_DISPLAY_NAME: $aaa" +
                    "\nCalendarContract.Calendars.ACCOUNT_NAME: $ccc" +
                    "\nCalendarContract.Calendars.ACCOUNT_TYPE: $ddd" +
                    "\nCalendarContract.Calendars.CALENDAR_COLOR: $eee"
            Log.d("Google Calendar",msg)
        }
        cursor.close()
    }

}