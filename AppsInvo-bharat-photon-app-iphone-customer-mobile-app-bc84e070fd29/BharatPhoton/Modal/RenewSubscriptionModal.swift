//
//  RenewSubscriptionModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 19/02/25.
//

import Foundation

struct RenewSubscriptionModal : Codable {
    let code : Int?
    let message : String?
    var data : [RenewSubscriptionData]?
    let count : Int?
}
struct RenewSubscriptionData : Codable {
    let _id : String?
    let customerId : String?
    let orderId : String?
    let productId : String?
    let productName : String?
    let categoryName : String?
    let modelNo : String?
    let sin : String?
    let productImage : [RenewSubscriptionProductImage]?
    let subscriptionuuid : String?
    let subscriptionStartDate : String?
    let subscriptionExpiryDate : String?
    let subscriptionMode : String?
    let createdAt : String?
    let updatedAt : String?
    let isFieldAvailable : Int?
    let isUrinalSensorAvilable : Int?
    let remainingDays : Int?
    let categoryId : String?
}
struct RenewSubscriptionProductImage : Codable {
    let url : String?
    let _id : String?
}
