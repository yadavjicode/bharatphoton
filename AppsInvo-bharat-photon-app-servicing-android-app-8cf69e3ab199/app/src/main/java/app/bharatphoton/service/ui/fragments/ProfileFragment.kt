package app.bharatphoton.service.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentProfileBinding
import app.bharatphoton.service.databinding.LayoutCustomAlertDialogBinding
import app.bharatphoton.service.ui.activities.AboutUsActivity
import app.bharatphoton.service.ui.activities.HomeActivity
import app.bharatphoton.service.ui.activities.LoginActivity
import app.bharatphoton.service.ui.activities.PrivacyPolicyActivity
import app.bharatphoton.service.ui.activities.SupportActivity
import app.bharatphoton.service.ui.activities.TermsAndConditionsActivity
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.FULL_NAME
import app.bharatphoton.service.utils.Constants.IS_ALREADY_LOGGED_IN
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.PHONE_NO
import app.bharatphoton.service.utils.Constants.PROFILE_PIC
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""

    override fun onResume() {
        super.onResume()
        profileGet()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileGet()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.apply {
            (activity as BaseActivity).animateViews(cvProfile, ivProfile, tvName, tvPhone, tvOtherLinks, cvOtherLinks,
                llAboutUs, llTermsConditions, llPrivacyPolicy, llHelpSupport, tvBtnLogout, startDelayMs = 100, staggerDelayMs = 50, durationMs = 500)
            (activity as BaseActivity).applySlideInAnimation(clProfile)
        }

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        (requireActivity() as HomeActivity).profileDetails()

        val name = sharedPreferencesHelper.getString(FULL_NAME,"NA")
        val phone = sharedPreferencesHelper.getString(PHONE_NO,"")
        val profilePic = sharedPreferencesHelper.getString(PROFILE_PIC,"")
        binding.tvName.text = name
        binding.tvPhone.text = phone
        Glide.with(this)
            .load(profilePic)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(binding.ivProfile)

        binding.tvBtnLogout.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            showLogoutAlertDialog()
        }

        binding.llAboutUs.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val intent= Intent(requireContext(), AboutUsActivity::class.java)
            startActivity(intent)
        }

        // TODO: REMOVE CODE FOR REVIEW IF NOT NEEDED
        /*binding.llReviewRatings.setOnClickWithAnimation {
            val intent= Intent(requireContext(), ReviewAndRatingActivity::class.java)
            startActivity(intent)
        }*/

        binding.llPrivacyPolicy.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val intent= Intent(requireContext(), PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        binding.llTermsConditions.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val intent= Intent(requireContext(), TermsAndConditionsActivity::class.java)
            startActivity(intent)
        }

        binding.llHelpSupport.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val intent= Intent(requireContext(), SupportActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun showLogoutAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialogBinding= LayoutCustomAlertDialogBinding.inflate(layoutInflater)
        builder.setView(alertDialogBinding.root)
        alertDialogBinding.tvAlert.text="Alert!"
        alertDialogBinding.tvMessage.text="Are You Sure to Logout?"
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        alertDialogBinding.tvPositiveBtn.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            dialog.dismiss()
            val intent= Intent(requireContext(), LoginActivity::class.java)
            sharedPreferencesHelper.clearSharedPreferences()
            sharedPreferencesHelper.saveString(IS_ALREADY_LOGGED_IN,"False")
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    "FCM token deleted successfully".mLog()
                }
            }
            startActivity(intent)
            requireActivity().finishAffinity()
        }
        alertDialogBinding.tvNegativeBtn.setOnClickWithAnimation {
            dialog.dismiss()
        }
    }

    private fun profileGet(){
        (activity as BaseActivity).profileDetailsApiCall(requireContext(), token, sharedPreferencesHelper)
        val fullName = sharedPreferencesHelper.getString(FULL_NAME,"NA")
        val phone = sharedPreferencesHelper.getString(PHONE_NO,"")
        val profilePic = sharedPreferencesHelper.getString(PROFILE_PIC,"")
        binding.tvName.text = fullName
        binding.tvPhone.text = phone
        Glide.with(this)
            .load(profilePic)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(binding.ivProfile)
    }
}