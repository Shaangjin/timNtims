package seoultech.itm.timntims.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import seoultech.itm.timntims.R
import seoultech.itm.timntims.model.RoomItem

class RoomListAdapter(private val roomList: List<RoomItem>) :
    RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>() {

    // ViewHolder 클래스 정의
    class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.roomTitle)
        // 여기에 필요한 경우 다른 뷰 요소를 추가할 수 있습니다.
    }

    // ViewHolder를 생성하는 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.room_list_item, parent, false)
        return RoomViewHolder(view)
    }

    // 데이터를 ViewHolder에 바인딩하는 메서드
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val roomItem = roomList[position]
        holder.titleTextView.text = roomItem.title
        // 여기에 클릭 리스너 등 추가 로직을 구현할 수 있습니다.
    }

    // 전체 아이템 개수를 반환하는 메서드
    override fun getItemCount() = roomList.size
}
