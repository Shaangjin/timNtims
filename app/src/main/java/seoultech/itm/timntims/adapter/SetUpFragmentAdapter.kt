package seoultech.itm.timntims.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import seoultech.itm.timntims.home.RoomSetupFragment

class SetUpFragmentAdapter(fragmentActivity: RoomSetupFragment) :
    FragmentStateAdapter(fragmentActivity) {
    var fragList = listOf<Fragment>()
    override fun getItemCount(): Int {
        return fragList.size
    }
    override fun createFragment(position: Int): Fragment {
        return fragList[position]
    }
}