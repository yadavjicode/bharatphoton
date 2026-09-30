package com.app.bharatphoton.module.productResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InstalledProductDetailsResponse(
    val code: Int?, val count: Int?, val `data`: Data?, val message: String?
) : Parcelable {
    @Parcelize
    data class Data(
        val order: Order?
    ) : Parcelable {
        @Parcelize
        data class Order(
            val address: String?,
            val addressId: String?,
            val cartId: String?,
            val city: String?,
            val countryCode: String?,
            val coupons: List<Coupon?>?,
            val currency: String?,
            val discountedAmount: Int?,
            val finalOrderStatus: Int?,
            val finalTotalAmount: Int?,
            val firstName: String?,
            @SerializedName("_id") val id: String?,
            val installationInfo: List<InstallationInfo?>?,
            val invoiceUrl: String?,
            val lastName: String?,
            val paymentStatus: String?,
            val phoneNo: Long?,
            val products: Products?,
            val state: String?,
            val subTotal: Int?,
            val taxAmount: Int?,
            val uuid: String?,
            @SerializedName("ZipCode") val zipCode: Int?
        ) : Parcelable {
            @Parcelize
            data class InstallationInfo(
                val batteryVoltage: Int?,
                val complaintId: String?,
                val complaintStatus: Int?,
                val costSaved: Int?,
                val createdAt: String?,
                val customerId: String?,
                val customerName: String?,
                val finNo: String?,
                val flushTime: Int?,
                @SerializedName("_id") val id: String?,
                val image: List<Image?>?,
                val installationStatus: Int?,
                val installationType: Int?,
                @SerializedName("is_deleted") val isDeleted: Boolean?,
                val isOtpVerfied: Boolean?,
                val modelNo: String?,
                val orderId: String?,
                val otp: String?,
                val preflush: Int?,
                val productId: String?,
                val montlyInstallationUrl: String?,
                val productImage: List<ProductImage?>?,
                val productName: String?,
                val range: Int?,
                val remark: String?,
                val resolveDate: String?,
                val sin: String?,
                val staffId: String?,
                val status: Boolean?,
                val subscriptionId: String?,
                val totalPayment: Int?,
                val updatedAt: String?,
                val updatedByAdmin: Int?,
                val userCount: Int?,
                @SerializedName("__v") val v: Int?,
                val warrentyEndDate: String?,
                val warrentyMonth: String?,
                val warrentyStartDate: String?,
                val waterSaved: Int?
            ) : Parcelable {
                @Parcelize
                data class Image(
                    @SerializedName("_id") val id: String?, val url: String?
                ) : Parcelable

                @Parcelize
                data class ProductImage(
                    @SerializedName("_id") val id: String?, val url: String?
                ) : Parcelable
            }

            @Parcelize
            data class Coupon(
                val couponId: String?, val couponType: Int?, @SerializedName("_id") val id: String?, val title: String?
            ) : Parcelable

            @Parcelize
            data class Products(
                val assignDate: String?,
                val cancelledDate: String?,
                val categoryId: String?,
                val categoryName: String?,
                val costSaved: Int?,
                val deliveredProductDate: String?,
                val description: String?,
                val dispatchedProductDate: String?,
                @SerializedName("_id") val id: String?,
                val image: String?,
                val installationDetails: List<InstallationDetail?>?,
                val installationPincode: Int?,
                val installationPrice: Int?,
                val isFieldAvailable: Int?,
                val isUrinalSensorAvilable: Int?,
                val modelNo: String?,
                val orderStatus: Int?,
                val pendingProductDate: String?,
                val price: Int?,
                val processedProductDate: String?,
                val productId: String?,
                @SerializedName("product_name") val productName: String?,
                val quantity: Int?,
                val remainingWarrantyDays: Int?,
                val resolveDate: String?,
                val staffId: String?,
                val totalPrice: Int?,
                val warrentyEndDate: String?,
                val warrentyMonth: String?,
                val warrentyStartDate: String?,
                val waterSaved: Int?
            ) : Parcelable {
                @Parcelize
                data class InstallationDetail(
                    val finNo: String?, @SerializedName("_id") val id: String?, val installationStatus: Int?
                ) : Parcelable
            }
        }
    }
}