package seoultech.itm.timntims.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.MainActivity3
import seoultech.itm.timntims.R
import seoultech.itm.timntims.adapter.RoomListAdapter
import seoultech.itm.timntims.model.RoomItem
import androidx.recyclerview.widget.DividerItemDecoration


/**
 * A simple [Fragment] subclass.
 * Use the [RoomListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RoomListFragment : Fragment(), RoomListAdapter.OnRoomItemClickListener {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    private val user = Firebase.auth.currentUser

    private lateinit var roomsRecyclerView: RecyclerView
    private lateinit var roomListAdapter: RoomListAdapter
    private var roomsList = ArrayList<RoomItem>()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_room_list, container, false)

        roomsRecyclerView = v.findViewById(R.id.roomListRecyclerView)
        roomListAdapter = RoomListAdapter(roomsList, this)
        roomsRecyclerView.adapter = roomListAdapter
        roomsRecyclerView.layoutManager = LinearLayoutManager(context)

        loadRooms()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dividerItemDecoration = DividerItemDecoration(roomsRecyclerView.context, LinearLayoutManager.VERTICAL)
        roomsRecyclerView.addItemDecoration(dividerItemDecoration)

        val swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val roomToDelete = roomsList[position]

                // Delete room from Firebase using chatId
                roomToDelete.chatId?.let { deleteRoomFromFirebase(it) }

                // Remove item from list and notify adapter
                roomsList.removeAt(position)
                roomListAdapter.notifyItemRemoved(position)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val itemView = viewHolder.itemView
                val background = ColorDrawable()
                val backgroundColor = Color.argb(255, (255 + dX / 4).toInt().coerceAtLeast(0), 0, 0) // Becomes more red as item is swiped left

                background.color = backgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(roomsRecyclerView)
    }

    private fun deleteRoomFromFirebase(chatId: String) {
        val userId = user?.uid
        if (userId != null) {
            val userRoomsRef = databaseReference.child("users").child(userId).child("rooms").child(chatId)
            val chatMembersRef = databaseReference.child("chat_members").child(chatId).child(userId)

            userRoomsRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    chatMembersRef.removeValue().addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            // Show Toast message when both deletions are successful
                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "Room successfully deleted", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Handle failure
                    Log.e(TAG, "Failed to delete room: ${task.exception?.message}")
                }
            }
        } else {
            Log.e(TAG, "User ID is null")
        }
    }

    private fun loadRooms() {
        val userId = user?.uid
        if (userId != null) {
            val roomsRef = databaseReference.child("users").child(userId).child("rooms")

            roomsRef.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val room = snapshot.getValue(RoomItem::class.java)
                    room?.let {
                        roomsList.add(it)
                        roomListAdapter.notifyItemInserted(roomsList.size - 1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // 필요한 경우 여기에 방 정보 변경 로직을 구현합니다.
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "loadRooms:onCancelled", error.toException())
                }
            })
        } else {
            // userID가 null일 때 처리 로직
            Log.e(TAG, "User ID is null")
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoomListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoomListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRoomItemClick(chatId: String) {
        val intent = Intent(requireContext(), MainActivity3::class.java).apply {
            putExtra("chatId", chatId)
        }
        startActivity(intent)
    }
}