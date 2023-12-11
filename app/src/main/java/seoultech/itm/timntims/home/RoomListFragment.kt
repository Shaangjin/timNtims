package seoultech.itm.timntims.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Comment
import seoultech.itm.timntims.MainActivity3
import seoultech.itm.timntims.R
import seoultech.itm.timntims.model.Chat

/**
 * A simple [Fragment] subclass.
 * Use the [RoomListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RoomListFragment : Fragment() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    private val user = Firebase.auth.currentUser

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var tmpChatRoomBtn: Button //temporary button for chatting room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

//        val chatRoomRef = databaseReference
//            .child("users")
//            .child("${user?.uid}")
//            .child("rooms")
//
//        val chatRoomList: MutableList<Chat> = mutableListOf()
//
//        val childEventListener = object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("ITM", "onChildAdded:" + dataSnapshot.key!!)
//
//// roomIterator를 사용하여 chat 데이터를 chatList에 추가
//                val roomIterator: Iterator<DataSnapshot> = dataSnapshot.children.iterator()
//                while (roomIterator.hasNext()) {
//                    val chatRoomDataSnapshot = roomIterator.next()
//                    val chatId = chatRoomDataSnapshot.child("chatId").getValue(String::class.java)
//                    val title = chatRoomDataSnapshot.child("title").getValue(String::class.java)
//                    val createDate = chatRoomDataSnapshot.child("createDate").getValue(String::class.java)?.toLong()
//                    val disabled = chatRoomDataSnapshot.child("disabled").getValue(String::class.java).toBoolean()
//                    Log.d("ITM", "onChildAdded:$chatRoomDataSnapshot")
//                    if (chatId != null && title != null && createDate != null && disabled != null) {
//                        val chat = Chat(chatId, title, createDate, disabled)
//                        chatRoomList.add(chat)
//                    }
//                }
//
//                Log.d("ITM", "onChildAdded:$chatRoomList")
////                val comment = dataSnapshot.getValue<Comment>()
//
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("ITM", "onChildChanged: ${dataSnapshot.key}")
//
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so displayed the changed comment.
//                val newComment = dataSnapshot.getValue<Comment>()
////                val commentKey = dataSnapshot.key
//
//                // ...
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                Log.d("ITM", "onChildRemoved:" + dataSnapshot.key!!)
//
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so remove it.
////                val commentKey = dataSnapshot.key
//
//                // ...
//            }
//
//            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d("ITM", "onChildMoved:" + dataSnapshot.key!!)
//
//                // A comment has changed position, use the key to determine if we are
//                // displaying this comment and if so move it.
////                val movedComment = dataSnapshot.getValue<Comment>()
////                val commentKey = dataSnapshot.key
//
//                // ...
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w("ITM", "postComments:onCancelled", databaseError.toException())
////                Toast.makeText(
////                    context,
////                    "Failed to load comments.",
////                    Toast.LENGTH_SHORT,
////                ).show()
//            }
//        }
//
//        chatRoomRef.addChildEventListener(childEventListener)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val v = inflater.inflate(R.layout.fragment_room_list, container, false)
        tmpChatRoomBtn = v.findViewById(R.id.tmpChatRoom)

        tmpChatRoomBtn.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity3::class.java)
            startActivity(intent)
        }

        return v
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
}