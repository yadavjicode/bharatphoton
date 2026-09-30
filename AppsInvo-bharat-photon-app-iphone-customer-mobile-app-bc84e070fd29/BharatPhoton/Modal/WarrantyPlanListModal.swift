//
//  WarrantyPlanListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 09/12/24.
//

import Foundation

struct WarrantyPlanListModal : Codable {
    let code : Int?
    let message : String?
    let data : [WarrantyPlanListData]?
    let count : Int?
}
struct WarrantyPlanListData : Codable {
    let _id : String?
    let image : String?
    let categoryId : String?
    let products : [String]?
    let title : String?
    let mode : String?
    let description : String?
    let price : Int?
    let currencyType : String?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?

}
