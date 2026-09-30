package app.bharatphoton.service.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityOtpactivityBinding
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.setOnClickWithAnimation

class OTPActivity : BaseActivity() {
    private lateinit var binding : ActivityOtpactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editTexts = listOf(binding.et1, binding.et2, binding.et3, binding.et4, binding.et5, binding.et6)
        val views = listOf(binding.v1, binding.v2, binding.v3, binding.v4, binding.v5, binding.v6)

        for (i in editTexts.indices) {
            val currentEditText = editTexts[i]
            val nextEditText = if (i < editTexts.lastIndex) editTexts[i + 1] else null
            val previousEditText = if (i > 0) editTexts[i - 1] else null
            currentEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    if (editable.length == 1 && nextEditText != null) {
                        nextEditText.requestFocus()
                    }
                    if (editable.isNotEmpty()) {
                        views[i].setBackgroundColor(ContextCompat.getColor(this@OTPActivity, R.color.photon_orange))
                    }
                    else {
                        views[i].setBackgroundColor(ContextCompat.getColor(this@OTPActivity, R.color.black))
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            currentEditText.setOnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentEditText.text.isEmpty() && previousEditText != null) {
                    previousEditText.text = null
                    previousEditText.requestFocus()
                    true
                } else {
                    false
                }
            }
        }

        resendOtp()
        binding.tvResendOtp.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            resendOtp()
            showAnimatedCustomToast("OTP has been resent!")
        }

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }

        binding.tvBtnVerify.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            val otp = binding.et1.text.toString() + binding.et2.text.toString() +
                    binding.et3.text.toString() + binding.et4.text.toString() +
                    binding.et5.text.toString() + binding.et6.text.toString()
//            otpForApi = otp

            if (binding.et1.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et2.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et3.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et4.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et5.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else if (binding.et6.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter valid otp!")
            }
            else {
                val intent = Intent(this@OTPActivity, SetPasswordActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun resendOtp(){
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvResendOtp.isClickable=false
                binding.tvResendOtp.setTextColor(ContextCompat.getColor(this@OTPActivity, R.color.photon_dark_gray))
                binding.tvTimer.text = "00:"+"%02d".format(millisUntilFinished / 1000)
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                binding.tvResendOtp.isClickable=true
                binding.tvResendOtp.setTextColor(ContextCompat.getColor(this@OTPActivity, R.color.photon_orange))
            }
        }.start()
    }
}