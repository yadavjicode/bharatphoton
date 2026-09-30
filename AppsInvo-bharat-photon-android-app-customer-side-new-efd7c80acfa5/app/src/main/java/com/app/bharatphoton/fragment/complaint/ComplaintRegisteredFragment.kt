package com.app.bharatphoton.fragment.complaint

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.formatReadableDate
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.zoomInOutAnimation
import com.app.bharatphoton.databinding.FragmentComplaintRegisteredBinding
import java.text.SimpleDateFormat
import java.util.Locale


class ComplaintRegisteredFragment : BaseFragment<FragmentComplaintRegisteredBinding>() {
    private var complaintNo = ""
    private var complaintDate = ""
    private val navOptions by lazy { Utils.navigateWithAnimation(requireContext()) }
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComplaintRegisteredBinding = FragmentComplaintRegisteredBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
    }

    private fun onClick() {
        with(binding) {
            back.setOnAnimatedClickListener { navigateBack() }
            backToHomeBtn.setOnAnimatedClickListener { navigateToScreen(id = R.id.homeFragment, bundle = null, navOptions = navOptions) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            handleOnPressBackButton { navigateBack() }
            zoomInOutAnimation(listOf(imageView) )
            complaintNo = arguments?.getString("complaintNo", "") ?: ""
            complaintDate = arguments?.getString("complaintDate", "") ?: ""
            val formatedDate = formatReadableDate(complaintDate, simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()))
            complaintNoTv.text = "Complaint No : $complaintNo"
            complaintDateTv.text = "Complaint date : $formatedDate"
        }
    }
}