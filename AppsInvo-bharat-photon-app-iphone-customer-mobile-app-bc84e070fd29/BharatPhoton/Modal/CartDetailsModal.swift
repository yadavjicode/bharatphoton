//
//  CartDetailsModal.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/09/24.
//

import Foundation

// MARK: CartDetailsModal ==>
struct CartDetailsModal : Codable {
    let code : Int?
    let message : String?
    var data : CartDetailData?
    let count : Int?

}
// Data
struct CartDetailData : Codable {
    var cart : Cart?
    let address : CartAddress?
}
// CART
struct Cart : Codable {
    let _id : String?
    let userId : String?
    let deviceId : String?
    var products : [CartProducts]?
    let totalAmount : Int?
    let taxAmount : Double?
    let finalTotalAmount : Double?
    let discountAmount : Double?
    let finalTotalAmountAfterDiscount : Double?

}
// Products
struct CartProducts : Codable {
    let productId : String?
    var quantity : Int?
    let installationPincode : Int?
    let _id : String?
    let productName : String?
    let image : [CartImage]?
    let description : String?
    let price : Int?
    let priceToDisplay : Int?
    let productquantity : Int?
    let isQuantityUnlimited : Int?
    let totalPrice : Int?
    let rating: Int?

}
struct CartImage : Codable {
    let url : String?
    let _id : String?

}
struct CartAddress : Codable {
    let selectedAddress : String?
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
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?

}
// MARK: CouponListModal ==>
struct CouponListModal : Codable {
    let code : Int?
    let message : String?
    let data : [CouponData]?
    let count : Int?
}
struct CouponData : Codable {
    let _id : String?
    let title : String?
    let productId : String?
    let categoryId : String?
    let description : String?
    let couponCode : String?
    let couponType : Int?
    let discount : Int?
    let startDate : String?
    let endDate : String?
    let isCouponApplied : Bool?
}
