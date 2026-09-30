package com.app.bharatphoton.module.reportResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MonthlyReportResponse(
    var code: Int?,
    var count: Count?,
    var `data`: Data?,
    var message: String?
) : Parcelable {
    @Parcelize
    class Count : Parcelable

    @Parcelize
    data class Data(
        var duration: Duration?,
        var installationData: List<InstallationData?>?,
        var reportingdate: String?,
        var userDetails: UserDetails?,
        var userReports: UserReports?
    ) : Parcelable {
        @Parcelize
        data class Duration(
            var from: String?,
            var to: String?
        ) : Parcelable

        @Parcelize
        data class InstallationData(
            var batteryVoltage: Double?,
            var categoryId: String?,
            var complaintId: String?,
            var complaintStatus: Int?,
            var costSaved: Int?,
            var createdAt: String?,
            var customerId: String?,
            var customerName: String?,
            var cutOffDelay: Int?,
            var finNo: String?,
            var flushTime: String?,
            @SerializedName("_id")
            var id: String?,
            var image: List<Image?>?,
            var installationStatus: Int?,
            var installationType: Int?,
            @SerializedName("is_deleted")
            var isDeleted: Boolean?,
            var isFieldAvailable: Int?,
            var isOtpVerfied: Boolean?,
            var isUrinalSensorAvilable: Int?,
            var modelNo: String?,
            var montlyInstallationUrl: String?,
            var offDelay: Int?,
            var orderId: String?,
            var otp: String?,
            var preflush: String?,
            var productId: String?,
            var productImage: List<ProductImage?>?,
            var productName: String?,
            var range: Int?,
            var remark: String?,
            var resolveDate: String?,
            var sin: String?,
            var staffId: String?,
            var status: Boolean?,
            var subscriptionExpiryDate: String?,
            var subscriptionId: String?,
            var subscriptionStartDate: String?,
            var totalPayment: Int?,
            var updatedAt: String?,
            var updatedByAdmin: Int?,
            var userCount: Int?,
            var warrentyEndDate: String?,
            var warrentyMonth: String?,
            var warrentyStartDate: String?,
            var waterSaved: Int?
        ) : Parcelable {
            @Parcelize
            data class Image(
                @SerializedName("_id")
                var id: String?,
                var url: String?
            ) : Parcelable

            @Parcelize
            data class ProductImage(
                @SerializedName("_id")
                var id: String?,
                var url: String?
            ) : Parcelable
        }

        @Parcelize
        data class UserDetails(
            var name: String?
        ) : Parcelable

        @Parcelize
        data class UserReports(
            var totalAverageWaterSaved: Int?,
            var totalCostSaved: TotalCostSaved?,
            var totalUserCount: TotalUserCount?,
            var totalWaterSaved: TotalWaterSaved?
        ) : Parcelable {
            @Parcelize
            data class TotalCostSaved(
                var sensor: Int?,
                var urinal: Int?
            ) : Parcelable

            @Parcelize
            data class TotalUserCount(
                var sensor: Int?,
                var urinal: Int?
            ) : Parcelable

            @Parcelize
            data class TotalWaterSaved(
                var sensor: Int?,
                var urinal: Int?
            ) : Parcelable
        }
    }
}