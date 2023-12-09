package seoultech.itm.timntims.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import seoultech.itm.timntims.R


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
        // Inflate the layout for this fragment
//        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//        val databaseReference: DatabaseReference = database.reference
//
//        val path = "users/userInfo"
//
//        val userData = hashMapOf(
//            "username" to "JohnDoe",
//            "email" to "johndoe@example.com",
//            "age" to 30
//        )
//        databaseReference.child(path).setValue(userData)
//            .addOnSuccessListener {
//                // 데이터 쓰기 성공 시 동작
//                println("데이터 쓰기 성공!")
//            }
//            .addOnFailureListener {
//                // 데이터 쓰기 실패 시 동작
//                println("데이터 쓰기 실패: $it")
//            }

        return inflater.inflate(R.layout.fragment_room_setup, container, false)
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