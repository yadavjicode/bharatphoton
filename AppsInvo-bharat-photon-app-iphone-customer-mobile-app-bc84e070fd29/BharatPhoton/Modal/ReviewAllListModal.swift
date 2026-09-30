
//
//  ReviewAllListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 24/12/24.
//

import Foundation


struct ReviewAllListModal : Codable {
    let code : Int?
    let message : String?
    let data : [ReviewAllListData]?
    let count : Int?
}
struct ReviewAllListData : Codable {
    let _id : String?
    let userId : String?
    let installationId : String?
    let complaintId : String?
    let productId : String?
    let rating : Double?
    let review : String?
    let image : [ReviewAllImages]?
    let isApprovedByAdmin : Bool?
    let status : Bool?
    let staffReview : Int?
    let createdAt : String?
    let updatedAt : String?
    let userProfileImage : String?
    let userFirstName : String?
    let userLastName : String?
}
struct ReviewAllImages : Codable {
    let url : String?
    let _id : String?
}
