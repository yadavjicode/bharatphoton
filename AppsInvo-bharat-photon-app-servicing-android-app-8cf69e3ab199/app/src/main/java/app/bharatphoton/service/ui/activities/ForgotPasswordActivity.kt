package app.bharatphoton.service.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityForgotPasswordBinding
import app.bharatphoton.service.utils.setOnClickWithAnimation

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding : ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }

        binding.tvBtnContinue.setOnClickWithAnimation {
            if (binding.etEmail.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Email/Phone is required!")
            }
            else {
                val intent = Intent(this@ForgotPasswordActivity, OTPActivity::class.java)
                startActivity(intent)
            }
        }
    }
}