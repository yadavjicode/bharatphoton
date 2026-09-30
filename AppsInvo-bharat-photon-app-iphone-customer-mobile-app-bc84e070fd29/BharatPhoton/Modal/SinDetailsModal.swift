//
//  SinDetailsModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 09/12/24.
//

import Foundation

struct SinDetailsModal : Codable {
    let code : Int?
    let message : String?
    let data : SinDetailsData?

}
struct SinDetailsData : Codable {
    let installatedDetails : SinInstallatedDetails?
    let complaintDetails : [String]?
    let complaintDates : [String]?
}
struct SinInstallatedDetails : Codable {
    let _id : String?
    let staffId : String?
    let customerId : String?
    let orderId : String?
    let productId : String?
    let subscriptionId : String?
    let productName : String?
    let modelNo : String?
    let finNo : String?
    let sin : String?
    let productImage : [SinProductImage]?
    let waterSaved : Int?
    let costSaved : Int?
    let userCount : Int?
    let range : Int?
    let preflush : Int?
    let flushTime : Int?
    let batteryVoltage : Int?
    let offDelay : Int?
    let cutOffDelay : Int?
    let warrentyStartDate : String?
    let warrentyEndDate : String?
    let warrentyMonth : String?
    let totalPayment : Int?
    let image : [SinImage]?
    let complaintId : String?
    let remark : String?
    let customerName : String?
    let otp : String?
    let isOtpVerfied : Bool?
    let status : Bool?
    let updatedByAdmin : Int?
    let installationType : Int?
    let installationStatus : Int?
    let complaintStatus : Int?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let resolveDate : String?
    let categoryName : String?
    let categoryId : String?
    let remainingWarrantyDays : Int?
    let isFieldAvailable : Int?
    let isUrinalSensorAvilable : Int?
}
struct SinProductImage : Codable {
    let url : String?
    let _id : String?
}
struct SinImage : Codable {
    let url : String?
    let _id : String?

}


