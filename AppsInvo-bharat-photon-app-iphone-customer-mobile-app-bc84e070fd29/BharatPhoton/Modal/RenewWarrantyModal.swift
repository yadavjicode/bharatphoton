//
//  RenewWarrantyModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 13/12/24.
//

import Foundation

struct RenewWarrantyModal : Codable {
    let code : Int?
    let message : String?
    let data : RenewWarrantyData?
}
struct RenewWarrantyData : Codable {
    let userId : String?
    let previousOrderId : String?
    let productId : String?
    let amountInPaisa : Int?
    let subTotal : Int?
    let currency : String?
    let warrentyId : String?
    let title : String?
    let mode : String?
    let description : String?
    let paymentMode : Int?
    let paymentStatus : String?
    let status : Bool?
    let is_deleted : Bool?
    let _id : String?
    let createdAt : String?
    let updatedAt : String?
    let __v : Int?
    let razorpayOrderId : String?
}
// MARK: RenewWarrantyListModal ==>
struct RenewWarrantyListModal : Codable {
    let code : Int?
    let message : String?
    let data : [RenewWarrantyListData]?
    let count : Int?
}
struct RenewWarrantyListData : Codable {
    let _id : String?
    let customerId : String?
    let orderId : String?
    let productId : String?
    let productName : String?
    let modelNo : String?
    let sin : String?
    let finNo : String?
    let uninstalledProductImage : String?
    let productImage : [RenewWarrantyProductImage]?
    let warrentyStartDate : String?
    let warrentyEndDate : String?
    let warrentyMonth : String?
    let remainingWarrantyDays : Int?
    let uuid : String?
    let categoryName : String?
}
struct RenewWarrantyProductImage : Codable {
    let url : String?
    let _id : String?
}

