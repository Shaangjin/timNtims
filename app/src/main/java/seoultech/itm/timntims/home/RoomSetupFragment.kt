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

            var newChat = Chat(null, chatName, currentTimeInMillis, false)

            databaseReference.child("users/$currentUserID/rooms/$roomId/").setValue(newChat) // 생성된 채팅방 아이디를 user의 rooms에 저장

            if (currentUserID != null) {
                databaseReference.child("chat_members/$roomId/$currentUserID").setValue(true)
            }

            databaseReference.child("chat_rooms/$roomId").setValue(true)

            editName.text = ""
        }

        buttonJoin.setOnClickListener {
            val currentUserID = auth.currentUser?.uid
            val chatCode = editCode.text.toString() // 사용자가 입력한 채팅방 코드

            // chat_rooms에서 입력한 채팅방 코드가 존재하는지 확인
            databaseReference.child("chat_rooms").child(chatCode).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // TODO 오류 수정 필요
                    if (dataSnapshot.exists()) {
                        // 채팅방이 존재하면 현재 사용자를 해당 채팅방에 추가
                        val chatRoomData = dataSnapshot.getValue(Map::class.java) as Map<String, Boolean>?
                        if (chatRoomData != null && currentUserID != null) {
                            // 현재 사용자의 rooms에 채팅방을 추가
                            databaseReference.child("users/$currentUserID/rooms").child(chatCode).setValue(true)

                            // 채팅 멤버 목록에 현재 사용자를 추가
                            databaseReference.child("chat_members/$chatCode").child(currentUserID).setValue(true)

                            // 사용자가 이미 채팅방에 속해 있음을 알리는 메시지 표시
                            Toast.makeText(requireContext(), "이미 채팅방에 속해 있습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // 채팅방이 존재하지 않는 경우 사용자에게 알림
                        Toast.makeText(requireContext(), "채팅방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // 데이터베이스 오류 처리
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