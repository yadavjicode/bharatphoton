//
//  ProductDetailsModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 28/10/24.
//

import Foundation
 

struct ProductDetailsModal : Codable {
    let code : Int?
    let message : String?
    let data : DetailsData?
    let count : Int?
}
struct DetailsData : Codable {
    let productDetails : ProductDetails?
    let reviews : [Reviews]?
    let subscriptionPrice : SubscriptionPrice?
}
struct ProductDetails : Codable {
    let _id : String?
    let categoryId : String?
    let subcategoryId : String?
    let productName : String?
    let modelNo : String?
    let image : [ProductDetailsImg]?
    let description : String?
    let price : Int?
    let priceToDisplay : Int?
    let currencyType : String?
    let warrenty : Bool?
    let replacement : Bool?
    let specification : [Specification]?
    let features : [Features]?
    let youtube : [Youtube]?
    let installation : [Installation]?
    let quantity : Int?
    let uploadBrocheure : String?
    let amazonProductLink : String?
    let flipcartProductLink : String?
    let isDeleted : Bool?
    let isActive : Bool?
    let createdAt : String?
    let updatedAt : String?
    let rating : Int?
    let finNo : String?
    let discountPercentage : Int?
    let is_wishlist : Int?
    let cartStatus : Int?
    let cartQuantity : Int?
    let isQuantityUnlimited : Int?
    let categoryName : String?
    
}
struct ProductDetailsImg : Codable {
    let url : String?
    let _id : String?
}
struct Specification : Codable {
    let filedName : String?
    let value : String?
    let _id : String?
     
}
struct Features : Codable {
    let label : String?
    let description : String?
    let _id : String?
     
}
struct Youtube : Codable {
    let demoVideo : String?
    let installationVideo : String?
    let _id : String?
}
struct Installation : Codable {
    let pincode : Int?
    let price : Int?
    let _id : String?
   
}
struct Reviews : Codable {
    let _id : String?
    let userId : String?
    let productId : String?
    let rating : Double?
    let review : String?
    let image : [ReviewImages]?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let userProfile : UserProfile?
}
struct ReviewImages : Codable {
    let url : String?
    let _id : String?
}
struct UserProfile : Codable {
    let _id : String?
    let firstName : String?
    let lastName : String?
    let profileImage : String?
}
struct SubscriptionPrice : Codable {
    let price : Int?
    let mode : String?
    let planType : Int?
    let currencyType : String?
}
