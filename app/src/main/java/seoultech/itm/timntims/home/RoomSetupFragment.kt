package seoultech.itm.timntims.home

import seoultech.itm.timntims.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference = database.reference
        val auth = Firebase.auth

        val editName = v.findViewById<TextView>(R.id.editText1)
        val editCode = v.findViewById<TextView>(R.id.editText2)

        val buttonCreate = v.findViewById<Button>(R.id.button1)
        val buttonJoin = v.findViewById<Button>(R.id.button2)

        buttonCreate.setOnClickListener{
            val currentUserID = auth.currentUser?.uid
            val chatName = editName.text.toString()
            val currentTimeInMillis = System.currentTimeMillis()

            var newChat = Chat(null, chatName, currentTimeInMillis, false)

            databaseReference.child("users/$currentUserID/rooms").setValue(newChat)

            databaseReference.child("chat_members/").setValue(currentUserID)


        }

        buttonJoin.setOnClickListener{
            val chatCode = editCode.text.toString()
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