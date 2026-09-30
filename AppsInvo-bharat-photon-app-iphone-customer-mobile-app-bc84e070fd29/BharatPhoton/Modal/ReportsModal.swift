//
//  ReportModal.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 09/01/25.
//

import Foundation

struct ReportsModal : Codable {
    let code : Int?
    let message : String?
    let data : ReportsData?
}
struct ReportsData : Codable {
    let waterSavedReports : WaterSavedReports?
    let costReport : CostReport?
    let userCountReport : UserCountReport?
}
struct WaterSavedReports : Codable {
    let monthlyWaterSaved : [MonthlyWaterSaved]?
    let totalWaterSavedForYear : Int?
}
struct MonthlyWaterSaved : Codable {
    let month : String?
    let totalWaterSaved : Int?
}
struct CostReport : Codable {
    let monthlyCostSaved : [MonthlyCostSaved]?
    let totalCostSavedForYear : Int?
}
struct MonthlyCostSaved : Codable {
    let month : String?
    let totalCostSaved : Int?
}
struct UserCountReport : Codable {
    let monthlyUserCount : [MonthlyUserCount]?
    let totalUserCountForYear : Int?
}
struct MonthlyUserCount : Codable {
    let month : String?
    let totalUserCount : Int?

}

