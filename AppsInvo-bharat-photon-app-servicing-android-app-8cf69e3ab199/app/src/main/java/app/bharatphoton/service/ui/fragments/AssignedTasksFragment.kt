package app.bharatphoton.service.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentAssignedTasksBinding
import app.bharatphoton.service.ui.fragments.product_complaint.ProductComplaintFragment
import app.bharatphoton.service.ui.fragments.product_installation.ProductInstallationFragment
import app.bharatphoton.service.ui.fragments.product_subscription.SubscriptionInstallationFragment
import app.bharatphoton.service.utils.MyCustomViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class AssignedTasksFragment : Fragment() {
    private lateinit var binding : FragmentAssignedTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAssignedTasksBinding.inflate(layoutInflater)

        //Tabs layout
        val adapter = MyCustomViewPagerAdapter(childFragmentManager)
        adapter.addFragment(ProductInstallationFragment(), "Product Installation")
        adapter.addFragment(SubscriptionInstallationFragment(), "Subscription Installation")
        adapter.addFragment(ProductComplaintFragment(), "Product Complaints")

        (activity as BaseActivity).getStorageDetailsApiCall(requireContext())

        binding.viewPagerAssignedTask.adapter = adapter
        binding.tabsAssignedTask.setupWithViewPager(binding.viewPagerAssignedTask)
        binding.viewPagerAssignedTask.currentItem = 0
        binding.viewPagerAssignedTask.offscreenPageLimit = 1

        updateTabFont(binding.tabsAssignedTask)

        binding.tabsAssignedTask.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateTabFont(binding.tabsAssignedTask)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                updateTabFont(binding.tabsAssignedTask)
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
                updateTabFont(binding.tabsAssignedTask)
            }
        })


        binding.viewPagerAssignedTask.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                updateTabFont(binding.tabsAssignedTask)
            }
            override fun onPageSelected(position: Int) {
                updateTabFont(binding.tabsAssignedTask)
            }
            override fun onPageScrollStateChanged(state: Int) {
                updateTabFont(binding.tabsAssignedTask)
            }
        })

        return binding.root
    }

    private fun updateTabFont(tabLayout: TabLayout) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val tabTextView = (tab?.view as? ViewGroup)?.getChildAt(1) as? TextView
            tabTextView?.typeface = ResourcesCompat.getFont(requireContext(), R.font.marcellus_regular)
            if (i == tabLayout.selectedTabPosition) {
                tabTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.photon_orange))
            } else {
                tabTextView?.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }
    }
}