//
//  PaymentHistoryListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 15/11/24.
//

import Foundation

struct PaymentHistoryListModal : Codable {
    let code : Int?
    let message : String?
    var data : [PaymentHistoryList]?
    let count : Int?

}
struct PaymentHistoryList : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let finalTotalAmount : Double?
    let currency : String?
    let paymentStatus : String?
    let createdAt : String?
    let updatedAt : String?
    let paymentMethod : String?
    let invoiceUrl :  String?
    let receiptUrl : String?
    let log : String?

}
