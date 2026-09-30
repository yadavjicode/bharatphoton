//
//  TrackOrderDetailsModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 07/01/25.
//

import Foundation

struct TrackOrderDetailsModal : Codable {
    let code : Int?
    let message : String?
    let data : TrackOrderDetailData?
}
struct TrackOrderDetailData : Codable {
    let orderId : String?
    let uuid : String?
    let userId : String?
    let product : TrackOrderProduct?
    let coupons : [TrackOrderCoupons]?
    let finalOrderStatus : Int?
    let subTotal : Int?
    let taxAmount : Double?
    let finalTotalAmount : Double?
    let discountedAmount : Int?
    let currency : String?
    let paymentMethod : String?
    let invoiceUrl : String?
    let address : TrackOrderAddress?
    let installationAddress : TrackOrderInstallationAddress?
    let createdAt : String?
    let updatedAt : String?
}
struct TrackOrderProduct : Codable {
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
}
struct TrackOrderAddress : Codable {
    let firstName : String?
    let lastName : String?
    let phoneNo : Int?
    let countryCode : String?
    let ZipCode : Int?
    let address : String?
    let city : String?
    let state : String?
    let address_type : Int?
}
struct TrackOrderInstallationAddress : Codable {
    let _id : String?
    let userId : String?
    let orderId : String?
    let address : String?
    let ZipCode : Int?
    let state : String?
    let city : String?
}
struct TrackOrderCoupons : Codable {
    let couponId : String?
    let title : String?
    let couponType : Int?
    let _id : String?
}
