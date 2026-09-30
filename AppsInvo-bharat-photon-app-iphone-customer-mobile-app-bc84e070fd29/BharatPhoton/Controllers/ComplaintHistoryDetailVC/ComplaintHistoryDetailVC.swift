//
//  ComplaintHistoryDetailVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 15/07/24.
//

import UIKit
import SDWebImage

class ComplaintHistoryDetailVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var complantDateLbl: UILabel!
    @IBOutlet weak var categoryNameLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var sinLbl: UILabel!
    @IBOutlet weak var complantNoLbl: UILabel!
    @IBOutlet weak var complantTypeLbl: UILabel!
    @IBOutlet weak var resolveDateLbl: UILabel!
    @IBOutlet weak var staffNameLbl: UILabel!
    @IBOutlet weak var staffImg: UIImageView!
    @IBOutlet weak var totalPaymentLbl: UILabel!
    @IBOutlet weak var complaintStatusLbl: UILabel!
    @IBOutlet weak var resolveActionTokenLbl: UILabel!
    @IBOutlet weak var remarkLbl: UILabel!
    @IBOutlet weak var customerNameLbl: UILabel!
    var complantListDetailModal: ComplantListDetailModal?
    var id = ""
    var categoryName = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        categoryNameLbl.text = categoryName
        serviceComplantHistoryDetailAPI()
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
    func updateCartBadge(){
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: false)
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension ComplaintHistoryDetailVC {
    func serviceComplantHistoryDetailAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "complaintListing?id=\(self.id)", params: parms, token: token) { JSONResponse in
            print(JSONResponse)
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.complantListDetailModal = try JSONDecoder().decode(ComplantListDetailModal.self, from: JSONResponse.rawData())
                   // self.msgLbl.isHidden = (self.complantListModal?.data?.count ?? 0) == 0 ? false : true
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
        let productImage = complantListDetailModal?.data?.complaintDetails?.productImage?.first?.url ?? ""
        self.productImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
        self.productImg.sd_setImage(with: URL(string: productImage), placeholderImage: UIImage(named: "profilePlaceholder"))
        self.complantDateLbl.text = complantListDetailModal?.data?.complaintDetails?.createdAt?.toDisplayDate() ?? ""
        self.complantNoLbl.text = complantListDetailModal?.data?.complaintDetails?.uuid ?? ""
        self.productNameLbl.text = complantListDetailModal?.data?.complaintDetails?.productName ?? ""
        self.modalNameLbl.text = complantListDetailModal?.data?.complaintDetails?.modelNo ?? ""
        self.sinLbl.text = "SIN: \(complantListDetailModal?.data?.complaintDetails?.sin ?? "")"
        let firstName = complantListDetailModal?.data?.complaintDetails?.staffFirstName ?? ""
        let lastName = complantListDetailModal?.data?.complaintDetails?.staffLastName ?? ""
        self.staffNameLbl.text = "\(firstName) \(lastName)"
        let staffImage = complantListDetailModal?.data?.complaintDetails?.staffImage ?? ""
        self.staffImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
        self.staffImg.sd_setImage(with: URL(string: staffImage), placeholderImage: UIImage(named: "profilePlaceholder"))
        self.totalPaymentLbl.text = "\(complantListDetailModal?.data?.resolvedDetails?.totalPayment ?? 0)"
        self.remarkLbl.text = complantListDetailModal?.data?.resolvedDetails?.remark ?? "NA"
        self.customerNameLbl.text = complantListDetailModal?.data?.resolvedDetails?.customerName ?? "NA"
        self.resolveActionTokenLbl.text = complantListDetailModal?.data?.resolvedDetails?.resolveAction ?? "NA"
        self.resolveDateLbl.text = complantListDetailModal?.data?.resolvedDetails?.resolveDate?.toDisplayDate() ?? "NA"
        let complaintReason = complantListDetailModal?.data?.complaintDetails?.complaintReason ?? ""
        if complaintReason == "other"{
            complantTypeLbl.text = complantListDetailModal?.data?.complaintDetails?.description ?? ""
        }
        else if complaintReason == "Other"{
            complantTypeLbl.text = complantListDetailModal?.data?.complaintDetails?.description ?? ""
        }
        else{
            complantTypeLbl.text = complantListDetailModal?.data?.complaintDetails?.complaintReason ?? ""
        }
        let complaintStatus = complantListDetailModal?.data?.complaintDetails?.complaintStatus ?? 0
        if complaintStatus == 0{
            self.complaintStatusLbl.text = "Pending"
            self.complaintStatusLbl.textColor = UIColor(hexString: "1B6AC6")
        }
        else if complaintStatus == 1{
            self.complaintStatusLbl.text = "Processed"
            self.complaintStatusLbl.textColor = UIColor(hexString: "1B6AC6")
        }
        else if complaintStatus == 2{
            self.complaintStatusLbl.text = "Resolved"
            self.complaintStatusLbl.textColor = UIColor(hexString: "F28D3A")
        }
    }
}

