//
//  RenewPlanModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 21/11/24.
//

import Foundation

struct RenewPlanModal : Codable {
    let code : Int?
    let message : String?
    let data : RenewPlanData?
    let count : Int?

}
struct RenewPlanData : Codable {
    let currentPlan : CurrentPlan?
    let avilablePlan : [AvilablePlan]?
    let installationDetails : [InstallationDetails]?
}
struct CurrentPlan : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let sin : String?
    let productId : String?
    let quantity : Int?
    let productImage : String?
    let productName : String?
    let categoryName : String?
    let modelNo : String?
    let planType : Int?
    let price : Int?
    let mode : String?
    let currencyType : String?
    let totalPrice : Int?
    let taxAmount : Int?
    let finalTotalAmount : Int?
    let paymentMode : Int?
    let paymentMethod : String?
    let paymentStatus : String?
    let subscriptionStartDate : String?
    let subscriptionExpiryDate : String?
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
    let createdAt : String?
    let updatedAt : String?
    let categoryId : String?
    let daysRemaining : Int?
}
struct AvilablePlan : Codable {
    let _id : String?
    let image : String?
    let products : [String]?
    let pincode : [Int]?
    let title : String?
    let mode : String?
    let planType : Int?
    let description : String?
    let price : Int?
    let currencyType : String?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
}
struct InstallationDetails : Codable {
    let _id : String?
    let orderId : String?
    let productId : String?
    let finNo : String?
    let sin : String?

}

