package com.app.bharatphoton.module.purchaseSubscriptionResponse


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RenewPlanResponse(
    var code: Int?,
    var count: Int?,
    var `data`: Data?,
    var message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        var avilablePlan: List<AvilablePlan?>?,
        var currentPlan: CurrentPlan?,
        var installationDetails: List<InstallationDetail?>?
    ) : Parcelable {
        @Parcelize
        data class AvilablePlan(
            var categoryId: String?,
            var createdAt: String?,
            var currencyType: String?,
            var description: String?,
            @SerializedName("_id")
            var id: String?,
            var image: String?,
            @SerializedName("is_deleted")
            var isDeleted: Boolean?,
            var keyword: List<String?>?,
            var metaDescription: String?,
            var metaTitle: String?,
            var mode: String?,
            var pincode: List<Int?>?,
            var planType: Int?,
            var price: Int?,
            var products: List<String?>?,
            var status: Boolean?,
            var title: String?,
            var updatedAt: String?
        ) : Parcelable

        @Parcelize
        data class CurrentPlan(
            var address: String?,
            var addressId: String?,
            @SerializedName("address_type")
            var addressType: Int?,
            var categoryId: String?,
            var categoryName: String?,
            var city: String?,
            var countryCode: String?,
            var createdAt: String?,
            var currencyType: String?,
            var daysRemaining: Int?,
            var features: List<Feature?>?,
            var finalOrderStatus: Int?,
            var finalTotalAmount: Int?,
            var firstName: String?,
            @SerializedName("_id")
            var id: String?,
            var invoiceUrl: String?,
            var isFieldAvailable: Int?,
            var isUrinalSensorAvilable: Int?,
            var lastName: String?,
            var mode: String?,
            var modelNo: String?,
            var paymentMethod: String?,
            var paymentMode: Int?,
            var paymentStatus: String?,
            var phoneNo: Long?,
            var planType: Int?,
            var price: Double?,
            @SerializedName("ProductDetailsimage")
            var productDetailsimage: List<ProductDetailsimage?>?,
            var productId: String?,
            var productImage: String?,
            var productName: String?,
            var quantity: Int?,
            var resolveDate: String?,
            var sin: String?,
            var specification: List<Specification?>?,
            var staffId: String?,
            var state: String?,
            var status: Boolean?,
            var subscriptionExpiryDate: String?,
            var subscriptionStartDate: String?,
            var totalPrice: Int?,
            var updatedAt: String?,
            var userId: String?,
            var uuid: String?,
            var youtube: List<Youtube?>?,
            @SerializedName("ZipCode")
            var zipCode: Int?
        ) : Parcelable {
            @Parcelize
            data class Feature(
                var description: String?,
                @SerializedName("_id")
                var id: String?,
                var label: String?
            ) : Parcelable

            @Parcelize
            data class ProductDetailsimage(
                @SerializedName("_id")
                var id: String?,
                var url: String?
            ) : Parcelable

            @Parcelize
            data class Specification(
                var filedName: String?,
                @SerializedName("_id")
                var id: String?,
                var value: String?
            ) : Parcelable

            @Parcelize
            data class Youtube(
                var demoVideo: String?,
                @SerializedName("_id")
                var id: String?,
                var installationVideo: String?
            ) : Parcelable
        }

        @Parcelize
        data class InstallationDetail(
            var finNo: String?,
            @SerializedName("_id")
            var id: String?,
            var orderId: String?,
            var productId: String?,
            var sin: String?
        ) : Parcelable
    }
}