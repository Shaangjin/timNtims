package seoultech.itm.timntims

import android.app.Activity
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import seoultech.itm.timntims.databinding.ActivityCalendar2Binding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarActivity2 : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    val binding by lazy { ActivityCalendar2Binding.inflate(layoutInflater) }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>){

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>){

    }


    val permissionsCode = 100
    val requiredPermissions = arrayOf(
        android.Manifest.permission.GET_ACCOUNTS,
        android.Manifest.permission.READ_CALENDAR,
        android.Manifest.permission.WRITE_CALENDAR)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this, requiredPermissions, permissionsCode)

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dec = DecimalFormat("00")
            val today = "${year}-${dec.format(month+1)}-${dec.format(dayOfMonth)}"
            Log.d("Calendar_test",today)
            binding.logText.text = readAllCalendarEvents().toString()
        }
    }

    fun readCalendarEvents(today: String): MutableList<String> {

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val eventList = mutableListOf<String>()

        val organizerCol = CalendarContract.Events.ORGANIZER
        val titleCol = CalendarContract.Events.TITLE
        val startDateCol = CalendarContract.Events.DTSTART
        val endDateCol = CalendarContract.Events.DTEND

        val projection = arrayOf(organizerCol, titleCol, startDateCol, endDateCol)

        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection, null,null,null)

        val organizerColIdx = cursor!!.getColumnIndex(organizerCol)
        val titleColIdx = cursor.getColumnIndex(titleCol)
        val startDateColIdx = cursor.getColumnIndex(startDateCol)
        val endDateColIdx = cursor.getColumnIndex(endDateCol)

        while (cursor.moveToNext()) {
            val organizer = cursor.getString(organizerColIdx)
            val title = cursor.getString(titleColIdx)
            val startDate = formatter.format(Date(cursor.getLong(startDateColIdx)))
            val endDate = formatter.format(Date(cursor.getLong(endDateColIdx)))

            if (startDate == today) {
                val event = "$startDate $endDate $title $organizer"
                eventList.add(event)
            }
        }
        cursor.close()
        return eventList
    }

    fun readAllCalendarEvents() {

        val a = CalendarContract.Events.TITLE
        val b = CalendarContract.Events.CALENDAR_ID
        val c = CalendarContract.Events.DTSTART
        val d = CalendarContract.Events.DTEND
        val e = CalendarContract.Events.EVENT_TIMEZONE

        val projection = arrayOf(a, b, c, d, e)

        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection, null,null,null)

        val aa = cursor!!.getColumnIndex(a)
        val bb = cursor.getColumnIndex(b)
        val cc = cursor.getColumnIndex(c)
        val dd = cursor.getColumnIndex(d)
        val ee = cursor.getColumnIndex(e)

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

        while (cursor.moveToNext()) {
            val aaa = cursor.getString(aa)
            val bbb = cursor.getString(bb)
            val ccc = cursor.getString(cc)
            val ddd = cursor.getString(dd)
            val eee = cursor.getString(ee)

            Log.d("Calendar_test","TAG" +
                    "\n$aaa" +
                    "\n$bbb" +
                    "\n$ccc" +
                    "\n$ddd" +
                    "\n$eee")
        }
        cursor.close()
    }

}
