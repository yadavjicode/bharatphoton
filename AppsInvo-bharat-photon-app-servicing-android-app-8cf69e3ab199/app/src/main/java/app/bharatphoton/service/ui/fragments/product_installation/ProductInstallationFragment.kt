package app.bharatphoton.service.ui.fragments.product_installation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.bharatphoton.service.R
import app.bharatphoton.service.databinding.FragmentProductInstallationBinding
import app.bharatphoton.service.utils.TabViewPagerAdapterWithImage
import com.google.android.material.tabs.TabLayout

class ProductInstallationFragment : Fragment() {
    private lateinit var binding : FragmentProductInstallationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProductInstallationBinding.inflate(layoutInflater)

        //Tabs layout
        val adapter = TabViewPagerAdapterWithImage(childFragmentManager)
        adapter.addFragment(ProductInstallationNewFragment(), R.drawable.tab_dot, "NEW")
        adapter.addFragment(ProductInstallationPendingFragment(), R.drawable.tab_dot, "PENDING")
        adapter.addFragment(ProductInstallationCompletedFragment(), R.drawable.tab_dot, "COMPLETED")

        binding.viewPagerProductInstallation.adapter = adapter
        binding.tabsProductInstallation.setupWithViewPager(binding.viewPagerProductInstallation)
        binding.viewPagerProductInstallation.currentItem = 0
        binding.viewPagerProductInstallation.offscreenPageLimit = 1

        for (i in 0 until adapter.count) {
            val tab = binding.tabsProductInstallation.getTabAt(i)
            tab?.customView = adapter.getTabView(i, requireContext())
        }

        fun updateTabIcons(selectedPosition: Int) {
            for (i in 0 until adapter.count) {
                val tab = binding.tabsProductInstallation.getTabAt(i)
                tab?.customView?.findViewById<ImageView>(R.id.tabIcon)?.visibility =
                    if (i == selectedPosition) View.VISIBLE else View.GONE
            }
        }

        binding.tabsProductInstallation.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    updateTabIcons(position)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        updateTabIcons(binding.viewPagerProductInstallation.currentItem)


        return binding.root
    }
}