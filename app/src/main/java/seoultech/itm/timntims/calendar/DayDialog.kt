package seoultech.itm.timntims.calendar

import android.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import seoultech.itm.timntims.databinding.DayDialogBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Inside your LocalCalendarActivity


// Helper function to filter items based on the calendar date
private fun getFilteredItems(todoItems: List<TodoItem>, calendar: Calendar): List<String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val selectedDate = sdf.format(calendar.time)
    return todoItems.filter {
        it.date.startsWith(selectedDate)
    }.map {
        "${it.contents} - ${sdf.format(Date(it.date))}"
    }
}
