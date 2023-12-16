package seoultech.itm.timntims.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import seoultech.itm.timntims.R
import seoultech.itm.timntims.model.User

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var callback: Callbacks? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var signOutBtn: Button
    private lateinit var textName: TextView
//    private lateinit var textLastName: TextView
    private lateinit var textEmail: TextView
    private lateinit var imageUser: ImageView
    private lateinit var editProfileBtn: Button

    interface Callbacks{
        fun finishHomeActivity()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as Callbacks
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
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_setting, container, false)

        signOutBtn = v.findViewById(R.id.signOutBtn)
        textName = v.findViewById(R.id.textName)
//        textLastName = v.findViewById(R.id.textLastName)
        textEmail = v.findViewById(R.id.textEmail)
        imageUser= v.findViewById(R.id.imageUser)
        editProfileBtn = v.findViewById(R.id.EditProfileBtn)

        signOutBtn.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            Toast.makeText(requireContext(), "Sign Out", Toast.LENGTH_SHORT).show()
            callback?.finishHomeActivity()
        }

        // Firebase에서 사용자 데이터 가져오기
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID != null) {
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(currentUserID)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("firstName", user.firstName)
                            editor.putString("lastName", user.lastName)
                            editor.putString("email", user.email)
                            editor.putString("profileImage", user.profileImage)
                            editor.apply()

                            // View에 정보 표시
                            textName.text = user.firstName + " " + user.lastName
//                            textLastName.text = user.lastName
                            textEmail.text = user.email
                            // Glide를 사용하여 이미지 설정
                            user.profileImage?.let {
                                Glide.with(requireContext())
                                    .load(it)
                                    .override(450, 450) // 원하는 크기로 조절
                                    .into(imageUser)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        }

        editProfileBtn.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        fetchUserData()
    }

    private fun fetchUserData() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserID != null) {
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(currentUserID)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        user?.let {
                            textName.text = user.firstName + " " + user.lastName
//                            textLastName.text = user.lastName
                            textEmail.text = user.email
                            Glide.with(requireContext()).load(user.profileImage)
                                .override(450, 450).into(imageUser)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}