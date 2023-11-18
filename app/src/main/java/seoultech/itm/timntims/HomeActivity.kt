package seoultech.itm.timntims

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity(){

    private val roomSetupFragment by lazy { RoomSetupFragment() }
    private val roomListFragment by lazy { RoomListFragment() }
    private val userProfileFragment by lazy { UserProfileFragment() }
    private val fManager by lazy { supportFragmentManager }
    private lateinit var bottomNavigationView : NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { 
            
        }

        var fManager = supportFragmentManager
        var transaction = fManager.beginTransaction()

        transaction.add(R.id.fragment_container_view, roomListFragment)

        transaction.commit()
    }
    private fun NavigationBarView.setOnItemSelectedListener(function: (item: MenuItem) -> Unit) {
            when(it.itemId) {
                TODO("make button") -> {

                    changeFragment(roomSetupFragment)
                }
                R.id.second -> {
                    changeFragment(RoomListFragment)
                }
                R.id.third -> {
                    changeFragment(UserProfileFragment)
                }
            }
            true
        selectedItemId = R.id.first
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, fragment)
            .commit()
    }
}
