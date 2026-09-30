//
//  Modal.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 16/09/24.
//

import Foundation
import UIKit

// MARK: CategoryModal ===>
struct CategoryModal : Codable {
    let code : Int?
    let message : String?
    let data : [CatData]?
    let count : Int?
}
struct CatData : Codable {
    let _id : String?
    let categoryName : String?
    let image : String?
    let isDeleted : Bool?
    let isActive : Bool?
    let createdAt : String?
    let updatedAt : String?
}
// MARK: SubCategoryModal ===>
struct SubCategoryModal : Codable {
    let code : Int?
    let message : String?
    let data : [SubCatData]?
    let count : Int?
}
struct SubCatData : Codable {
    let _id : String?
    let categoryId : String?
    let image : String?
    let subcategoryName : String?
    let isDeleted : Bool?
    let isActive : Bool?
    let createdAt : String?
    let updatedAt : String?
    let categoryName : String?
}
// MARK: ProductListingModal ===>
struct ProductListingModal : Codable {
    let code : Int?
    let message : String?
    let data : [ProductListingData]?
    let count : Int?
}
struct ProductListingData : Codable {
    let _id : String?
    let categoryId : String?
    let productName : String?
    let modelNo : String?
    let title : String?
}
// MARK: StateModal ==>
struct StateModal : Codable {
    let code : Int?
    let message : String?
    let data : [String]?
    let count : Int?
}
// MARK: CityModal ==>
struct CityModal : Codable {
    let code : Int?
    let message : String?
    let data : [String]?
    let count : Int?
}
// MARK: AboutUsModal ==>
struct AboutUsModal : Codable {
    let code : Int?
    let message : String?
    let data : AboutUsData?
    let count : Int?
}
struct AboutUsData : Codable {
    let _id : String?
    let title : String?
    let descriptions : String?
    let type : String?
    let userType : Int?
    let is_status : Int?
    let createdAt : String?
    let updatedAt : String?

}
// MARK:  WishListModal  ==>
struct WishListModal : Codable {
    let code : Int?
    let message : String?
    var data : [WishListData]?
    let count : Int?
}
struct WishListData : Codable {
    let productId : String?
    let _id : String?
    let userId : String?
    let rating : Int?
    let isActive : Bool?
    let idDeleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let productName : String?
    let productImage : [WishListProductImage]?
    let price : Int?
    let priceToDisplay : Int?
    let currencyType : String?
}
struct WishListProductImage : Codable {
    let url : String?
    let _id : String?
}
