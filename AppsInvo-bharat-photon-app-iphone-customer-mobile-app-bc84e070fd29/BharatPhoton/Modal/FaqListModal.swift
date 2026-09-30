//
//  FaqListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 12/12/24.
//

import Foundation

struct FaqListModal : Codable {
    let code : Int?
    let message : String?
    let data : [FaqListData]?
    let count : Int?
}
struct FaqListData : Codable {
    let _id : String?
    let question : String?
    let answer : String?
    let is_status : Int?
    let is_deleted : Int?
    let createdAt : String?
    let updatedAt : String?

}
