package seoultech.itm.timntims.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import seoultech.itm.timntims.R
import seoultech.itm.timntims.adapter.SetUpFragmentAdapter
import seoultech.itm.timntims.model.RoomItem

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

    private var job: Job? = null
    private var listener: RoomActionListener? = null

    interface RoomActionListener {
        fun onRoomAdded()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RoomActionListener) {
            listener = context
        }
    }

    fun setFireBase(database: FirebaseDatabase, databaseReference: DatabaseReference, auth: FirebaseAuth) {
        this.database = database
        this.databaseReference = databaseReference
        this.auth = auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("ITM", "RoomCreateFragement onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_room_create, container, false)
        Log.d("ITM", "RoomCreateFragement onCreateView")

        val editName = v.findViewById<EditText>(R.id.editTextRoomName)
        val buttonCreate = v.findViewById<Button>(R.id.buttonRoomCreate)
        val textRoomCode = v.findViewById<TextView>(R.id.textRoomCode)
        val buttonCopyCode = v.findViewById<ImageButton>(R.id.buttonCopyCode)

        // Set up the listener for the copy button
        buttonCopyCode.setOnClickListener {
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Room Code", textRoomCode.text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Room code copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        buttonCreate.setOnClickListener {
            val currentUserID = auth.currentUser?.uid
            val chatName = editName.text.toString()

            if (chatName.isNullOrEmpty()) {
                activity?.let {
                    Toast.makeText(it, "Please set Tim name", Toast.LENGTH_LONG).show()
                }
            } else {
                job = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Generate a new unique key (room ID) using push()
                        val roomIdRef = databaseReference.child("chat_rooms").push()
                        val roomId = roomIdRef.key ?: return@launch // Exit if the key is null

                        val currentTimeInMillis = System.currentTimeMillis()
                        val newChat = RoomItem(roomId, chatName, currentTimeInMillis, false)

                        // Set the new chat room under the 'chat_rooms' node
                        roomIdRef.setValue(true).await()

                        // Create and set other details under corresponding nodes
                        databaseReference.child("users/$currentUserID/rooms/$roomId/").setValue(newChat).await()
                        databaseReference.child("chat_members/$roomId/$currentUserID").setValue(true).await()
                        databaseReference.child("messages/$roomId").setValue(true).await()

                        withContext(Dispatchers.Main) {
                            Log.d("RoomCreateFragment", "Room creation successful")
                            listener?.onRoomAdded()
                            activity?.let {
                                Toast.makeText(it, "New Tim '$chatName:$roomId'\n is Created", Toast.LENGTH_LONG).show()
                            }
                            // Set the room code in the TextView
                            textRoomCode.text = roomId
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            activity?.let {
                                Toast.makeText(it, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                editName.text.clear()
            }
        }

        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel() // Cancel the coroutine when the view is destroyed
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