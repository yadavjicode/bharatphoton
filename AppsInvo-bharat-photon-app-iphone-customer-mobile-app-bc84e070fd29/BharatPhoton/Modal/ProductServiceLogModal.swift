//
//  ProductServiceLogModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 19/02/25.
//

import Foundation

struct ProductServiceLogModal : Codable {
    let code : Int?
    let message : String?
    let data : [ProductServiceLogData]?
//    let count : Count?
}
struct ProductServiceLogData : Codable {
    let _id : String?
    let uuid : String?
    let sin : String?
    let complaintReason : String?
    let description : String?
    let resolveDate : String?
    let staffId : String?
    let complaintStatus : Int?
    let createdAt : String?
    let payment : Int?
    let resolveActionTaken : String?
    let customerName : String?
    let remark : String?
    let firstName : String?
    let lastName : String?
    let profileImage : String?

}
 
