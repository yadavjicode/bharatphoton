//
//  SinDetailsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 27/06/24.
//

import UIKit
import SDWebImage
import PDFKit

class SinDetailsVC: UIViewController {
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var complantTableHeight: NSLayoutConstraint!
    @IBOutlet weak var complantTableView: UITableView!
    @IBOutlet weak var downloadReportView: UIView!
    @IBOutlet weak var view1: UIView!
    @IBOutlet weak var view2: UIView!
    @IBOutlet weak var view3: UIView!
    @IBOutlet weak var rangeSetting: UIView!
    @IBOutlet weak var offDelay: UIView!
    @IBOutlet weak var cutOfdelay: UIView!
    @IBOutlet weak var preflushInAF: UIView!
    @IBOutlet weak var flushTimeInAF: UIView!
    @IBOutlet weak var batteryVoltage: UIView!
    @IBOutlet weak var resolveDateLbl: UILabel!
    @IBOutlet weak var totalPaymentLbl: UILabel!
    @IBOutlet weak var totalWaterSavedLbl: UILabel!
    @IBOutlet weak var totalCostSavedLbl: UILabel!
    @IBOutlet weak var userCostSavedLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var planStartDateLbl: UILabel!
    @IBOutlet weak var planExpiryDateLbl: UILabel!
    @IBOutlet weak var sinNoLbl: UILabel!
    @IBOutlet weak var customerRepresentativeNameLbl: UILabel!
    @IBOutlet weak var remarkLbl: UILabel!
    @IBOutlet weak var batteryVoltageLbl: UILabel!
    @IBOutlet weak var rangeLbl: UILabel!
    @IBOutlet weak var offDelayLbl: UILabel!
    @IBOutlet weak var cutOfdelayLbl: UILabel!
    @IBOutlet weak var preflushInAFLbl: UILabel!
    @IBOutlet weak var flushTimeInAFLbl: UILabel!
    @IBOutlet weak var serviceLogLbl: UILabel!
    @IBOutlet weak var serviceLogCollection: UICollectionView!
    var sinDetailsModal: SinDetailsModal?
    var productServiceLogModal: ProductServiceLogModal?
    var orderId = ""
    var productId = ""
    var sin = ""
    var installationId = ""
    var selectedIndex: Int = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        scrollView.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        serviceSinDetailAPI()
        serviceLogCollection.delegate = self
        serviceLogCollection.dataSource = self
        serviceLogCollection.register(UINib(nibName: "ServiceLogCell", bundle: nil), forCellWithReuseIdentifier: "ServiceLogCell")
        complantTableView.delegate = self
        complantTableView.dataSource = self
        complantTableView.register(UINib(nibName: "ComplantTableCell", bundle: nil), forCellReuseIdentifier: "ComplantTableCell")
        complantTableView.separatorStyle = .none
        complantTableView.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
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
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.complantTableView && keyPath == "contentSize" {
                complantTableHeight.constant = complantTableView.contentSize.height
            }
        }
    }
    func updateCartBadge(){
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
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
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    @IBAction func downloadReportAction(_ sender: UIButton) {
        self.saveAndSharePDF(from: downloadReportView)
    }
    @IBAction func registerComplantAction(_ sender: UIButton) {
        let installatedDetails = sinDetailsModal?.data?.installatedDetails?._id ?? ""
        if installatedDetails == ""{
            print("SinDetails Not found")
        }
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "RegisterComplaintVC") as! RegisterComplaintVC
        vc.productName = self.sinDetailsModal?.data?.installatedDetails?.productName ?? ""
        vc.productId = self.sinDetailsModal?.data?.installatedDetails?.productId ?? ""
        vc.categoryName = self.sinDetailsModal?.data?.installatedDetails?.categoryName ?? ""
        vc.categoryId = self.sinDetailsModal?.data?.installatedDetails?.categoryId ?? ""
        vc.orderId = self.sinDetailsModal?.data?.installatedDetails?.orderId ?? ""
        vc.sin = self.sin
        vc.type = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func renewWarrantyAction(_ sender: UIButton) {
        let installatedDetails = sinDetailsModal?.data?.installatedDetails?._id ?? ""
        if installatedDetails == ""{
            print("SinDetails Not found")
        }
        else{
            let remainingWarrantyDays = sinDetailsModal?.data?.installatedDetails?.remainingWarrantyDays ?? 0
            if  remainingWarrantyDays <= 0{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "WarrantyPlanVC") as! WarrantyPlanVC
                vc.productId = sinDetailsModal?.data?.installatedDetails?.productId ?? ""
                vc.orderId = sinDetailsModal?.data?.installatedDetails?.orderId ?? ""
                let finNo = sinDetailsModal?.data?.installatedDetails?.finNo ?? ""
                vc.selectedFinNo = [finNo]
                vc.selectedQuantity = 1
                self.navigationController?.pushViewController(vc, animated: true)
            }
            else{
                alert(message: "Your product warranty expire in \(remainingWarrantyDays) days")
            }
        }
    }
    func saveAndSharePDF(from view: UIView) {
        let pdfData = view.exportAsPDF()
        let pdfFilename = FileManager.default.temporaryDirectory.appendingPathComponent("Report.pdf")
        
        do {
            try pdfData.write(to: pdfFilename)
            let activityVC = UIActivityViewController(activityItems: [pdfFilename], applicationActivities: nil)
            
            // Find the top view controller in the current active window
            if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let keyWindow = windowScene.windows.first(where: { $0.isKeyWindow }),
               let topVC = keyWindow.rootViewController {
                
                topVC.present(activityVC, animated: true, completion: nil)
            }
        } catch {
            print("Failed to save PDF: \(error.localizedDescription)")
        }
    }
}
extension UIView {
    func exportAsPDF() -> Data {
        let pdfPageFrame = self.bounds
        let pdfData = NSMutableData()
        UIGraphicsBeginPDFContextToData(pdfData, pdfPageFrame, nil)
        UIGraphicsBeginPDFPageWithInfo(pdfPageFrame, nil)
        guard let pdfContext = UIGraphicsGetCurrentContext() else { return Data() }
        self.layer.render(in: pdfContext)
        UIGraphicsEndPDFContext()
        return pdfData as Data
    }
   
}
extension SinDetailsVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return productServiceLogModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ComplantTableCell", for: indexPath) as! ComplantTableCell
        let remainingWarrantyDays = sinDetailsModal?.data?.installatedDetails?.remainingWarrantyDays ?? 0
        if remainingWarrantyDays <= 0{
            cell.dayLbl.isHidden = true
            cell.warrantyExpLbl.text = "Your Warranty Has Expired"
            cell.warrantyExpLbl.textColor = .red
        }else{
            cell.dayLbl.isHidden = false
            cell.dayLbl.text = "\(remainingWarrantyDays) Days"
            cell.warrantyExpLbl.text = "Your warranty will expire in"
            cell.warrantyExpLbl.textColor = UIColor(hexString: "F28D3A")
        }
//        let remainingWarrantyDays = sinDetailsModal?.data?.installatedDetails?.remainingWarrantyDays ?? 0
//        cell.dayLbl.text = "\(remainingWarrantyDays) Days"
        cell.resolveActionTokenLbl.text =  productServiceLogModal?.data?[indexPath.row].resolveActionTaken ?? "N/A"
        cell.complantNoLbl.text = productServiceLogModal?.data?[indexPath.row].uuid ?? ""
        cell.complantTypeLbl.text = productServiceLogModal?.data?[indexPath.row].complaintReason ?? ""
        cell.paymentModeLbl.text = "₹ \(productServiceLogModal?.data?[indexPath.row].payment ?? 0)"
        cell.resolveDateLbl.text = productServiceLogModal?.data?[indexPath.row].resolveDate?.toDisplayDate() ?? "N/A"
        cell.img.sd_imageIndicator = SDWebImageActivityIndicator.gray
        cell.img.sd_setImage(with: URL(string: productServiceLogModal?.data?[indexPath.row].profileImage ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.representativeNameLbl.text = productServiceLogModal?.data?[indexPath.row].customerName ?? "N/A"
        cell.remarkLbl.text = productServiceLogModal?.data?[indexPath.row].remark ?? "N/A"
        let firstName = productServiceLogModal?.data?[indexPath.row].firstName ?? "N/A"
        let lastName = productServiceLogModal?.data?[indexPath.row].lastName ?? ""
        cell.attendedByLbl.text = "\(firstName) \(lastName)"
        let complaintStatus = productServiceLogModal?.data?[indexPath.row].complaintStatus ?? 0
        if complaintStatus == 0{
            cell.complantStatusLbl.text = "Pending"
            cell.complantStatusLbl.textColor = .red
        }
        else{
            cell.complantStatusLbl.text = "Completed"
            cell.complantStatusLbl.textColor = .systemBlue
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 440
    }
}
extension SinDetailsVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return sinDetailsModal?.data?.complaintDates?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = serviceLogCollection.dequeueReusableCell(withReuseIdentifier: "ServiceLogCell", for: indexPath) as! ServiceLogCell
        cell.servicelogDateLbl.text = sinDetailsModal?.data?.complaintDates?[indexPath.item]
        if indexPath.item == selectedIndex {
            cell.backView.backgroundColor = UIColor(hexString: "FF8D3A")
            cell.backView.borderColor = UIColor(hexString: "FF8D3A")
            cell.servicelogDateLbl.textColor = .white
        } else {
            cell.backView.backgroundColor = .white
            cell.backView.borderColor = .black
            cell.servicelogDateLbl.textColor = .black
        }
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (self.serviceLogCollection.bounds.width/2.8)-32, height: 40)
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        self.serviceProductServiceLogAPI(date: sinDetailsModal?.data?.complaintDates?[indexPath.item] ?? "")
        selectedIndex = indexPath.item
        serviceLogCollection.reloadData()
    }
    func collectionView(_ collectionView: UICollectionView, didDeselectItemAt indexPath: IndexPath) {
        serviceLogCollection.reloadData()
    }
}
extension SinDetailsVC {
    func serviceSinDetailAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "sinDetails?orderId=\(self.orderId)&productId=\(self.productId)&installationId=\(self.installationId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.sinDetailsModal = try JSONDecoder().decode(SinDetailsModal.self, from: JSONResponse.rawData())
                    self.serviceLogCollection.reloadData()
                    self.sin = self.sinDetailsModal?.data?.installatedDetails?.sin ?? ""
                    let date = self.sinDetailsModal?.data?.complaintDates?.first ?? ""
                    self.serviceProductServiceLogAPI(date: date)
                    self.setData()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
    func setData(){
        scrollView.isHidden = false
        let isFieldAvailable = sinDetailsModal?.data?.installatedDetails?.isFieldAvailable ?? 0
        let isUrinalSensorAvilable = sinDetailsModal?.data?.installatedDetails?.isUrinalSensorAvilable ?? 0
        let shouldShowViews = (isFieldAvailable == 1 || isUrinalSensorAvilable == 1)
        self.view1.isHidden = !shouldShowViews
        self.view2.isHidden = !shouldShowViews
        self.view3.isHidden = !shouldShowViews
        self.rangeSetting.isHidden = !shouldShowViews
        self.batteryVoltage.isHidden = !shouldShowViews
        if isFieldAvailable == 1 {
            self.offDelay.isHidden = false
            self.cutOfdelay.isHidden = false
            self.offDelayLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.offDelay ?? 0)"
            self.cutOfdelayLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.cutOffDelay ?? 0)"

            self.preflushInAF.isHidden = true
            self.flushTimeInAF.isHidden = true
        } else if isUrinalSensorAvilable == 1 {
            self.preflushInAF.isHidden = false
            self.flushTimeInAF.isHidden = false
            self.preflushInAFLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.preflush ?? 0)"
            self.flushTimeInAFLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.flushTime ?? 0)"

            self.offDelay.isHidden = true
            self.cutOfdelay.isHidden = true
        } else {
            // Default case if neither is available
            self.offDelay.isHidden = true
            self.cutOfdelay.isHidden = true
            self.preflushInAF.isHidden = true
            self.flushTimeInAF.isHidden = true
        }
        self.productImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
        self.productImg.sd_setImage(with: URL(string: sinDetailsModal?.data?.installatedDetails?.productImage?.first?.url ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        self.titleLbl.text = sinDetailsModal?.data?.installatedDetails?.categoryName ?? ""
        self.productNameLbl.text = sinDetailsModal?.data?.installatedDetails?.productName ?? ""
        self.modalNameLbl.text = sinDetailsModal?.data?.installatedDetails?.modelNo ?? ""
        self.planStartDateLbl.text = "Warranty Start Date : \(sinDetailsModal?.data?.installatedDetails?.warrentyStartDate?.toDisplayDate() ?? "")"
        self.planExpiryDateLbl.text = "Warranty End Date : \(sinDetailsModal?.data?.installatedDetails?.warrentyEndDate?.toDisplayDate() ?? "")"
        self.totalWaterSavedLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.waterSaved ?? 0) Ltrs"
        self.totalCostSavedLbl.text = "₹ \(sinDetailsModal?.data?.installatedDetails?.costSaved ?? 0)"
        self.userCostSavedLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.userCount ?? 0)"
        self.remarkLbl.text = self.sinDetailsModal?.data?.installatedDetails?.remark ?? ""
//        self.resolveDateLbl.text = self.sinDetailsModal?.data?.installatedDetails?.resolveDate?.toDisplayDate() ?? ""
//        self.totalPaymentLbl.text = "\(self.sinDetailsModal?.data?.installatedDetails?.totalPayment ?? 0)"
        self.customerRepresentativeNameLbl.text = self.sinDetailsModal?.data?.installatedDetails?.customerName ?? ""
        self.rangeLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.range ?? 0)"
        self.batteryVoltageLbl.text = "\(sinDetailsModal?.data?.installatedDetails?.batteryVoltage ?? 0)"
        self.sinNoLbl.text = "SIN. \(sinDetailsModal?.data?.installatedDetails?.sin ?? "")"
        let complaintDates = sinDetailsModal?.data?.complaintDates ?? []
        if complaintDates == []{
            serviceLogLbl.isHidden = true
            serviceLogCollection.isHidden = true
        }
        else{
            serviceLogLbl.isHidden = false
            serviceLogCollection.isHidden = false
        }
    }
}
extension SinDetailsVC {
    func serviceProductServiceLogAPI(date: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productServiceLog?orderId=\(self.orderId)&productId=\(self.productId)&date=\(date)&sin=\(self.sin)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productServiceLogModal = try JSONDecoder().decode(ProductServiceLogModal.self, from: JSONResponse.rawData())
                    self.complantTableView.reloadData()
                    self.setData()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
