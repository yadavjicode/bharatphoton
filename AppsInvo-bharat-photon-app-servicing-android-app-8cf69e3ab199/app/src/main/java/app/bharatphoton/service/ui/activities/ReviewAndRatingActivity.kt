package app.bharatphoton.service.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.bharatphoton.service.databinding.ActivityReviewAndRatingBinding
import app.bharatphoton.service.ui.adapter.ReviewAndRatingAdapter
import app.bharatphoton.service.ui.adapter.ReviewModel
import app.bharatphoton.service.utils.setOnClickWithAnimation

class ReviewAndRatingActivity : AppCompatActivity() {
    private lateinit var binding : ActivityReviewAndRatingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewAndRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }

        val arrNew = ArrayList<ReviewModel>()

        arrNew.add(ReviewModel("Jenny Wilson","5 days ago"))
        arrNew.add(ReviewModel("Harry","2 week ago"))
        arrNew.add(ReviewModel("Ema ","2 week ago"))
        arrNew.add(ReviewModel("Jenny Wilson","5 days ago"))
        arrNew.add(ReviewModel("Harry","2 week ago"))
        arrNew.add(ReviewModel("Ema ","2 week ago"))

        binding.rvReviewRating.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvReviewRating.adapter = ReviewAndRatingAdapter(this, arrNew)
    }
}