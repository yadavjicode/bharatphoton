package com.app.bharatphoton.fragment.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ZoomImageAdapter
import com.app.bharatphoton.databinding.FragmentZoomPhotosBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ZoomPhotosDialogFragment : DialogFragment() {

    private val binding by lazy {
        FragmentZoomPhotosBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataInAdapter()
        onClick()
    }

    private fun onClick() {
        binding.backBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun setDataInAdapter() {
        val imageList: List<String> = arguments?.getStringArrayList("productImages")?.toList() ?: emptyList()
        if (!imageList.isNullOrEmpty()) {
            val adapter = ZoomImageAdapter(imageList)
            binding.viewPager.adapter = adapter
        }
    }
}
