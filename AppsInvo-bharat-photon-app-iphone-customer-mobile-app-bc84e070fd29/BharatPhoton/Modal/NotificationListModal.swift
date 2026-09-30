//
//  NotificationListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 24/12/24.
//

import Foundation

struct NotificationListModal : Codable {
    let code : Int?
    let message : String?
    var data : [NotificationListData]?
    let count : Int?
}
struct NotificationListData : Codable {
    let _id : String?
    let userId : String?
    let title : String?
    let body : String?
    let read : Int?
    let is_Active : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
    let __v : Int?
    let profileImage : String?
}
