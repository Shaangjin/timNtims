package seoultech.itm.timntims.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import seoultech.itm.timntims.R
import seoultech.itm.timntims.calendar.TodoItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TodoItemAdapter(context: Context, private val todoItems: List<TodoItem>) :
    ArrayAdapter<TodoItem>(context, 0, todoItems) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        val view = convertView ?: layoutInflater.inflate(R.layout.event_dialog, parent, false)

        val todoItem = todoItems[position]
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvContents = view.findViewById<TextView>(R.id.tvContents)
        val imgAuthorIcon = view.findViewById<ImageView>(R.id.imgAuthorIcon)

        tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(todoItem.date) ?: Date())
        tvContents.text = if (todoItem.contents.length > 20) todoItem.contents.substring(0, 20) + "..." else todoItem.contents

        when (todoItem.authorID) {
            "GPT" -> imgAuthorIcon.setImageResource(R.drawable.gpticon)
            "SummarizerDeeplearing" -> imgAuthorIcon.setImageResource(R.drawable.notify_icon)
            else -> imgAuthorIcon.setImageResource(R.drawable.image_icon)
        }

        return view
    }
}