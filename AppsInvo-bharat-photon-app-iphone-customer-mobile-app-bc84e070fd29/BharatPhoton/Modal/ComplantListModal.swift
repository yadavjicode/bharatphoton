//
//  RegisterComplantListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 15/11/24.
//

import Foundation

 
// MARK: ComplantListModal ==>
struct ComplantListModal : Codable {
    let code : Int?
    let message : String?
    let data : [ComplantData]?

}
struct ComplantData : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let categoryId : String?
    let productId : String?
    let sin : String?
    let complaintReason : String?
    let description : String?
    let image : [ComplantImage]?
    let complaintStatus : Int?
    let createdAt : String?
    let updatedAt : String?
    let categoryName : String?
    let productName : String?
    let productImage : [ComplantProductImage]?
    let productDescription : String?
    let modelNo : String?

}
struct ComplantImage : Codable {
    let url : String?
    let _id : String?
}
struct ComplantProductImage : Codable {
    let url : String?
    let _id : String?
}

 // MARK: ComplantListDetailModal ==>
struct ComplantListDetailModal : Codable {
    let code : Int?
    let message : String?
    let data : ComplantListDetailData?
    let count : Int?
}
struct ComplantListDetailData : Codable {
    let complaintDetails : ComplaintListDetails?
    let resolvedDetails : ComplaintListResolvedDetails?
}
struct ComplaintListDetails : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let categoryId : String?
    let productId : String?
    let sin : String?
    let complaintReason : String?
    let description : String?
    let image : [ComplaintListImage]?
    let staffId : String?
    let complaintStatus : Int?
    let createdAt : String?
    let updatedAt : String?
    let categoryName : String?
    let productName : String?
    let productImage : [ComplaintListProductImage]?
    let productDescription : String?
    let modelNo : String?
    let staffFirstName : String?
    let staffLastName : String?
    let staffImage : String?
    let staffEmail : String?
    let staffPhoneNo : Int?
    let staffCountryCode : String?

}
struct ComplaintListImage : Codable {
    let url : String?
    let _id : String?
}
struct ComplaintListProductImage : Codable {
    let url : String?
    let _id : String?
}
struct ComplaintListResolvedDetails : Codable {
    let _id : String?
    let sin : String?
    let waterSaved : Int?
    let costSaved : Int?
    let userCount : Int?
    let range : Int?
    let preflush : Int?
    let flushTime : Int?
    let batteryVoltage : Int?
    let totalPayment : Int?
    let remark : String?
    let customerName : String?
    let uuid : String?
    let resolveDate : String?
    let resolveAction : String?

}


