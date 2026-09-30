@file:Suppress("DEPRECATION")

package com.app.bharatphoton.fragment.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Constant
import com.app.bharatphoton.common.SharedViewModule
import com.app.bharatphoton.common.Utils.hideKeyboard
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentLoginBinding
import com.app.bharatphoton.module.authResponse.LoginResponse
import com.app.bharatphoton.module.authResponse.SocialLoginResponse
import com.app.bharatphoton.retrofit.Constants
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var sharedViewModule: SharedViewModule
    private var isEmailVisible = true
    private var phoneOrEmail = "email"
    private var socialType = ""
    private var type = "individual"
    private val authViewModule by viewModels<AuthViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModule.login.postValue(null)
        authViewModule.forgetPassword.postValue(null)
        binding.EditTextPhoneNumber.setText("")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        sharedViewModule = ViewModelProvider(requireActivity())[SharedViewModule::class.java]

        initData()
        onClick()
        apiLoginResponse()
        apiSocialLoginResponse()
        faceBookLogin()
        phoneNumberTetWatcher()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater)

    private fun apiLoginRequest() {
        with(binding) {
            when (type) {
                "individual" -> {
                    val request = JsonObject().apply {
                        addProperty("country_code", ccp.selectedCountryCodeWithPlus)
                        addProperty("phoneNo", EditTextPhoneNumber.text.toString())

                    }
                    authViewModule.login(request)
                }

                "business" -> {
                    val request = JsonObject().apply {
                        if (EditTextPhoneNumber.text.toString().trim().isNotBlank()) {
                            addProperty("country_code", ccp.selectedCountryCodeWithPlus)
                            addProperty("businessPhoneNo", EditTextPhoneNumber.text.toString())
                        } else {
                            addProperty("businessEmail", EditTextEmailID.text.toString())
                            addProperty("password", EditTextPassword.text.toString())
                        }
                    }
                    authViewModule.login(request)
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun apiLoginResponse() {
        authViewModule.login.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.loginBtn.btnView.showLoading()
                }

                is NetworkResult.Success -> {
                    with(binding) {
                        loginBtn.btnView.hideLoading()
                        try {
                            loginResponse = Gson().fromJson(it.data, LoginResponse::class.java)
                            if (type != "individual" && phoneOrEmail == "email") {
                                loginResponse?.data?.authToken?.let { it1 -> sharedPrefManager.saveString(key = Constants.SharedPref.AUTH_TOKEN, value = it1) }
                            }
                            if (loginResponse?.code == 1) {
                                if (loginResponse?.data?.result?.userType == 2) {
                                    sharedViewModule._profilePicUrl.value = loginResponse?.data?.result?.profileImage ?: ""
                                    navigateToScreen(R.id.homeFragment, bundle = null, navOptions = navOptions)
                                } else {

                                    val bundle = Bundle().apply {
                                        putString("email", EditTextEmailID.text.toString())
                                        putString("phone", EditTextPhoneNumber.text.toString())
                                        putString("emailOrPhone", "phone")
                                        putString("notificationType", "login")
                                        putString("authToken", loginResponse?.data?.authToken)
                                        putString("userType", type)
                                    }
                                    sharedViewModule._profilePicUrl.value = loginResponse?.data?.result?.profileImage ?: ""

                                    navigateToScreen(id = R.id.OTPFragment, bundle = bundle, navOptions = navOptions)
                                }

                            } else if (loginResponse?.code == 2) {
                                val bundle = Bundle().apply {
                                    putString("email", EditTextEmailID.text.toString())
                                    putString("phone", EditTextPhoneNumber.text.toString())
                                    putString("emailOrPhone", "email")
                                    putString("notificationType", "login")
                                    putString("authToken", loginResponse?.data?.authToken)
                                    putString("userType", type)

                                }
                                navigateToScreen(id = R.id.OTPFragment, bundle = bundle, navOptions = navOptions)
                                sharedViewModule._profilePicUrl.value = loginResponse?.data?.result?.profileImage ?: ""

                            } else {
                                requireActivity().toast(loginResponse?.message.toString())
                            }

                        } catch (e: Exception) {
                            e.message.mlog()
                        }

                    }

                }

                is NetworkResult.Error -> {
                    binding.loginBtn.btnView.hideLoading()
                    try {
                        val message = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(message)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun apiSocialLoginRequest(email: String, socialId: String, profilePic: String, firstName: String, lastName: String) {
        val request = JsonObject().apply {
            addProperty("email", email)
            if (type == "individual") {
                addProperty("userType", "1")
            } else {
                addProperty("userType", "2")
            }
            addProperty("socialType", socialType)
            if (socialType == "G") {
                addProperty("googleId", socialId)
            } else {
                addProperty("facebookId", socialId)
            }
            addProperty("deviceType", "android")
            addProperty("profileImage", profilePic)
            addProperty("firstName", firstName)
            addProperty("lastName", lastName)
        }
        authViewModule.socialLogin(request)
    }

    private fun apiSocialLoginResponse() {
        authViewModule.socialLogin.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        socialLoginResponse = Gson().fromJson(it.data, SocialLoginResponse::class.java)
                        if (socialLoginResponse?.code == 1) {
                            socialLoginResponse?.data?.authToken?.let { it1 -> sharedPrefManager.saveString(key = Constants.SharedPref.AUTH_TOKEN, value = it1) }
                            sharedPrefManager.saveString("phoneNumber", socialLoginResponse?.data?.result?.contactNo.toString())
                            sharedPrefManager.saveString(Constant.SharedPref.PROFILE_PIC, socialLoginResponse?.data?.result?.profileImage ?: "")
                            sharedViewModule._profilePicUrl.value = socialLoginResponse?.data?.result?.profileImage ?: ""
                            navigateToScreen(id = R.id.homeFragment, bundle = null, navOptions = navOptions)
                        } else {
                            requireActivity().toast(socialLoginResponse?.message ?: "")
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun faceBookLogin() {
        binding.faceBookLogin.setOnAnimatedClickListener {

            socialType = "F"
            LoginManager.getInstance().logInWithReadPermissions(this, permissions = listOf("email", "public_profile"))

            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    requireActivity().toast(getString(R.string.login_cancelled))
                    sinOutFromFacebook()
                }

                override fun onError(error: FacebookException) {
                    requireActivity().toast(getString(R.string.something_went_wrong))
                }

                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    useLoginInformation(accessToken)
                }
            })
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                updateUI(user)
            } else {
                requireActivity().toast(getString(R.string.authentication_failed))
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(idToken = account.idToken)
        } catch (e: ApiException) {
            requireActivity().toast(getString(R.string.login_cancelled))
        }
    }

    private fun initData() {
        with(binding) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            firebaseAuth = FirebaseAuth.getInstance()

            ccp.setOnCountryChangeListener { TextViewCountryCode.text = ccp.selectedCountryCodeWithPlus }
            TextViewCountryCode.text = ccp.selectedCountryCodeWithPlus
            sharedPrefManager.saveString(key = "type", value = type)
            callbackManager = CallbackManager.Factory.create()

        }
    }

    @SuppressLint("SetTextI18n")
    private fun onClick() = with(binding) {
        loginBtn.btnView.setOnAnimatedClickListener { if (validate()) apiLoginRequest() }
        guestUser.setOnAnimatedClickListener { navigateToScreen(R.id.homeFragment, null, navOptions) }
        googleLogin.setOnAnimatedClickListener {
            socialType = "G"
            signIn()
        }
        fun updateLoginType(isIndividual: Boolean) = with(binding) {
            type = if (isIndividual) "individual" else "business"
            sharedPrefManager.saveString("type", type)
            EditTextPhoneNumber.setText("")
            loginDescription.text = getString(
                if (isIndividual) R.string.please_enter_your_mobile_number_to_proceed_further
                else R.string.please_login_to_your_account
            )
            ImageViewIndividualLoginBg.visibility = if (isIndividual) View.VISIBLE else View.GONE
            ImageViewIndividualLoginCheck.visibility = if (isIndividual) View.VISIBLE else View.GONE
            ImageViewBusinessLoginBg.visibility = if (!isIndividual) View.VISIBLE else View.GONE
            ImageViewBusinessLoginCheck.visibility = if (!isIndividual) View.VISIBLE else View.GONE
            LayoutLoginEmail.visibility = if (!isIndividual) View.VISIBLE else View.GONE
            LayoutLoginPhone.visibility = if (isIndividual) View.VISIBLE else View.GONE
            googleLogin.visibility = if (isIndividual) View.VISIBLE else View.GONE
            faceBookLogin.visibility = if (isIndividual) View.VISIBLE else View.GONE
            phone.visibility = if (!isIndividual) View.VISIBLE else View.GONE
        }
        updateLoginType(type == "individual")
        CardViewIndividualLogin.setOnAnimatedClickListener { updateLoginType(true) }
        CardViewBusinessLogin.setOnAnimatedClickListener { updateLoginType(false) }
        phone.setOnAnimatedClickListener {
            isEmailVisible = !isEmailVisible
            LayoutLoginEmail.visibility = if (isEmailVisible) View.VISIBLE else View.GONE
            LayoutLoginPhone.visibility = if (!isEmailVisible) View.VISIBLE else View.GONE
            loginDescription.text = getString(
                if (isEmailVisible) R.string.please_login_to_your_account
                else R.string.please_enter_your_mobile_number_to_proceed_further
            )
            phone.setImageResource(if (isEmailVisible) R.drawable.phone else R.drawable.email_btn)
            phoneOrEmail = if (isEmailVisible) "email" else "phone"
        }
        ForgetPassword.setOnAnimatedClickListener { navigateToScreen(R.id.forgetPasswordFragment, null, navOptions) }
        createAccount.setOnAnimatedClickListener {
            navigateToScreen(
                if (type == "individual") R.id.signUpIndividualFragment else R.id.signUpBusinessFragment, null, navOptions
            )
        }
    }

    private fun phoneNumberTetWatcher() {
        binding.EditTextPhoneNumber.addTextChangedListener {
            if (binding.EditTextPhoneNumber.text.length == 10) {
                hideKeyboard(requireContext(), view = requireView())
            }
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun sinOutFromFacebook() {
        LoginManager.getInstance().logOut()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            user.photoUrl?.let { photoUrl ->
                val profilePicUrl = photoUrl.toString()
                val displayName = user.displayName
                val email = user.email
                val googleId = user.uid

                val nameParts = displayName?.split(" ")
                val firstName = nameParts?.getOrNull(0) ?: ""
                val lastName = nameParts?.getOrNull(1) ?: ""
                apiSocialLoginRequest(email = email ?: "", socialId = googleId, profilePic = profilePicUrl, firstName = firstName, lastName = lastName)
                sharedPrefManager.saveString("profileName", displayName ?: "")
            }
        }
    }

    private fun useLoginInformation(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { jsonObject, response ->
            try {
                val name = jsonObject?.getString("name")
                val email = jsonObject?.optString("email")
                val facebookId = jsonObject?.optString("id")
                val profilePic = "https://graph.facebook.com/$facebookId/picture?type=large"

                apiSocialLoginRequest(email = email ?: "", facebookId ?: "", profilePic = profilePic, firstName = "", lastName = "")
                sharedPrefManager.saveString("profileName", jsonObject?.optString("name") ?: "")
//                sharedPrefManager.saveString("profilePic", profilePic)

            } catch (e: JSONException) {
                e.printStackTrace()
                e.message.mlog()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id, name, email, picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun validate(): Boolean {
        with(binding) {
            when (type) {
                "individual" -> {
                    if (EditTextPhoneNumber.text.toString().isBlank()) {
                        EditTextPhoneNumber.apply {
                            error = getString(R.string.please_enter_phone_number)
                            requestFocus()
                        }
                        return false
                    } else if (EditTextPhoneNumber.text.length < 10) {
                        EditTextPhoneNumber.apply {
                            error = getString(R.string.please_enter_valid_phone_number)
                            requestFocus()
                        }
                        return false
                    }
                }

                "business" -> {
                    when (phoneOrEmail) {
                        "phone" -> {
                            if (EditTextPhoneNumber.text.toString().isBlank()) {
                                EditTextPhoneNumber.apply {
                                    error = getString(R.string.please_enter_phone_number)
                                    requestFocus()
                                }
                            }
                        }

                        "email" -> {
                            if (EditTextEmailID.text.toString().trim().isBlank()) {
                                EditTextEmailID.apply {
                                    error = getString(R.string.please_enter_email)
                                    requestFocus()
                                }
                                return false
                            } else if (!EditTextEmailID.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                                EditTextEmailID.apply {
                                    error = getString(R.string.please_enter_valid_email)
                                    requestFocus()
                                }
                                return false
                            } else if (EditTextPassword.text.toString().isBlank()) {
                                EditTextPassword.apply {
                                    error = context.getString(R.string.please_enter_password)
                                    requestFocus()
                                }
                                return false
                            }
                        }
                    }
                }
                else -> {
                    //
                }
            }
        }
        return true
    }

    companion object {
        private var loginResponse: LoginResponse? = null
        private var socialLoginResponse: SocialLoginResponse? = null
        const val RC_SIGN_IN = 9001
    }
}