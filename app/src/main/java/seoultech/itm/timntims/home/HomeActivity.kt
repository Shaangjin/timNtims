package seoultech.itm.timntims.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import seoultech.itm.timntims.R
import seoultech.itm.timntims.sign.SignInActivity

class HomeActivity : AppCompatActivity(), UserProfileFragment.Callbacks {

    private val roomSetupFragment by lazy { RoomSetupFragment() }
    private val roomListFragment by lazy { RoomListFragment() }
    private val userProfileFragment by lazy { UserProfileFragment() }
    private val fManager by lazy { supportFragmentManager }
    private lateinit var bottomNavigationView : NavigationBarView

    override fun finishHomeActivity() { // Override callback function in UserProfileFragment
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
}
