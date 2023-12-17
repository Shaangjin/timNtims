package seoultech.itm.timntims.adapter

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//
//class DialogRecyclerAdapter(list: ArrayList<String>?) :
//    RecyclerView.Adapter<DialogRecyclerAdapter.DialogViewHolder>() {
//
//    interface OnEventItemClickListener {
//        fun onEventItemClick(element: String)
//    }
//
//    class DialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val titleTextView: TextView = view.findViewById(seoultech.itm.timntims.R.id.event_date)
//        // 여기에 필요한 경우 다른 뷰 요소를 추가할 수 있습니다.
//    }
//
//    // ViewHolder를 생성하는 메서드
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(seoultech.itm.timntims.R.layout.day_dialog_item, parent, false)
//        return DialogViewHolder(view)
//    }
//
//    // 데이터를 ViewHolder에 바인딩하는 메서드
//    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
//        val eventItem = eventList[position]
//        holder.titleTextView.text = roomItem.title
//
//        // Set the click listener
//        holder.itemView.setOnClickListener {
//            listener.onRoomItemClick(roomItem.chatId ?: "")
//        }
//        // 여기에 클릭 리스너 등 추가 로직을 구현할 수 있습니다.
//    }
//
//    // 전체 아이템 개수를 반환하는 메서드
//    override fun getItemCount() = roomList.size
//}