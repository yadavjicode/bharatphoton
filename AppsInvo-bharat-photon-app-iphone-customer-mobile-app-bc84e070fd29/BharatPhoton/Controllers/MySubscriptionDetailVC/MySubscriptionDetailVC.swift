//
//  MySubscriptionDetailVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 16/07/24.
//

import UIKit
import SDWebImage
 
class MySubscriptionDetailVC: UIViewController {
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
    @IBOutlet weak var addBtn: UIButton!
    @IBOutlet weak var customerRepresentativeNameLbl: UILabel!
    @IBOutlet weak var resolveDateLbl: UILabel!
    @IBOutlet weak var totalPaymentLbl: UILabel!
    @IBOutlet weak var remarkLbl: UILabel!
    @IBOutlet weak var batteryVoltageLbl: UILabel!
    @IBOutlet weak var rangeLbl: UILabel!
    @IBOutlet weak var offDelayLbl: UILabel!
    @IBOutlet weak var cutOfdelayLbl: UILabel!
    @IBOutlet weak var preflushInAFLbl: UILabel!
    @IBOutlet weak var flushTimeInAFLbl: UILabel!
    @IBOutlet weak var totalWaterSavedLbl: UILabel!
    @IBOutlet weak var totalCostSavedLbl: UILabel!
    @IBOutlet weak var userCostSavedLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var addressLbl: UILabel!
    @IBOutlet weak var qtyLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var planStartDateLbl: UILabel!
    @IBOutlet weak var planExpiryDateLbl: UILabel!
    @IBOutlet weak var sinNoLbl: UILabel!
    @IBOutlet weak var serviceLogLbl: UILabel!
    @IBOutlet weak var serviceLogCollection: UICollectionView!
    var purchasedSubListingDetailModal: PurchasedSubListingDetailModal?
    var subscriptionServiceModal: ProductServiceLogModal?
    var id = ""
    var installationId = ""
    var sin = ""
    var productId = ""
    var invoiceUrl = ""
    var selectedIndex: Int = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        addBtn.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        servicePurchasedSubListingDetailAPI()
        serviceLogCollection.delegate = self
        serviceLogCollection.dataSource = self
        serviceLogCollection.register(UINib(nibName: "ServiceLogCell", bundle: nil), forCellWithReuseIdentifier: "ServiceLogCell")
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        complantTableView.delegate = self
        complantTableView.dataSource = self
        complantTableView.register(UINib(nibName: "ComplantTableCell", bundle: nil), forCellReuseIdentifier: "ComplantTableCell")
        complantTableView.separatorStyle = .none
        complantTableView.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
    }
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.complantTableView && keyPath == "contentSize" {
                complantTableHeight.constant = complantTableView.contentSize.height
            }
        }
    }
    // MARK: back swipe gesture func ==>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        if gestureRecognizer.direction == .right {
            navigationController?.popViewController(animated: true)
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
    @IBAction func addInstallationAddressBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ThanksPurchasePlanVC") as! ThanksPurchasePlanVC
        vc.orderId = purchasedSubListingDetailModal?.data?.subscriptionLisitng?._id ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func downloadInvoiceAction(_ sender: UIButton) {
        let invoiceUrl = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.invoiceUrl ?? ""
        self.downloadInvoice(invoiceUrl: invoiceUrl)
    }
    @IBAction func downloadReportAction(_ sender: UIButton) {
        self.saveAndSharePDF(from: downloadReportView)
    }
    @IBAction func registerComplantAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "RegisterComplaintVC") as! RegisterComplaintVC
        vc.productName =  purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productName ?? ""
        vc.categoryName = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.categoryName ?? ""
        vc.categoryId = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.categoryId ?? ""
        vc.productId = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productId ?? ""
        vc.sin = self.sin
        vc.purchasedSubscriptionId = purchasedSubListingDetailModal?.data?.subscriptionLisitng?._id ?? ""
        vc.type = 2
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func renewPlanAction(_ sender: UIButton) {
//        let paymentMethod = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.paymentMethod ?? ""
//        if paymentMethod == "cod"{
//            print("renew not Available")
//        }
//        else{
            let remainingDays = self.purchasedSubListingDetailModal?.data?.sinDetails?.remainingDays ?? 0
            if remainingDays <= 0{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "RenewPlanVC") as! RenewPlanVC
                vc._id = self.id
                vc.productId = self.purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productId ?? ""
                let sin = self.purchasedSubListingDetailModal?.data?.sinDetails?.sin ?? ""
                vc.sin = sin
                vc.selectedQuantity = 1
                vc.sinNoArray = [sin]
                vc.isRenewPlan = 1
                self.navigationController?.pushViewController(vc, animated: true)
            }
            else{
                alert(message: "Your plan has not expired yet, so you can't renew it now.")
           // }
        }
    }
    func saveAndSharePDF(from view: UIView) {
        let pdfData = view.exportAsPDF()
        let pdfFilename = FileManager.default.temporaryDirectory.appendingPathComponent("Report.pdf")
        
        do {
            try pdfData.write(to: pdfFilename)
            let activityVC = UIActivityViewController(activityItems: [pdfFilename], applicationActivities: nil)
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
extension MySubscriptionDetailVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return subscriptionServiceModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ComplantTableCell", for: indexPath) as! ComplantTableCell
        let remainingDays = purchasedSubListingDetailModal?.data?.sinDetails?.remainingDays ?? 0
        if remainingDays <= 0{
            cell.dayLbl.isHidden = true
            cell.warrantyExpLbl.text = "Your Warranty Has Expired"
            cell.warrantyExpLbl.textColor = .red
        }else{
            cell.dayLbl.isHidden = false
            cell.dayLbl.text = "\(remainingDays) Days"
            cell.warrantyExpLbl.text = "Your warranty will expire in"
            cell.warrantyExpLbl.textColor = UIColor(hexString: "F28D3A")
        }
        cell.resolveActionTokenLbl.text =  subscriptionServiceModal?.data?[indexPath.row].resolveActionTaken ?? "N/A"
        cell.complantNoLbl.text = subscriptionServiceModal?.data?[indexPath.row].uuid ?? ""
        cell.complantTypeLbl.text = subscriptionServiceModal?.data?[indexPath.row].complaintReason ?? ""
        cell.paymentModeLbl.text = "₹ \(subscriptionServiceModal?.data?[indexPath.row].payment ?? 0)"
        cell.resolveDateLbl.text = subscriptionServiceModal?.data?[indexPath.row].resolveDate?.toDisplayDate() ?? "N/A"
        cell.img.sd_imageIndicator = SDWebImageActivityIndicator.gray
        cell.img.sd_setImage(with: URL(string: subscriptionServiceModal?.data?[indexPath.row].profileImage ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.representativeNameLbl.text = subscriptionServiceModal?.data?[indexPath.row].customerName ?? "N/A"
        cell.remarkLbl.text = subscriptionServiceModal?.data?[indexPath.row].remark ?? "N/A"
        let firstName = subscriptionServiceModal?.data?[indexPath.row].firstName ?? "N/A"
        let lastName = subscriptionServiceModal?.data?[indexPath.row].lastName ?? ""
        cell.attendedByLbl.text = "\(firstName) \(lastName)"
        let complaintStatus = subscriptionServiceModal?.data?[indexPath.row].complaintStatus ?? 0
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
extension MySubscriptionDetailVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return purchasedSubListingDetailModal?.data?.complaintDates?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = serviceLogCollection.dequeueReusableCell(withReuseIdentifier: "ServiceLogCell", for: indexPath) as! ServiceLogCell
        cell.servicelogDateLbl.text = purchasedSubListingDetailModal?.data?.complaintDates?[indexPath.item]
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
        self.serviceSubscriptionServiceLogAPI(date: purchasedSubListingDetailModal?.data?.complaintDates?[indexPath.item] ?? "")
        selectedIndex = indexPath.item
        serviceLogCollection.reloadData()
    }
    func collectionView(_ collectionView: UICollectionView, didDeselectItemAt indexPath: IndexPath) {
        serviceLogCollection.reloadData()
    }
}
extension MySubscriptionDetailVC {
    func servicePurchasedSubListingDetailAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "purchasedSubListing?id=\(self.id)&type=0&installationId=\(self.installationId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.purchasedSubListingDetailModal = try JSONDecoder().decode(PurchasedSubListingDetailModal.self, from: JSONResponse.rawData())
                    self.serviceLogCollection.reloadData()
                    let date = self.purchasedSubListingDetailModal?.data?.complaintDates?.first ?? ""
                    self.serviceSubscriptionServiceLogAPI(date: date)
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
        let isFieldAvailable = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.isFieldAvailable ?? 0
        let isUrinalSensorAvilable = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.isUrinalSensorAvilable ?? 0
        let shouldShowViews = (isFieldAvailable == 1 || isUrinalSensorAvilable == 1)
        self.view1.isHidden = !shouldShowViews
        self.view2.isHidden = !shouldShowViews
        self.view3.isHidden = !shouldShowViews
        self.rangeSetting.isHidden = !shouldShowViews
        self.batteryVoltage.isHidden = !shouldShowViews
        if isFieldAvailable == 1 {
            self.offDelay.isHidden = false
            self.cutOfdelay.isHidden = false
            self.offDelayLbl.text = "\(purchasedSubListingDetailModal?.data?.sinDetails?.offDelay ?? 0)"
            self.cutOfdelayLbl.text = "\(purchasedSubListingDetailModal?.data?.sinDetails?.cutOffDelay ?? 0)"
            self.preflushInAF.isHidden = true
            self.flushTimeInAF.isHidden = true
        } else if isUrinalSensorAvilable == 1 {
            self.preflushInAF.isHidden = false
            self.flushTimeInAF.isHidden = false
            self.preflushInAFLbl.text = "\(purchasedSubListingDetailModal?.data?.sinDetails?.preflush ?? 0)"
            self.flushTimeInAFLbl.text = "\(purchasedSubListingDetailModal?.data?.sinDetails?.flushTime ?? 0)"
            self.offDelay.isHidden = true
            self.cutOfdelay.isHidden = true
        } else {
            self.offDelay.isHidden = true
            self.cutOfdelay.isHidden = true
            self.preflushInAF.isHidden = true
            self.flushTimeInAF.isHidden = true
        }
        let paymentMethod = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.paymentMethod ?? ""
        if paymentMethod == "cod"{
          //  self.totalPaymentLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.totalPayment ?? 0)"
            self.remarkLbl.text = self.purchasedSubListingDetailModal?.data?.sinDetails?.remark ?? "N/A"
            self.customerRepresentativeNameLbl.text = self.purchasedSubListingDetailModal?.data?.sinDetails?.customerName ?? "N/A"
            self.totalWaterSavedLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.waterSaved ?? 0) Ltrs"
            self.totalCostSavedLbl.text = "₹ \(self.purchasedSubListingDetailModal?.data?.sinDetails?.costSaved ?? 0)"
            self.userCostSavedLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.userCount ?? 0)"
            self.rangeLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.range ?? 0)"
            self.batteryVoltageLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.batteryVoltage ?? 0)"
            self.qtyLbl.text = "QTY : \(purchasedSubListingDetailModal?.data?.subscriptionLisitng?.quantity ?? 0)"
            self.titleLbl.text = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.categoryName ?? "N/A"
            self.productNameLbl.text = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productName ?? ""
            self.modalNameLbl.text = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.modelNo ?? ""
            self.planStartDateLbl.text = "Plan Start Date : \(purchasedSubListingDetailModal?.data?.sinDetails?.subscriptionStartDate?.date() ?? "")"
            self.planExpiryDateLbl.text = "Plan End Date : \(purchasedSubListingDetailModal?.data?.sinDetails?.subscriptionExpiryDate?.date() ?? "")"
            self.sinNoLbl.text = "SIN. \(purchasedSubListingDetailModal?.data?.subscriptionLisitng?.sin ?? "")"
            let productImage = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productImage ?? ""
            self.productImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
            self.productImg.sd_setImage(with: URL(string: productImage), placeholderImage: UIImage(named: "profilePlaceholder"))
            let city = self.purchasedSubListingDetailModal?.data?.installationAddress?.city ?? ""
            let state = self.purchasedSubListingDetailModal?.data?.installationAddress?.state ?? ""
            let zipCode = "\(self.purchasedSubListingDetailModal?.data?.installationAddress?.ZipCode ?? 0)"
            let address = self.purchasedSubListingDetailModal?.data?.installationAddress?.address ?? ""
            let installAddID = self.purchasedSubListingDetailModal?.data?.installationAddress?._id ?? ""
            if installAddID == ""{
                self.addBtn.isHidden = false
                self.addressLbl.text = "Address: NA"
            }
            else{
                self.addBtn.isHidden = true
                self.addressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
            }
            let complaintDates = self.purchasedSubListingDetailModal?.data?.complaintDates ?? []
            if complaintDates == []{
                serviceLogLbl.isHidden = true
                serviceLogCollection.isHidden = true
            }
            else{
                serviceLogLbl.isHidden = false
                serviceLogCollection.isHidden = false
            }
        }
        else{
            self.qtyLbl.text = "QTY : \(purchasedSubListingDetailModal?.data?.subscriptionLisitng?.quantity ?? 0)"
           // self.resolveDateLbl.text = self.purchasedSubListingDetailModal?.data?.sinDetails?.resolveDate?.toDisplayDate() ?? ""
           // self.totalPaymentLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.totalPayment ?? 0)"
            self.remarkLbl.text = self.purchasedSubListingDetailModal?.data?.sinDetails?.remark ?? "N/A"
            self.customerRepresentativeNameLbl.text = self.purchasedSubListingDetailModal?.data?.sinDetails?.customerName ?? "N/A"
            self.totalWaterSavedLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.waterSaved ?? 0) Ltrs"
            self.totalCostSavedLbl.text = "₹ \(self.purchasedSubListingDetailModal?.data?.sinDetails?.costSaved ?? 0)"
            self.userCostSavedLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.userCount ?? 0)"
            self.rangeLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.range ?? 0)"
            self.batteryVoltageLbl.text = "\(self.purchasedSubListingDetailModal?.data?.sinDetails?.batteryVoltage ?? 0)"
            let productImage = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productImage ?? ""
            self.productImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
            self.productImg.sd_setImage(with: URL(string: productImage), placeholderImage: UIImage(named: "profilePlaceholder"))
            self.titleLbl.text = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.categoryName ?? ""
            self.productNameLbl.text = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.productName ?? ""
            self.modalNameLbl.text = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.modelNo ?? ""
            self.planStartDateLbl.text = "Plan Start Date : \(purchasedSubListingDetailModal?.data?.sinDetails?.subscriptionStartDate?.date() ?? "")"
            self.planExpiryDateLbl.text = "Plan End Date : \(purchasedSubListingDetailModal?.data?.sinDetails?.subscriptionExpiryDate?.toDisplayDate() ?? "")"
            self.sinNoLbl.text = "SIN. \(purchasedSubListingDetailModal?.data?.subscriptionLisitng?.sin ?? "")"
            let city = self.purchasedSubListingDetailModal?.data?.installationAddress?.city ?? ""
            let state = self.purchasedSubListingDetailModal?.data?.installationAddress?.state ?? ""
            let zipCode = "\(self.purchasedSubListingDetailModal?.data?.installationAddress?.ZipCode ?? 0)"
            let address = self.purchasedSubListingDetailModal?.data?.installationAddress?.address ?? ""
            let installAddID = self.purchasedSubListingDetailModal?.data?.installationAddress?._id ?? ""
            if installAddID == ""{
                self.addBtn.isHidden = false
                self.addressLbl.text = "NA"
            }
            else{
                self.addBtn.isHidden = true
                self.addressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
            }
            let complaintDates = self.purchasedSubListingDetailModal?.data?.complaintDates ?? []
            if complaintDates == []{
                serviceLogLbl.isHidden = true
                serviceLogCollection.isHidden = true
            }
            else{
                serviceLogLbl.isHidden = false
                serviceLogCollection.isHidden = false
            }
//            let daysRemaining = purchasedSubListingDetailModal?.data?.subscriptionLisitng?.daysRemaining ?? 0
//            if daysRemaining <= 0 {
//                alert(message: "Plan already expired")
//            } else if daysRemaining == 1 {
//                alert(message: "Plan will expire today")
//            }
        }
    }
}
extension MySubscriptionDetailVC {
    func serviceSubscriptionServiceLogAPI(date: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "subscriptionServiceLog?id=\(self.id)&date=\(date)&sin=\(self.sin)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.subscriptionServiceModal = try JSONDecoder().decode(ProductServiceLogModal.self, from: JSONResponse.rawData())
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
