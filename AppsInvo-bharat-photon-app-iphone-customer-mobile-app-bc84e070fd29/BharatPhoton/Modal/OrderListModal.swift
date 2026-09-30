//
//  OrderListModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 17/10/24.
//

import Foundation


// OrderListModal
struct OrderListModal : Codable {
    let code : Int?
    let message : String?
    var data : [OrderListData]?
    let count : Int?
}
struct OrderListData : Codable {
    let _id : String?
    let uuid : String?
    let userId : String?
    let cartId : String?
    let currency : String?
    let paymentMethod : String?
    let subTotal : Int?
    let taxAmount : Double?
    let discountedAmount : Double?
    let finalTotalAmount : Double?
    let invoiceUrl : String?
    let finalOrderStatus : Int?
    let addressId : String?
    let createdAt : String?
    let products : [OrderListProducts]?
    let itemCount : Int?
    let isSitePhotoUploaded : Int?

}
struct OrderListProducts : Codable {
    let _id : String?
    let productId : String?
    let modelNo : String?
    let product_name : String?
    let image : String?
    let description : String?
    let quantity : Int?
    let price : Int?
    let totalPrice : Int?
    let orderStatus : Int?
    let isSitePhotoUploaded : Int?
    let finNo : String?
    let installationPincode : Int?
    let installationPrice : Int?
    let installationStatus : Int?
    let staffId : String?
    let uuid : String?
    let createdAt : String?
    let installationAddressId : String?
    let assignDate : String?
    let pendingProductDate : String?
    let processedProductDate : String?
    let dispatchedProductDate : String?
    let deliveredProductDate : String?
    

}
