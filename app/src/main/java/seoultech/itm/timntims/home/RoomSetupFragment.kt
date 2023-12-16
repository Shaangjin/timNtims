package seoultech.itm.timntims.home

import seoultech.itm.timntims.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

        val firFragment = RoomCreateFragment.newInstance("First","2022")
        val secFragment = RoomJoinFragment.newInstance("Second","2022")
        val myFrags = listOf(firFragment, secFragment)
        val fragAdapter = SetUpFragmentAdapter(this)
        fragAdapter.fragList = myFrags
        val viewPager = v.findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = fragAdapter

        // Setup TabLayout
        val tabLayout = v.findViewById<TabLayout>(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Create Tim" else "Join Tim"
        }.attach()

        firFragment.setFireBase(database, databaseReference, auth)
        secFragment.setFireBase(database, databaseReference, auth)

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