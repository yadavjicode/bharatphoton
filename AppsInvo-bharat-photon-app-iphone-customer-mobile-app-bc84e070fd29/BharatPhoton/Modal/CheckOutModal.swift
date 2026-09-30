//
//  CheckOutModal.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 25/09/24.
//

import Foundation

//MARK: CheckOutModal ==>
struct CheckOutModal : Codable {
    let code : Int?
    let message : String?
    let data : CheckOutData?
    let count : Int?
}
// Data
struct CheckOutData : Codable {
    let cart : CartItem?
    let coupon : Coupon?
    let priceSummary : PriceSummary?
    let address : Address?

}
// Cart
struct CartItem : Codable {
    let _id : String?
    let deviceId : String?
    let products : [CheckOutProducts]?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    
}
// Products
struct CheckOutProducts : Codable {
    let productId : String?
    let quantity : Int?
    let _id : String?
    let productName : String?
    let image : [CheckOutImage]?
    let price : Int?
    let priceToDisplay : Int?
    let installationPrice : Int?

}
// Image
struct CheckOutImage : Codable {
    let url : String?
    let _id : String?


}
// Coupon
struct Coupon : Codable {
    let couponCodes : [String]?

}
// PriceSummary
struct PriceSummary : Codable {
    let totalAmount : Int?
    let taxAmount : Double?
    let finalTotalAmount : Double?
    let discountAmount : Double?
    let finalTotalAmountAfterDiscount : Double?
}
// Address
struct Address : Codable {
    let _id : String?
    let userId : String?
    let firstName : String?
    let lastName : String?
    let phoneNo : Int?
    let country_code : String?
    let address : String?
    let zipCode : Int?
    let state : String?
    let city : String?
    let addressType : Int?
    let selectedAddress : String?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?

}

struct InstallationData : Codable {
    let cart : InstallationCart?
}
struct InstallationCart : Codable {
    let _id : String?
    let products : [InstallationProducts]?
}
struct InstallationProducts : Codable {
    let productId : String?
    let quantity : Int?
    let installationPincode : Int?
    let _id : String?

}
 

