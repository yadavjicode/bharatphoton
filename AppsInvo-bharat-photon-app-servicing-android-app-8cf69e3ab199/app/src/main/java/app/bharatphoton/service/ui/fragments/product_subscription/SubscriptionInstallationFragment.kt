package app.bharatphoton.service.ui.fragments.product_subscription

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.bharatphoton.service.R
import app.bharatphoton.service.databinding.FragmentSubscriptionInstallationBinding
import app.bharatphoton.service.utils.TabViewPagerAdapterWithImage
import com.google.android.material.tabs.TabLayout

class SubscriptionInstallationFragment : Fragment() {
    private lateinit var binding : FragmentSubscriptionInstallationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSubscriptionInstallationBinding.inflate(layoutInflater)

        //Tabs layout
        val adapter = TabViewPagerAdapterWithImage(childFragmentManager)
        adapter.addFragment(SubscriptionInstallationNewFragment(), R.drawable.tab_dot, "NEW")
        adapter.addFragment(SubscriptionInstallationPendingFragment(), R.drawable.tab_dot, "PENDING")
        adapter.addFragment(SubscriptionInstallationCompletedFragment(), R.drawable.tab_dot, "COMPLETED")

        binding.viewPagerSubscriptionInstallation.adapter = adapter
        binding.tabsSubscriptionInstallation.setupWithViewPager(binding.viewPagerSubscriptionInstallation)
        binding.viewPagerSubscriptionInstallation.currentItem = 0
        binding.viewPagerSubscriptionInstallation.offscreenPageLimit = 1

        for (i in 0 until adapter.count) {
            val tab = binding.tabsSubscriptionInstallation.getTabAt(i)
            tab?.customView = adapter.getTabView(i, requireContext())
        }

        fun updateTabIcons(selectedPosition: Int) {
            for (i in 0 until adapter.count) {
                val tab = binding.tabsSubscriptionInstallation.getTabAt(i)
                tab?.customView?.findViewById<ImageView>(R.id.tabIcon)?.visibility =
                    if (i == selectedPosition) View.VISIBLE else View.GONE
            }
        }

        binding.tabsSubscriptionInstallation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    updateTabIcons(position)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        updateTabIcons(binding.viewPagerSubscriptionInstallation.currentItem)

        return binding.root
    }
}