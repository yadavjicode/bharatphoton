//
//  AddressModal.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 24/09/24.
//

import Foundation

// MARK: AddressListModal ==>
struct AddressListModal : Codable {
    let code : Int?
    let message : String?
    let data : [AddressData]?
    let count : Int?
}
struct AddressData : Codable {
    let _id : String?
    let userId : String?
    let firstName : String?
    let lastName : String?
    let phoneNo : Int?
    let country_code : String?
    let address : String?
    let ZipCode : Int?
    let state : String?
    let city : String?
    let addressType : Int?
    let selectedAddress : String?
    let status : Bool?
    let is_deleted : Bool?
    let createdAt : String?
    let updatedAt : String?
}
