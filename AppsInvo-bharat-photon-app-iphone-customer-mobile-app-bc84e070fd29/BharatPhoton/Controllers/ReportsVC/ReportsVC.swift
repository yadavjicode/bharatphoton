//
//  ReportsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 02/07/24.
//

import UIKit
import Charts
import PDFKit

class ReportsVC: UIViewController {
    @IBOutlet weak var view1: UIView!
    @IBOutlet weak var view2: UIView!
    @IBOutlet weak var view3: UIView!
    @IBOutlet weak var view4: UIStackView!
    @IBOutlet weak var totalWaterSavedLbl: UILabel!
    @IBOutlet weak var totalUserCountLbl: UILabel!
    @IBOutlet weak var totalCostSavedLbl: UILabel!
    @IBOutlet weak var waterReportChartView: LineChartView!
    @IBOutlet weak var userCountChartView: LineChartView!
    @IBOutlet weak var costSavedChartView: LineChartView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    var reportsModal: ReportsModal?
    var months = [String]()
    var waterSaved = [Int]()
    var userCounts = [Int]()
    var costSaved = [Int]()
    var dataEntriesForWaterSaved: [ChartDataEntry] = []
    var dataEntriesForUserCount: [ChartDataEntry] = []
    var dataEntriesForSavedCost: [ChartDataEntry] = []
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceUserReportAPI()
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    // MARK: back swipe gesture func ==>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        if gestureRecognizer.direction == .right {
            navigationController?.popViewController(animated: true)
        }
    }
    func updateCartBadge() {
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    func generatePDF(from view: UIView, fileName: String) {
           // Define PDF page bounds based on the view's size
           let pdfPageBounds = CGRect(x: 0, y: 0, width: view.bounds.width, height: view.bounds.height)
           
           // Temporary file path for the PDF
           let tempPDFPath = NSTemporaryDirectory().appending(fileName)
           let tempPDFURL = URL(fileURLWithPath: tempPDFPath)
           
           // Create PDF context
           UIGraphicsBeginPDFContextToFile(tempPDFPath, pdfPageBounds, nil)
           guard let pdfContext = UIGraphicsGetCurrentContext() else { return }

           // Start a new PDF page and render the view
           UIGraphicsBeginPDFPage()
           view.layer.render(in: pdfContext)
           UIGraphicsEndPDFContext()

           // Share the generated PDF
           sharePDF(url: tempPDFURL)
       }
       func sharePDF(url: URL) {
           let activityViewController = UIActivityViewController(activityItems: [url], applicationActivities: nil)
           self.present(activityViewController, animated: true, completion: nil)
       }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func waterReportDownloadAction(_ sender: UIButton) {
        generatePDF(from: view1, fileName: "WaterReport.pdf")
    }
    @IBAction func userCountReportDownloadAction(_ sender: UIButton) {
        generatePDF(from: view2, fileName: "UserCountReport.pdf")
    }
    @IBAction func costSavedReportDownloadAction(_ sender: UIButton) {
        generatePDF(from: view3, fileName: "CostSavedReport.pdf")
    }
    @IBAction func downloadAllReportAction(_ sender: UIButton) {
        generatePDF(from: view4, fileName: "AllReports.pdf")
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
}
extension ReportsVC {
    func serviceUserReportAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "userReport", params: parms, token: token) { JSONResponse in
            let code = JSONResponse["code"].intValue
            LoaderManager.shared.hideLoader()
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.reportsModal = try JSONDecoder().decode(ReportsModal.self, from: JSONResponse.rawData())
                    self.setData()
                    self.waterSavedReports()
                    self.userCountReport()
                    self.costReports()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
                break
            }
        }failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
    func setData(){
        self.totalWaterSavedLbl.text = "\(reportsModal?.data?.waterSavedReports?.totalWaterSavedForYear ?? 0) Ltr"
        self.totalCostSavedLbl.text = "\(reportsModal?.data?.costReport?.totalCostSavedForYear ?? 0)"
        self.totalUserCountLbl.text = "\(reportsModal?.data?.userCountReport?.totalUserCountForYear ?? 0)"
    }
    func waterSavedReports(){
        if let monthlyWaterSaved = reportsModal?.data?.waterSavedReports?.monthlyWaterSaved {
            for report in monthlyWaterSaved {
                months.append(report.month ?? "")
                waterSaved.append(report.totalWaterSaved ?? 0)
                // MARK: 1. Set data for water report chart
                for (index, savedWater) in waterSaved.enumerated() {
                    let dataEntry = ChartDataEntry(x: Double(index), y: Double(savedWater))
                    dataEntriesForWaterSaved.append(dataEntry)
                }
                let waterSavedDataSet = LineChartDataSet(entries: dataEntriesForWaterSaved, label: "Water Saved")
                waterSavedDataSet.colors = [NSUIColor.blue]
                waterSavedDataSet.valueColors = [NSUIColor.clear]
                waterSavedDataSet.circleColors = [NSUIColor.blue]
                waterSavedDataSet.circleRadius = 4.0
                waterSavedDataSet.drawFilledEnabled = true
                waterSavedDataSet.fillColor = NSUIColor.blue
                let waterSavedData = LineChartData(dataSet: waterSavedDataSet)
                waterReportChartView.data = waterSavedData
                waterReportChartView.xAxis.valueFormatter = IndexAxisValueFormatter(values: months)
                waterReportChartView.xAxis.labelPosition = .bottom
                waterReportChartView.leftAxis.axisMinimum = 0
                waterReportChartView.rightAxis.enabled = false
                waterReportChartView.setScaleEnabled(true)
                waterReportChartView.dragEnabled = true
                waterReportChartView.animate(xAxisDuration: 1.0, yAxisDuration: 1.0)
            }
        } else {
            // Handle the case where the data is not available or is nil
            print("Water saved data is unavailable.")
        }
    }
    func userCountReport(){
        if let monthlyUserCount = reportsModal?.data?.userCountReport?.monthlyUserCount {
            for report in monthlyUserCount {
                months.append(report.month ?? "")
                userCounts.append(report.totalUserCount ?? 0)
            }
            // MARK: 2. Set data for user count chart ==>
            for (index, userCount) in userCounts.enumerated() {
                let dataEntry = ChartDataEntry(x: Double(index), y: Double(userCount))
                dataEntriesForUserCount.append(dataEntry)
            }
            let userCountDataSet = LineChartDataSet(entries: dataEntriesForUserCount, label: "User Count")
            userCountDataSet.colors = [NSUIColor.systemRed]
            userCountDataSet.valueColors = [NSUIColor.clear]
            userCountDataSet.circleColors = [NSUIColor.systemRed]
            userCountDataSet.circleRadius = 4.0
            userCountDataSet.drawFilledEnabled = true
            userCountDataSet.fillColor = NSUIColor.systemRed
            let userCountData = LineChartData(dataSet: userCountDataSet)
            userCountChartView.data = userCountData
            userCountChartView.xAxis.valueFormatter = IndexAxisValueFormatter(values: months)
            userCountChartView.xAxis.labelPosition = .bottom
            userCountChartView.leftAxis.axisMinimum = 0
            userCountChartView.rightAxis.enabled = false
            userCountChartView.setScaleEnabled(true)
            userCountChartView.dragEnabled = true
            userCountChartView.animate(xAxisDuration: 1.0, yAxisDuration: 1.0)
        } else {
            print("userCount data is unavailable.")
        }
    }
    func costReports(){
        if let monthlyCostSaved = reportsModal?.data?.costReport?.monthlyCostSaved {
            for report in monthlyCostSaved {
                months.append(report.month ?? "")
                costSaved.append(report.totalCostSaved ?? 0)
            }
            // MARK: 3. Set data for cost saved chart ==>
            for (index, savedCost) in costSaved.enumerated() {
                let dataEntry = ChartDataEntry(x: Double(index), y: Double(savedCost))
                dataEntriesForSavedCost.append(dataEntry)
            }
            let savedCostDataSet = LineChartDataSet(entries: dataEntriesForSavedCost, label: "Saved Cost")
            savedCostDataSet.colors = [NSUIColor.systemPurple]
            savedCostDataSet.valueColors = [NSUIColor.clear]
            savedCostDataSet.circleColors = [NSUIColor.systemPurple]
            savedCostDataSet.circleRadius = 4.0
            savedCostDataSet.drawFilledEnabled = true
            savedCostDataSet.fillColor = NSUIColor.systemPurple
            let savedCostData = LineChartData(dataSet: savedCostDataSet)
            costSavedChartView.data = savedCostData
            costSavedChartView.xAxis.valueFormatter = IndexAxisValueFormatter(values: months)
            costSavedChartView.xAxis.labelPosition = .bottom
            costSavedChartView.leftAxis.axisMinimum = 0
            costSavedChartView.rightAxis.enabled = false
            costSavedChartView.setScaleEnabled(true)
            costSavedChartView.dragEnabled = true
            costSavedChartView.animate(xAxisDuration: 1.0, yAxisDuration: 1.0)
        } else {
            print("costReport data is unavailable.")
        }
    }
}

