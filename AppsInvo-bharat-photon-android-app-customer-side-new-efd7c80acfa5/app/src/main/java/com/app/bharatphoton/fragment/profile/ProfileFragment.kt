package com.app.bharatphoton.fragment.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.R.drawable.about
import com.app.bharatphoton.R.drawable.account
import com.app.bharatphoton.R.drawable.address
import com.app.bharatphoton.R.drawable.complaint
import com.app.bharatphoton.R.drawable.delete
import com.app.bharatphoton.R.drawable.installed
import com.app.bharatphoton.R.drawable.issueclosed
import com.app.bharatphoton.R.drawable.orders
import com.app.bharatphoton.R.drawable.password
import com.app.bharatphoton.R.drawable.placeholder
import com.app.bharatphoton.R.drawable.pp
import com.app.bharatphoton.R.drawable.refer
import com.app.bharatphoton.R.drawable.reports
import com.app.bharatphoton.R.drawable.subscribe
import com.app.bharatphoton.R.drawable.t_c
import com.app.bharatphoton.R.drawable.wishlist_
import com.app.bharatphoton.adapter.ProfileOtherFullLinksAdapter
import com.app.bharatphoton.adapter.ProfileUseFullLinksAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Constant
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedViewModule
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopUpDialog
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentProfileBinding
import com.app.bharatphoton.fragment.auth.ChangePasswordBottomSheetFragment
import com.app.bharatphoton.module.Module
import com.app.bharatphoton.module.profileResponse.PersonalProfileResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.retrofit.NetworkResult.Loading
import com.app.bharatphoton.viewModule.ProfileViewModule
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {


    private lateinit var googleSignInClient: GoogleSignInClient
    private var optionLinkList = mutableListOf<Module>()
    private var otherLinkList = mutableListOf<Module>()
    private var type = ""
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val profileOtherFullLinksAdapter by lazy {
        val fragmentMap = mapOf(
            0 to R.id.customerSupportFragment, 1 to R.id.aboutUsFragment, 2 to R.id.termsAndConditionsFragment, 3 to R.id.privacyPolicyFragment, 4 to R.id.deleteAccountFragment
        )

        ProfileOtherFullLinksAdapter { type ->
            fragmentMap[type]?.let { navigateToScreen(it, bundle = null, navOptions = navOptions) }
        }
    }

    private val profileUseFullLinksAdapter by lazy {
        val fragmentMap = mapOf(
            0 to R.id.myOrderFragment,
            1 to R.id.myAddressFragment,
            2 to R.id.mySubscriptionFragment,
            3 to R.id.myProductFragment,
            4 to R.id.referFriendFragment,
            5 to R.id.reportsFragment,
            6 to R.id.paymentHistoryFragment,
            7 to R.id.renewWarrantyFragment,
            8 to R.id.renewSubscriptionPlanFragment,
            9 to R.id.wishListFragment
        )

        ProfileUseFullLinksAdapter { position ->
            fragmentMap[position]?.let { navigateToScreen(it, bundle = null, navOptions = navOptions) } ?: if (position == 10) {
                ChangePasswordBottomSheetFragment().show(childFragmentManager, "changePasswordFragment")
            } else {
                //
            }
        }
    }


    private val shareViewModule by lazy {
        ViewModelProvider(this)[SharedViewModule::class.java]
    }

    private val viewModule by viewModels<ProfileViewModule>()

    override fun onDestroyView() {
        super.onDestroyView()
        optionLinkList.clear()
        otherLinkList.clear()
    }

    override fun onResume() {
        super.onResume()
        setData()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        handleOnPressBackButton { navigateBack() }
        onClick()
        setDataInAdapter()
        otherLinkOptionList()
        apiProfileRequest()
        apiProfileResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

    private fun apiProfileRequest() {
        viewModule.userProfile()
    }

    @SuppressLint("SetTextI18n")
    private fun apiProfileResponse() {
        viewModule.userProfile.observe(viewLifecycleOwner) {
            if (it is Loading) {
//                showLoader()
            }
            if (it is NetworkResult.Success) {
                try {
                    profileResponse = Gson().fromJson(it.data, PersonalProfileResponse::class.java)
                    if (profileResponse?.code == 1) {
                        setData()
                    } else {
                        requireActivity().toast(profileResponse?.message ?: "")
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    private fun initData() {
        with(binding) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            RecyclerAnimationHelper.animation(context = requireContext(), views = root)
            val deviceId = sharedPrefManager.getString("deviceId")
            apiCartCountRequest(deviceId = deviceId)
            apiCartCountResponse { count ->
                binding.topBar.cartItemCount.text = count.toString()
            }
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            type = sharedPrefManager.getString(key = "type", "individual")
            handleOnPressBackButton { navigateBack() }
            Glide.with(requireContext()).load(sharedPrefManager.getString(Constant.SharedPref.PROFILE_PIC)).placeholder(placeholder).into(profile)
//            name.text = sharedPrefManager.getString(Constant.SharedPref.PROFILE_NAME)
            val contactNo = sharedPrefManager.getString("phoneNumber", "0")
            if (contactNo == "0" || contactNo == "null") {
                phone.text = "N/A"
            } else {
                phone.text = contactNo
            }
        }
    }

    private fun onClick() {
        with(binding) {
            when (type) {
                "individual" -> {
                    editProfile.setOnAnimatedClickListener { navigateToScreen(id = R.id.editProfileFragment, bundle = null, navOptions = navOptions) }
                }

                "business" -> {
                    editProfile.setOnAnimatedClickListener { navigateToScreen(id = R.id.editProfileBusinessFragment, bundle = null, navOptions = navOptions) }
                }
            }
            topBar.apply {
                call.setOnAnimatedClickListener { openDialer() }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
                cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            }
            logout.setOnAnimatedClickListener {
                showPopUpDialog(context = requireContext(), message = "Are You Sure You Want To Log Out!", onOk = {
                    sharedPrefManager.removeAll()
                    shareViewModule._profilePicUrl.postValue(null)
                    signOut()
                    val navOption = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                    navigateToScreen(R.id.loginFragment, null, navOption)
                })
            }
        }
    }

    private fun otherLinkOptionList() {
        otherLinkList.apply {
            add(Module(1, getString(R.string.customer_support), complaint))
            add(Module(1, getString(R.string.about_us), about))
            add(Module(1, getString(R.string.terms_and_conditions), t_c))
            add(Module(1, getString(R.string.privacy_policy), pp))
            add(Module(1, getString(R.string.delete_account), delete))
        }
        profileOtherFullLinksAdapter.submitList(otherLinkList)
    }

    private fun setData() {
        with(binding) {
            optionLinkList.clear()
            profileResponse?.data?.let { data ->
                Glide.with(requireContext()).load(data.profileImage).placeholder(placeholder).into(profile)
                useFullOptionList()
                name.text = if (data.userType == 1) {
                    "${data.firstName ?: "N/A"} ${data.lastName ?: ""}".trim()
                } else {
                    data.companyName ?: "N/A"
                }

                phone.text = if (data.userType == 1) {
                    data.phoneNo ?: "N/A"
                } else {
                    data.businessPhoneNo ?: "N/A"
                }
            }
        }
    }


    private fun setDataInAdapter() {
        with(binding) {
            profileUserLinksRv.apply {
                adapter = profileUseFullLinksAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            profileOtherLinksRv.apply {
                adapter = profileOtherFullLinksAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            sharedPrefManager.removeAll()
        }
    }

    private fun useFullOptionList() {
        optionLinkList.clear()
        optionLinkList.apply {
            add(Module(1, "My Orders", orders))
            add(Module(2, "My Addresses", address))
            add(Module(2, "My Subscriptions", subscribe))
            add(Module(2, "My Products", installed))
            add(Module(2, "Refer to a friend", refer))
            add(Module(2, "Reports", reports))
            add(Module(2, "Payment History", account))
            add(Module(2, "Renew Product Warranty", issueclosed))
            add(Module(2, "Renew Subscription Plan", subscribe))
            add(Module(2, "Wishlist", wishlist_))
            if (profileResponse?.data?.userType == 2) {
                add(Module(2, "Change Password", password))
            }
        }
        profileUseFullLinksAdapter.submitList(optionLinkList)
    }

    companion object {
        private var profileResponse: PersonalProfileResponse? = null
    }
}