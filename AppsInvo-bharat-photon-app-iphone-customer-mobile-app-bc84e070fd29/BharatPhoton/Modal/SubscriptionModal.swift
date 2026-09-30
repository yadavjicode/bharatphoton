//
//  File.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/09/24.
//

import Foundation
 
// MARK: PurchasedSubListingModal ==>
struct PurchasedSubListingModal : Codable {
    let code : Int?
    let message : String?
    var data : [PurchasedSubListingData]?
    let count : Int?
}
struct PurchasedSubListingData : Codable {
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
    let finalTotalAmount : Double?
    let paymentMode : Int?
    let paymentMethod : String?
    let subscriptionStartDate : String?
    let subscriptionExpiryDate : String?
    let createdAt : String?
    let updatedAt : String?
    let invoiceUrl : String?
    let isFieldAvailable : Int?
    let isUrinalSensorAvilable : Int?
    let installationMetrics : [InstallationMetrics]?
    let totalWaterSaved : Int?
    let totalCostSaved : Int?
    let installationAddressId : String?
    let isSitePhotoUploaded : Int?
    let isPlanExpired : Int?
    let categoryId : String?
}
struct InstallationMetrics : Codable {
    let _id : String?
    let finNo : String?
    let sin : String?
    let waterSaved : Int?
    let costSaved : Int?
    let subscriptionStartDate : String?
    let subscriptionExpiryDate : String?
    let isPlanExpired : Int?
}
// MARK: PurchasedSubListingDetailModal ==>
struct PurchasedSubListingDetailModal : Codable {
    let code : Int?
    let message : String?
    let data : ListingDetailData?
}
struct ListingDetailData : Codable {
    let subscriptionLisitng : DetailSubscriptionLisitng?
    let sinDetails : SinDetails?
    let complaintDates : [String]?
    let installationAddress : InstallationAddress?
}
struct DetailSubscriptionLisitng : Codable {
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
    let finalTotalAmount : Double?
    let paymentMode : Int?
    let paymentMethod : String?
    let paymentStatus : String?
    let subscriptionStartDate : String?
    let subscriptionExpiryDate : String?
    let staffId : String?
    let finalOrderStatus : Int?
    let addressId : String?
    let firstName : String?
    let lastName : String?
    let phoneNo : Int?
    let countryCode : String?
    let ZipCode : Int?
    let address : String?
    let city : String?
    let state : String?
    let address_type : Int?
    let status : Bool?
    let createdAt : String?
    let updatedAt : String?
    let resolveDate : String?
    let categoryId : String?
    let productDetailsimage : [ProductDetailsimage]?
    let specification : [Specification]?
    let features : [Features]?
    let youtube : [Youtube]?
    let daysRemaining : Int?
    let invoiceUrl : String?
    let isFieldAvailable : Int?
    let isUrinalSensorAvilable : Int?
}
struct SinDetails : Codable {
    let _id : String?
    let sin : String?
    let waterSaved : Int?
    let costSaved : Int?
    let userCount : Int?
    let range : Int?
    let offDelay : Int?
    let cutOffDelay : Int?
    let preflush : Int?
    let flushTime : Int?
    let batteryVoltage : Int?
    let totalPayment : Int?
    let image : [SinDetailsImage]?
    let customerName : String?
    let remark : String?
    let isFieldAvailable : Int?
    let isUrinalSensorAvilable : Int?
    let subscriptionStartDate : String?
    let subscriptionExpiryDate : String?
    let remainingDays : Int?
}
struct SinDetailsImage : Codable {
    let url : String?
    let _id : String?
}
struct ProductDetailsimage : Codable {
    let url : String?
    let _id : String?
}
struct InstallationAddress : Codable {
    let _id : String?
    let userId : String?
    let orderId : String?
    let address : String?
    let ZipCode : Int?
    let state : String?
    let city : String?
    let status : Bool?
    let is_deleted : Bool?
    let image : [InstallationAddressImage]?
    let createdAt : String?
    let updatedAt : String?
}
struct InstallationAddressImage : Codable {
    let url : String?
    let _id : String?
}

// MARK: According To ProductSubscription List ==>
struct ProductSubscriptionModal : Codable {
    let code : Int?
    let message : String?
    let data : [ProductSubscriptionData]?
}
struct ProductSubscriptionData : Codable {
    let _id : String?
    let image : String?
    let products : [String]?
    let pincode : [Int?]?
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
    let productId : String?
    let productName : String?
    let modelNo : String?
    let categoryName : String?
    let productImage : [ProductSubscriptionProductImage]?
    let productDescription : String?

}
struct ProductSubscriptionProductImage : Codable {
    let url : String?
    let _id : String?
}
// MARK: CartSubscriptionListModal ==>
struct CartSubscriptionListModal : Codable {
    let code : Int?
    let message : String?
    let data : CartSubscriptionData?
    let count : Int?
}
struct CartSubscriptionData : Codable {
    let _id : String?
    let userId : String?
    let quantity : Int?
    let subscriptionId : String?
    let productId : String?
    let price : Int?
    let planType : Int?
    let mode : String?
    let totalPrice : Int?
    let taxAmount : Int?
    let finalTotalAmount : Double?
    let productName : String?
    let image : [CartSubscriptionImage]?
    let description : String?
    let rating : Double?
}
struct CartSubscriptionImage : Codable {
    let url : String?
    let _id : String?
}

