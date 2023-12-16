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