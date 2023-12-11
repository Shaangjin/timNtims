package seoultech.itm.timntims.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.R
import seoultech.itm.timntims.model.User
import seoultech.itm.timntims.sign.SignInActivity

class HomeActivity : AppCompatActivity(), UserProfileFragment.Callbacks {

    private val roomSetupFragment by lazy { RoomSetupFragment() }
    private val roomListFragment by lazy { RoomListFragment() }
    private val userProfileFragment by lazy { UserProfileFragment() }
    private val fManager by lazy { supportFragmentManager }
    private lateinit var bottomNavigationView : NavigationBarView

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = database.reference
    private val auth = Firebase.auth

    override fun finishHomeActivity() { // Override callback function in UserProfileFragment
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

//        savePreference()

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.first -> {
                    changeFragment(roomSetupFragment)
                    true
                }
                R.id.second -> {
                    changeFragment(roomListFragment)
                    true
                }
                R.id.third -> {
                    changeFragment(userProfileFragment)
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.setOnItemReselectedListener {item ->
            when(item.itemId) {
                R.id.first -> {
                    changeFragment(roomSetupFragment)
                    true
                }
                R.id.second -> {
                    changeFragment(roomListFragment)
                    true
                }
                R.id.third -> {
                    changeFragment(userProfileFragment)
                    true
                }
                else -> false
            }
        }

        var transaction = fManager.beginTransaction()

        transaction.add(R.id.fragment_container_view, roomSetupFragment)

        transaction.commit()
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }

//    fun savePreference() {
//        val sharedPref = getPreferences(Context.MODE_PRIVATE)
//        val currentUserId = auth.currentUser?.uid
//        val userRef = databaseReference.child("users/$currentUserId")
//        userRef.child("email").get()
//
//    }
}
