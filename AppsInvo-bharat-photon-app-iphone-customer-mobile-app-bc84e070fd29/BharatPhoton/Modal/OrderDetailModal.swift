//
//  OrderDetailModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 24/10/24.
//

import Foundation
// MARK: OrderDetailModal ==>
struct OrderDetailModal : Codable {
    let code : Int?
    let message : String?
    let data : OrderDetailData?
}
struct OrderDetailData : Codable {
    let orderDetails : OrderDetails?
    let installastionAddrress : OrderDetailsInstallastionAddrress?

}
struct OrderDetails : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let cartId : String?
    let products : [OrderDetailsProducts]?
    let coupons : [OrderDetailsCoupons]?
    let amountInPaisa : Int?
    let subTotal : Int?
    let taxAmount : Double?
    let discountedAmount : Int?
    let finalTotalAmount : Double?
    let currency : String?
    let paymentMode : Int?
    let paymentStatus : String?
    let finalOrderStatus : Int?
    let addressId : String?
    let firstName : String?
    let lastName : String?
    let phoneNo : Int?
    let countryCode : String?
    let ZipCode : Int?
    let address : String?
    let city : String?
    let state : String?
    let address_type : Int?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let razorpayOrderId : String?
    let invoiceUrl : String?
    let paymentMethod : String?
}
struct OrderDetailsProducts : Codable {
    let productId : String?
    let product_name : String?
    let modelNo : String?
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
    let assignDate : String?
    let pendingProductDate : String?
    let processedProductDate : String?
    let dispatchedProductDate : String?
    let deliveredProductDate : String?
    let warrentyStartDate : String?
    let warrentyEndDate : String?
    let _id : String?
    let finNo : String?
    let categoryName : String?
}
struct OrderDetailsCoupons : Codable {
    let couponId : String?
    let title : String?
    let couponType : Int?
    let _id : String?
}
struct OrderDetailsInstallastionAddrress : Codable {
    let _id : String?
    let userId : String?
    let orderId : String?
    let address : String?
    let ZipCode : Int?
    let state : String?
    let city : String?
    let image : [OrderDetailsImage]?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
}
struct OrderDetailsImage : Codable {
    let url : String?
    let _id : String?

}
