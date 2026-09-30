package com.app.bharatphoton.module.reportResponse


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportsResponse(
    val code: Int?, val count: Count?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        val costReport: CostReport?, val userCountReport: UserCountReport?, val waterSavedReports: WaterSavedReports?
    ) : Parcelable {
        @Parcelize
        data class CostReport(
            val monthlyCostSaved: List<MonthlyCostSaved?>?, val totalCostSavedForYear: Int?
        ) : Parcelable {
            @Parcelize
            data class MonthlyCostSaved(
                val month: String?, val totalCostSaved: Int?
            ) : Parcelable
        }

        @Parcelize
        data class UserCountReport(
            val monthlyUserCount: List<MonthlyUserCount?>?, val totalUserCountForYear: Int?
        ) : Parcelable {
            @Parcelize
            data class MonthlyUserCount(
                val month: String?, val totalUserCount: Int?
            ) : Parcelable
        }

        @Parcelize
        data class WaterSavedReports(
            val monthlyWaterSaved: List<MonthlyWaterSaved?>?, val totalWaterSavedForYear: Int?
        ) : Parcelable {
            @Parcelize
            data class MonthlyWaterSaved(
                val month: String?, val totalWaterSaved: Int?
            ) : Parcelable
        }
    }
}