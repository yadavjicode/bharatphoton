package app.bharatphoton.service.ui.fragments.product_complaint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.bharatphoton.service.R
import app.bharatphoton.service.databinding.FragmentProductComplaintBinding
import app.bharatphoton.service.utils.TabViewPagerAdapterWithImage
import com.google.android.material.tabs.TabLayout

class ProductComplaintFragment : Fragment() {
    private lateinit var binding : FragmentProductComplaintBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProductComplaintBinding.inflate(layoutInflater)

        //Tabs layout
        val adapter = TabViewPagerAdapterWithImage(childFragmentManager)
        adapter.addFragment(ProductComplaintNewFragment(), R.drawable.tab_dot, "NEW")
        adapter.addFragment(ProductComplaintPendingFragment(), R.drawable.tab_dot, "PENDING")
        adapter.addFragment(ProductComplaintCompletedFragment(), R.drawable.tab_dot, "COMPLETED")

        binding.viewPagerProductComplaint.adapter = adapter
        binding.tabsProductComplaint.setupWithViewPager(binding.viewPagerProductComplaint)
        binding.viewPagerProductComplaint.currentItem = 0
        binding.viewPagerProductComplaint.offscreenPageLimit = 1

        for (i in 0 until adapter.count) {
            val tab = binding.tabsProductComplaint.getTabAt(i)
            tab?.customView = adapter.getTabView(i, requireContext())
        }

        fun updateTabIcons(selectedPosition: Int) {
            for (i in 0 until adapter.count) {
                val tab = binding.tabsProductComplaint.getTabAt(i)
                tab?.customView?.findViewById<ImageView>(R.id.tabIcon)?.visibility =
                    if (i == selectedPosition) View.VISIBLE else View.GONE
            }
        }

        binding.tabsProductComplaint.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    updateTabIcons(position)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        updateTabIcons(binding.viewPagerProductComplaint.currentItem)


        return binding.root
    }
}