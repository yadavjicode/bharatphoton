package app.bharatphoton.service.api_model_classes

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ComplaintDetailsResponse(
    var code: Int?,
    var count: Int?,
    var `data`: ComplaintDetailsResponseData?,
    var message: String?
)
@Parcelize
data class ComplaintDetailsResponseData(
    var complaintDetails: ComplaintDetailsResponseComplaintDetails?,
    var userDetails: ComplaintDetailsResponseUserDetails?
) : Parcelable
@Parcelize
data class ComplaintDetailsResponseComplaintDetails(
    var _id: String?,
    var categoryId: String?,
    var categoryName: String?,
    var complaintReason: String?,
    var complaintStatus: Int?,
    var createdAt: String?,
    var description: String?,
    var image: List<ComplaintDetailsResponseImage?>?,
    var remarkimage: List<ComplaintDetailsResponseImage?>?,
    var is_deleted: Boolean?,
    var productDescription: String?,
    var productFinNo: String?,
    var productId: String?,
    var productImage: List<ComplaintDetailsResponseProductImage?>?,
    var productModelNo: String?,
    var productName: String?,
    var resolveDate: String?,
    var sin: String?,
    var staffId: String?,
    var status: Boolean?,
    var updatedAt: String?,
    var userId: String?,
    var uuid: String?,
    var ZipCode: String?,
    var address: String?,
    var city: String?,
    var state: String?,
    var addressId: String?,
    var address_type: String?,
    var customerName: String?,
    var isFieldAvailable: String?,
    var isUrinalSensorAvailable: String?,
    var remark: String?
): Parcelable
@Parcelize
data class ComplaintDetailsResponseImage(
    var _id: String?,
    var url: String?
): Parcelable
@Parcelize
data class ComplaintDetailsResponseProductImage(
    var _id: String?,
    var url: String?
):Parcelable
@Parcelize
data class ComplaintDetailsResponseUserDetails(
    var _id: String?,
    var address: String?,
    var appleId: String?,
    var city: String?,
    var contactNo: String?,
//    @SerializedName("country_code")
    val countryCode: String?,
    val country_code: String?,
    var createdAt: String?,
    var deviceId: String?,
    var deviceToken: String?,
    var deviceType: String?,
    var email: String?,
    var facebookId: String?,
    var firstName: String?,
    var googleId: String?,
    var isActive: Boolean?,
    var isDeleted: Boolean?,
    var isPhoneVerified: Boolean?,
    var isemailVerified: Boolean?,
    var lastName: String?,
    var otp: String?,
    var phoneNo: String?,
    var profileImage: String?,
    var saveMoney: String?,
    var saveWater: String?,
    var secondayPhoneNo: String?,
    var socialType: String?,
    var state: String?,
    var status: Boolean?,
    var tempEmail: String?,
    var tempPhoneNo: String?,
    var tempcountry_code: String?,
    var updatedAt: String?,
    var userType: String?,
    var zipCode: String?,

    var businessEmail: String?,
    var businessPhoneNo: String?,
    var companyName: String?,


):Parcelable