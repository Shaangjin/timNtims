package seoultech.itm.timntims.home

import seoultech.itm.timntims.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.model.Chat
import java.time.LocalDate
import java.util.Date


/**
 * A simple [Fragment] subclass.
 * Use the [RoomSetupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RoomSetupFragment : Fragment() {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    private val auth = Firebase.auth

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
        val v: View = inflater.inflate(R.layout.fragment_room_setup, container, false)

        val editName = v.findViewById<TextView>(R.id.editText1)
        val editCode = v.findViewById<TextView>(R.id.editText2)

        val buttonCreate = v.findViewById<Button>(R.id.button1)
        val buttonJoin = v.findViewById<Button>(R.id.button2)

        buttonCreate.setOnClickListener{
            val currentUserID = auth.currentUser?.uid
            val chatName = editName.text.toString()
            val currentTimeInMillis = System.currentTimeMillis()
            val roomId = generateRandomString(8)

            var newChat = Chat(roomId, chatName, currentTimeInMillis, false)

            databaseReference.child("users/$currentUserID/rooms/$roomId/").setValue(newChat) // 생성된 채팅방 아이디를 user의 rooms에 저장

            if (currentUserID != null) {
                databaseReference.child("chat_members/$roomId/$currentUserID").setValue(true) // chat_member에 해당 roomId의 멤버에 uid 추가
            }

            databaseReference.child("chat_rooms/$roomId").setValue(true) // chat_rooms에 room id 추가

            editName.text = ""
        }

        buttonJoin.setOnClickListener {
            val currentUserID = auth.currentUser?.uid
            val chatCode = editCode.text.toString() // 사용자가 입력한 채팅방 코드

            val chatRoomsReference = databaseReference.child("chat_rooms")

            chatRoomsReference.child(chatCode).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // 채팅방이 존재하는 경우
                        val roomId = dataSnapshot.key.toString()
                        val userRoomsReference = databaseReference.child("users/$currentUserID/rooms")

                        userRoomsReference.child(roomId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(roomSnapshot: DataSnapshot) {
                                if (roomSnapshot.exists()) {
                                    // 이미 채팅방에 가입한 경우
                                    Toast.makeText(requireContext(), "이미 가입한 채팅방입니다.", Toast.LENGTH_SHORT).show()
                                } else {
                                    // 채팅방에 가입하지 않은 경우
                                    val currentTimeInMillis = System.currentTimeMillis()
                                    val newChat = Chat(roomId, chatCode, currentTimeInMillis, false)

                                    // 사용자의 rooms에 채팅방 정보 저장
                                    userRoomsReference.child(roomId).setValue(newChat)

                                    // 사용자를 채팅 멤버로 추가
                                    databaseReference.child("chat_members/$roomId/$currentUserID").setValue(true)

                                }
                            }

                            override fun onCancelled(roomDatabaseError: DatabaseError) {
                                // 에러 처리
                                Toast.makeText(requireContext(), "데이터베이스 오류: ${roomDatabaseError.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        // 채팅방이 존재하지 않는 경우
                        Toast.makeText(requireContext(), "Please put an exact chat room code.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 에러 처리
                    Toast.makeText(requireContext(), "데이터베이스 오류: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })

            editCode.text = ""
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
         * @return A new instance of fragment RoomSetupFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoomSetupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}