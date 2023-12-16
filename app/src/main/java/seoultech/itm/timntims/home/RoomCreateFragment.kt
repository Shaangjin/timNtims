package seoultech.itm.timntims.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.R
import seoultech.itm.timntims.adapter.SetUpFragmentAdapter
import seoultech.itm.timntims.model.Chat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoomCreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoomCreateFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    fun setFireBase(database: FirebaseDatabase, databaseReference: DatabaseReference, auth: FirebaseAuth) {
        this.database = FirebaseDatabase.getInstance()
        this.databaseReference = databaseReference
        this.auth = auth
    }

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
        val v: View = inflater.inflate(R.layout.fragment_room_create, container, false)

        val editName = v.findViewById<EditText>(R.id.editTextRoomName)
        val buttonCreate = v.findViewById<Button>(R.id.buttonRoomCreate)


        buttonCreate.setOnClickListener{
            val currentUserID = auth.currentUser?.uid
            val chatName = editName.text.toString()
            val currentTimeInMillis = System.currentTimeMillis()
            val roomId = generateRandomString(8)

            Log.d("ITM", "$currentUserID")

            var newChat = Chat(roomId, chatName, currentTimeInMillis, false)

            Log.d("ITM", "$newChat")
            databaseReference.child("users/$currentUserID/rooms/$roomId/").setValue(newChat) // 생성된 채팅방 아이디를 user의 rooms에 저장


            if (currentUserID != null) {
                databaseReference.child("chat_members/$roomId/$currentUserID").setValue(true) // chat_member에 해당 roomId의 멤버에 uid 추가
            }

            databaseReference.child("chat_rooms/$roomId").setValue(true) // chat_rooms에 room id 추가

            databaseReference.child("messages/$roomId").setValue(true) // messages에 room id 추가

            editName.text.clear()
        }

        return v
    }

    private fun generateRandomString(length: Int): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { characters.random() }
            .joinToString("")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment test1fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoomCreateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}