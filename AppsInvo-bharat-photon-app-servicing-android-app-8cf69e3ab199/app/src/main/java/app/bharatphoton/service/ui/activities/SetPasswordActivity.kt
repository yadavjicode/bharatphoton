package app.bharatphoton.service.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivitySetPasswordBinding
import app.bharatphoton.service.utils.setOnClickWithAnimation

class SetPasswordActivity : BaseActivity() {
    private lateinit var binding : ActivitySetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var f =true
        binding.ivEyeIcon.setOnClickWithAnimation {
            if(f)
            {
                binding.etPassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
                binding.ivEyeIcon.setImageResource(R.drawable.eye_close)
                f=false
            }
            else
            {
                binding.etPassword.transformationMethod= PasswordTransformationMethod.getInstance()
                binding.ivEyeIcon.setImageResource(R.drawable.eye_open)
                f=true
            }
        }
        var g =true
        binding.ivEyeIcon2.setOnClickWithAnimation {
            if(g)
            {
                binding.etConfirmPassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
                binding.ivEyeIcon2.setImageResource(R.drawable.eye_close)
                g=false
            }
            else
            {
                binding.etConfirmPassword.transformationMethod= PasswordTransformationMethod.getInstance()
                binding.ivEyeIcon2.setImageResource(R.drawable.eye_open)
                g=true
            }
        }

        binding.tvBtnSave.setOnClickWithAnimation {
            if (binding.etPassword.text.toString().isEmpty() && binding.etConfirmPassword.text.toString().isEmpty())
            {
                showAnimatedCustomToast("Please enter password!")
            }
            else if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString())
            {
                showAnimatedCustomToast("Passwords are not same!")
            }
            else {
                val intent = Intent(this@SetPasswordActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }
    }
}