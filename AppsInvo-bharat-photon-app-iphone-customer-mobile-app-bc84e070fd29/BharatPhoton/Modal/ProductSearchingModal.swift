//
//  ProductSearchingModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 25/06/25.
//

import Foundation

struct ProductSearchingModal : Codable {
    let code : Int?
    let message : String?
    let data : [ProductSearchingData]?
    let count : Int?
}
struct ProductSearchingData : Codable {
    let _id : String?
    let categoryId : String?
    let subcategoryId : String?
    let productName : String?
    let image : [Image]?
    let description : String?
    let price : Int?
    let rating: Int?
    let priceToDisplay : Int?
    let currencyType : String?
    let specification : [Specification]?
    let features : [Features]?
    let youtube : [Youtube]?
    let quantity : Int?
    let isQuantityUnlimited : Int?
    let warrentyMonth : String?
    let uploadBrocheure : String?
    let amazonProductLink : String?
    let flipcartProductLink : String?
    let isDeleted : Bool?
    let isActive : Bool?
    let createdAt : String?
    let updatedAt : String?
    let productSlug : String?
    let is_wishlist : Int?
    let discountPercentage : Int?
    let cartStatus : Int?
    let cartQuantity : Int?
    let categoryName : String?
}
