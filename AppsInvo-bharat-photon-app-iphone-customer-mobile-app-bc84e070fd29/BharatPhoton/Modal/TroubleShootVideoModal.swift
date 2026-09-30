//
//  TroubleShootVideoModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 03/12/24.
//

import Foundation

struct TroubleShootVideoModal : Codable {
    let code : Int?
    let message : String?
    let data : TroubleShootVideoData?
    let count : Int?
}
struct TroubleShootVideoData : Codable {
    let _id : String?
    let categoryId : String?
    let products : [String]?
    let type : String?
    let userManual : String?
    let troubleVideos : [TroubleVideos]?
    let installationVideo : [InstallationVideo]?
    let userType : Int?
    let is_status : Int?
    let createdAt : String?
    let updatedAt : String?
}
struct TroubleVideos : Codable {
    let url : String?
    let _id : String?
}
struct InstallationVideo : Codable {
    let url : String?
    let _id : String?
}

