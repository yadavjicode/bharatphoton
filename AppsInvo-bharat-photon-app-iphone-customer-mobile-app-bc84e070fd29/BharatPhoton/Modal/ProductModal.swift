//
//  ProductModal.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 18/09/24.
//

import Foundation
import UIKit

// MARK: ProductListModal ==>
struct ProductListModal : Codable {
    let code : Int?
    let message : String?
    let data : ProductData?
    let count : Int?
}
struct ProductData : Codable {
    let subcategoryListing : [Subcategory]?
    let description : String?
    let categoryName : String?

}
struct Subcategory : Codable {
    let _id : String?
    let subcategoryName : String?
    let subcategoryDescription : String?
    let products : [Products]?
}
struct Products : Codable {
    let _id : String?
    let productName : String?
    let price : Int?
    let priceToDisplay : Int?
    let categoryId : String?
    let subcategoryId : String?
    let image : [Image]?
    let currencyType : String?
    let is_wishlist : Int?
    let rating: Int?
    let discountPercentage : Int?
    let quantity : Int?
    let cartStatus : Int?
    let categoryName : String?
    let createdAt : String?
}
struct Image : Codable {
    let url : String?
    let _id : String?
}
// MARK: ViewAllProductModal ==>
struct ViewAllProductModal : Codable {
    let code : Int?
    let message : String?
    var data : ViewAllProductData?
    let count : Int?
}
struct ViewAllProductData : Codable {
    var productList : [ViewAllProductList]?
    let name : String?
    let subcategorydescription : String?
}
struct ViewAllProductList : Codable {
    let _id : String?
    let productName : String?
    let price : Int?
    let priceToDisplay : Int?
    let categoryId : String?
    let subcategoryId : String?
    let image : [Image]?
    let currencyType : String?
    let quantity : Int?
    let rating : Int?
    let isQuantityUnlimited : Int?
    let is_wishlist : Int?
    let warrentyMonth : String?
    let discountPercentage : Int?
    let cartStatus : Int?
    let cartQuantity : Int?
    let categoryName : String?
}
// MARK: HomeProductModal ==>
struct HomeProductModal : Codable {
    let code : Int?
    let message : String?
    let data : HomeData?
    let count : Int?
}
struct HomeData : Codable {
    let banner : [Banner]?
    let products : [ProductsData]?
    let categories : [HomeCategories]?
    let costSavedData : HomeCostSavedData?
}
struct Banner : Codable {
    let _id : String?
    let image : String?
    let appImage : String?
    let link : String?
    let web : String?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
}
struct ProductsData : Codable {
    let _id : String?
    let categoryId : String?
    let subcategoryId : String?
    let productName : String?
    let image : [Image]?
    let price : Int?
    let priceToDisplay : Int?
    let currencyType : String?
    let is_wishlisted : Int?
    let cartStatus : Int?
    let categoryName : String?
    let subcategoryName : String?
    let discountPercentage : Int?
}
struct HomeCategories : Codable {
    let _id : String?
    let categoryName : String?
    let description : String?
    let image : String?
    let isDeleted : Bool?
    let isActive : Bool?
    let createdAt : String?
    let updatedAt : String?

}
struct HomeCostSavedData : Codable {
    let totalWaterSaved : Int?
    let totalCostSaved : Int?
}

// MARK: MyProductModal ==>
struct MyProductModal : Codable {
    let code : Int?
    let message : String?
    var data : [InstalledProductData]?
    let count : Int?
}
struct InstalledProductData : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let cartId : String?
    let products : [InstalledProducts]?
    let coupons : [InstalledCoupons]?
    let subTotal : Int?
    let taxAmount : Double?
    let discountedAmount : Double?
    let finalTotalAmount : Double?
    let currency : String?
    let finalOrderStatus : Int?
    let addressId : String?
    let createdAt : String?
    let updatedAt : String?
    let invoiceUrl : String?
    let paymentMethod : String?
    let isSitePhotoUploaded : Int?
}
struct InstalledProducts : Codable {
    let productId : String?
    let product_name : String?
    let modelNo : String?
    let finNo : String?
    let image : String?
    let description : String?
    let quantity : Int?
    let price : Int?
    let installationPincode : Int?
    let installationPrice : Int?
    let totalPrice : Int?
    let orderStatus : Int?
    let installationStatus : Int?
    let staffId : String?
    let pendingProductDate : String?
    let processedProductDate : String?
    let dispatchedProductDate : String?
    let deliveredProductDate : String?
    let _id : String?
    let uuid : String?
    let createdAt : String?
    let isSitePhotoUploaded : Int?
    let remainingWarrantyDays : Double?
    let categoryName : String?
    let categoryId : String?
    let installationId : String?
    let userCount : Int?
    let costSaved : Int?
    let waterSaved : Int?
    let isFieldAvailable : Int?
    let isUrinalSensorAvilable  : Int?
}
struct InstalledCoupons : Codable {
    let couponId : String?
    let title : String?
    let couponType : Int?
    let _id : String?
 
}
// MARK: MyProductDetailsModal ==>
struct MyProductDetailsModal : Codable {
    let code : Int?
    let message : String?
    let data : MyProductDetailData?
    let count : Int?
}
struct MyProductDetailData : Codable {
    let order : MyProductDetailOrder?
   
}
struct MyProductDetailOrder : Codable {
    let _id : String?
    let uuid : String?
    let cartId : String?
    let products : MyProductDetailProducts?
    let coupons : [Coupons]?
    let amountInPaisa : Int?
    let subTotal : Int?
    let taxAmount : Double?
    let discountedAmount : Int?
    let finalTotalAmount : Double?
    let currency : String?
    let paymentStatus : String?
    let finalOrderStatus : Int?
    let addressId : String?
    let firstName : String?
    let lastName : String?
    let phoneNo : Int?
    let countryCode : String?
    let zipCode : Int?
    let address : String?
    let city : String?
    let state : String?
    let razorpayOrderId : String?
    let invoiceUrl : String?
    let installationInfo : [InstallationInfo]?
}
struct MyProductDetailProducts: Codable {
    let productId: String?
    let categoryId: String?
    let product_name: String?
    let modelNo: String?
    let installationDetails: [MyProductDetailInstallationDetails]?
    let image: String?
    let description: String?
    let quantity: Int?
    let price: Int?
    var installationPincode: String?  // Change to String for flexibility
    let installationPrice: Int?
    let totalPrice: Int?
    let orderStatus: Int?
    let staffId: String?
    let assignDate: String?
    let pendingProductDate: String?
    let processedProductDate: String?
    let processedLocation: String?
    let processedEvent: String?
    let dispatchedProductDate: String?
    let dispatchedLocation: String?
    let dispatchedEvent: String?
    let deliveredProductDate: String?
    let deliveredLocation: String?
    let deliveredEvent: String?
    let cancelledDate: String?
    let warrentyStartDate: String?
    let warrentyEndDate: String?
    let warrentyMonth: String?
    let _id: String?
    let categoryName: String?
    let isFieldAvailable: Int?
    let isUrinalSensorAvilable : Int?
    let costSaved: Int?
    let waterSaved: Int?
    let remainingWarrantyDays: Int?

    enum CodingKeys: String, CodingKey {
        case productId, categoryId, product_name, modelNo, installationDetails, image, description, quantity, price, installationPincode, installationPrice, totalPrice, orderStatus, staffId, assignDate, pendingProductDate, processedProductDate, processedLocation, processedEvent, dispatchedProductDate, dispatchedLocation, dispatchedEvent, deliveredProductDate, deliveredLocation, deliveredEvent, cancelledDate, warrentyStartDate, warrentyEndDate, warrentyMonth, _id, categoryName, isFieldAvailable, costSaved, waterSaved, remainingWarrantyDays, isUrinalSensorAvilable
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)

        productId = try? container.decode(String.self, forKey: .productId)
        categoryId = try? container.decode(String.self, forKey: .categoryId)
        product_name = try? container.decode(String.self, forKey: .product_name)
        modelNo = try? container.decode(String.self, forKey: .modelNo)
        installationDetails = try? container.decode([MyProductDetailInstallationDetails].self, forKey: .installationDetails)
        image = try? container.decode(String.self, forKey: .image)
        description = try? container.decode(String.self, forKey: .description)
        quantity = try? container.decode(Int.self, forKey: .quantity)
        price = try? container.decode(Int.self, forKey: .price)
        installationPrice = try? container.decode(Int.self, forKey: .installationPrice)
        totalPrice = try? container.decode(Int.self, forKey: .totalPrice)
        orderStatus = try? container.decode(Int.self, forKey: .orderStatus)
        staffId = try? container.decode(String.self, forKey: .staffId)
        assignDate = try? container.decode(String.self, forKey: .assignDate)
        pendingProductDate = try? container.decode(String.self, forKey: .pendingProductDate)
        processedProductDate = try? container.decode(String.self, forKey: .processedProductDate)
        processedLocation = try? container.decode(String.self, forKey: .processedLocation)
        processedEvent = try? container.decode(String.self, forKey: .processedEvent)
        dispatchedProductDate = try? container.decode(String.self, forKey: .dispatchedProductDate)
        dispatchedLocation = try? container.decode(String.self, forKey: .dispatchedLocation)
        dispatchedEvent = try? container.decode(String.self, forKey: .dispatchedEvent)
        deliveredProductDate = try? container.decode(String.self, forKey: .deliveredProductDate)
        deliveredLocation = try? container.decode(String.self, forKey: .deliveredLocation)
        deliveredEvent = try? container.decode(String.self, forKey: .deliveredEvent)
        cancelledDate = try? container.decode(String.self, forKey: .cancelledDate)
        warrentyStartDate = try? container.decode(String.self, forKey: .warrentyStartDate)
        warrentyEndDate = try? container.decode(String.self, forKey: .warrentyEndDate)
        warrentyMonth = try? container.decode(String.self, forKey: .warrentyMonth)
        _id = try? container.decode(String.self, forKey: ._id)
        categoryName = try? container.decode(String.self, forKey: .categoryName)
        isFieldAvailable = try? container.decode(Int.self, forKey: .isFieldAvailable)
        costSaved = try? container.decode(Int.self, forKey: .costSaved)
        waterSaved = try? container.decode(Int.self, forKey: .waterSaved)
        remainingWarrantyDays = try? container.decode(Int.self, forKey: .remainingWarrantyDays)
        isUrinalSensorAvilable = try? container.decode(Int.self, forKey: .isUrinalSensorAvilable)

        // Handling installationPincode (String or Int)
        if let pincodeInt = try? container.decode(Int.self, forKey: .installationPincode) {
            installationPincode = String(pincodeInt)
        } else {
            installationPincode = try? container.decode(String.self, forKey: .installationPincode)
        }
    }
}
struct MyProductDetailInstallationDetails : Codable {
    let finNo : String?
    let installationStatus : Int?
    let _id : String?
}
struct Coupons : Codable {
    let couponId : String?
    let title : String?
    let couponType : Int?
    let _id : String?
}
struct InstallationInfo : Codable {
    let _id : String?
    let staffId : String?
    let customerId : String?
    let orderId : String?
    let productId : String?
    let subscriptionId : String?
    let productName : String?
    let modelNo : String?
    let finNo : String?
    let sin : String?
    let productImage : [ProductImage]?
    let waterSaved : Int?
    let costSaved : Int?
    let userCount : Int?
    let range : Int?
    let preflush : Int?
    let flushTime : Int?
    let batteryVoltage : Int?
    let warrentyStartDate : String?
    let warrentyEndDate : String?
    let warrentyMonth : String?
    let totalPayment : Int?
    let image : [MyProductDetailImage]?
    let complaintId : String?
    let remark : String?
    let customerName : String?
    let otp : String?
    let isOtpVerfied : Bool?
    let status : Bool?
    let updatedByAdmin : Int?
    let installationType : Int?
    let installationStatus : Int?
    let complaintStatus : Int?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let __v : Int?
    let resolveDate : String?
}
struct ProductImage : Codable {
    let url : String?
    let _id : String?
}
struct MyProductDetailImage : Codable {
    let url : String?
    let _id : String?
}


