package seoultech.itm.timntims.home

import android.content.Context
import seoultech.itm.timntims.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.adapter.SetUpFragmentAdapter
import seoultech.itm.timntims.model.Chat


/**
 * A simple [Fragment] subclass.
 * Use the [RoomSetupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RoomSetupFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var fragAdapter: SetUpFragmentAdapter

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
        Log.d("ITM", "onCreate")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("ITM", "Attached")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ITM", "Destoryed")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("ITM", "Detached")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ITM", "onCreateView0")
        val view = inflater.inflate(R.layout.fragment_room_setup, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        setupViewPager()
        Log.d("ITM", "onCreateView1")
        return view
    }


//    private fun initializeFragments() {
//        if (!this::firFragment.isInitialized) {
//            firFragment = RoomCreateFragment()
//            firFragment.setFireBase(database, databaseReference, auth)
//        }
//        if (!this::secFragment.isInitialized) {
//            secFragment = RoomJoinFragment()
//            secFragment.setFireBase(database, databaseReference, auth)
//        }
//    }


    private fun setupViewPager() {
        fragAdapter = SetUpFragmentAdapter(this).apply {
            fragList = listOf(
                RoomCreateFragment().also { it.setFireBase(database, databaseReference, auth) },
                RoomJoinFragment().also { it.setFireBase(database, databaseReference, auth) }
            )
        }
        viewPager.adapter = fragAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.adapter = null // Prevent memory leaks
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