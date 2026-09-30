package app.bharatphoton.service.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import app.bharatphoton.service.R
import com.google.android.material.tabs.TabLayout

class TabViewPagerAdapterWithImage(
    supportFragmentManager: FragmentManager
) : FragmentStatePagerAdapter(supportFragmentManager) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentIconList = ArrayList<Int>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, icon: Int, title: String) {
        mFragmentList.add(fragment)
        mFragmentIconList.add(icon)
        mFragmentTitleList.add(title)
    }

    fun getTabView(position: Int, context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
        val tabText = view.findViewById<TextView>(R.id.tabText)
        val tabIcon = view.findViewById<ImageView>(R.id.tabIcon)
        tabText.text = mFragmentTitleList[position]
        tabIcon.setImageResource(mFragmentIconList[position])
        return view
    }
}


