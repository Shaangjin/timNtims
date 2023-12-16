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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import seoultech.itm.timntims.R
import seoultech.itm.timntims.adapter.SetUpFragmentAdapter
import seoultech.itm.timntims.model.Chat
import kotlin.coroutines.coroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoomJoinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoomJoinFragment : Fragment() {
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
        val v: View = inflater.inflate(R.layout.fragment_room_join, container, false)

        val editCode = v.findViewById<EditText>(R.id.editTextRoomCode)
        val buttonJoin = v.findViewById<Button>(R.id.buttonRoomJoin)

        Log.d("ITM", "$auth.currentUser?.uid")


        buttonJoin.setOnClickListener {
            val currentUserID = auth.currentUser?.uid
            val chatCode = editCode.text.toString() // 사용자가 입력한 채팅방 코드

            // 백그라운드 스레드에서 Firebase 작업을 수행하기 위해 코루틴을 사용합니다.
            // CoroutineScope(Dispatchers.IO).launch 블록 내에서 Firebase 작업을 실행합니다.
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val chatRoomsReference = databaseReference.child("chat_rooms")

                    val dataSnapshot = chatRoomsReference.child(chatCode).get().await()
                    if (dataSnapshot.exists()) {
                        // 채팅방이 존재하는 경우
                        val roomId = dataSnapshot.key.toString()
                        val userRoomsReference = databaseReference.child("users/$currentUserID/rooms")

                        val roomSnapshot = userRoomsReference.child(roomId).get().await()
                        if (roomSnapshot.exists()) {
                            // 이미 채팅방에 가입한 경우
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "이미 가입한 채팅방입니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // 채팅방에 가입하지 않은 경우
                            val currentTimeInMillis = System.currentTimeMillis()
                            val newChat = Chat(roomId, chatCode, currentTimeInMillis, false)

                            // 사용자의 rooms에 채팅방 정보 저장
                            userRoomsReference.child(roomId).setValue(newChat).await()

                            // 사용자를 채팅 멤버로 추가
                            databaseReference.child("chat_members/$roomId/$currentUserID").setValue(true).await()
                        }
                    } else {
                        // 채팅방이 존재하지 않는 경우
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Please put an exact chat room code.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    // Firebase 작업 중 발생하는 예외 처리
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Firebase 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            editCode.text.clear()
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
         * @return A new instance of fragment test2fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoomJoinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}