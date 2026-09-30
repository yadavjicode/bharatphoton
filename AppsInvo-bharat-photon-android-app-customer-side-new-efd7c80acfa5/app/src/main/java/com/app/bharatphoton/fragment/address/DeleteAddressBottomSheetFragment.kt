@file:Suppress("DEPRECATION")

package com.app.bharatphoton.fragment.address

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentDeleteAddressBottomSheetBinding
import com.app.bharatphoton.viewModule.AddressViewModule
import com.github.ybq.android.spinkit.style.Circle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DeleteAddressBottomSheetFragment(val onClick : (String) -> Unit) : BottomSheetDialogFragment() {


    private lateinit var loader: ProgressDialog

    private val binding by lazy {
        FragmentDeleteAddressBottomSheetBinding.inflate(layoutInflater)
    }

    private val viewModule by viewModels<AddressViewModule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        initLoader()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    fun hideLoader() {
        if (loader.isShowing) {
            loader.dismiss()
        }
    }

    fun showLoader() {
        if (!loader.isShowing) {
            loader.show()
        }
    }

    private fun initData() {
        with(binding) {

        }
    }

    private fun initLoader() {
        val loaderStyle = Circle().apply {
            color = resources.getColor(R.color.Deep_Saffron)
        }
        loader = ProgressDialog(requireContext(), R.style.TransparentProgressDialog).apply {
            setCancelable(true)
            setIndeterminateDrawable(loaderStyle)
        }
    }

    private fun onClick() {
        with(binding) {
            noGoBack.setOnAnimatedClickListener {
                dismiss()
            }
            yesDelete.setOnAnimatedClickListener {
                onClick.invoke("delete")
                dialog?.dismiss()
            }
        }
    }


}