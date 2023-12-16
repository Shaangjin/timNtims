package seoultech.itm.timntims.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import seoultech.itm.timntims.R
import seoultech.itm.timntims.sign.SignInActivity

class HomeActivity : AppCompatActivity(), SettingFragment.Callbacks, RoomCreateFragment.RoomActionListener, RoomJoinFragment.RoomActionListener {

    private val roomSetupFragment by lazy { RoomSetupFragment() }
    private val roomListFragment by lazy { RoomListFragment() }
    private val settingFragment by lazy { SettingFragment() }
//    private val fManager by lazy { supportFragmentManager }
    private lateinit var bottomNavigationView : NavigationBarView

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private val databaseReference: DatabaseReference = database.reference
//    private val auth = Firebase.auth

    override fun onRoomAdded() {
        // Update the icon on the second item of BottomNavigationView
        val menuItem = bottomNavigationView.menu.findItem(R.id.second)
        menuItem.icon = ContextCompat.getDrawable(this, R.drawable.baseline_mark_unread_chat_alt_24) // replace 'your_new_icon' with your icon resource
    }

    override fun finishHomeActivity() { // Override callback function in UserProfileFragment
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        if (savedInstanceState == null) {
            changeFragment(roomSetupFragment, "RoomSetupFragment")
        }

        // Bottom Navigation View 리스너 설정
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.first -> {
                    changeFragment(roomSetupFragment, "RoomSetupFragment")
                    true
                }
                R.id.second -> {
                    changeFragment(roomListFragment, "RoomListFragment")
                    item.icon = ContextCompat.getDrawable(this, R.drawable.baseline_chat_24) // 원래 아이콘으로 변경
                    true
                }
                R.id.third -> {
                    changeFragment(settingFragment, "SettingFragment")
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.setOnItemReselectedListener { item ->
            // 이 부분은 일반적으로 빈 구현이 될 수 있으며, 필요에 따라 구현할 수 있습니다.
        }
    }

    private fun changeFragment(fragment: Fragment, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // 기존에 추가된 프래그먼트가 있는지 확인
        var existingFragment = supportFragmentManager.findFragmentByTag(tag)

        if (existingFragment == null) {
            // 프래그먼트가 없다면 새로 추가
            existingFragment = fragment
            fragmentTransaction.add(R.id.fragment_container_view, existingFragment, tag)
        }

        // 다른 모든 프래그먼트를 숨김
        supportFragmentManager.fragments.forEach {
            if (it != existingFragment) fragmentTransaction.hide(it)
        }

        // 현재 프래그먼트 표시
        fragmentTransaction.show(existingFragment).commit()
    }

}
