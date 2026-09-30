//
//  PayNowModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 13/12/24.
//

import Foundation

struct PayNowModal : Codable {
    let code : Int?
    let message : String?
    let data : PayNowData?
}
struct PayNowData : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let cartId : String?
    let products : [PayNowProducts]?
    let coupons : [String]?
    let subTotal : Int?
    let taxAmount : Int?
    let discountedAmount : Int?
    let finalTotalAmount : Int?
    let currency : String?
    let paymentMode : Int?
    let paymentMethod : String?
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
    let address_type : Int?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let __v : Int?
    let invoiceUrl : String?
    let amountInPaisa : Int?
    let razorpayOrderId : String?

}
struct PayNowProducts : Codable {
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
    let warrentyStartDate : String?
    let warrentyEndDate : String?
    let _id : String?

}

