//
//  CustomerSupportComplaintHistoryVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 04/07/24.
//

import UIKit
 

class CustomerSupportComplaintHistoryVC: UIViewController {
    @IBOutlet weak var horizontalScrollView: UIScrollView!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var complantHistoryTable: UITableView!
    var complantListModal: ComplantListModal?
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        let offset = CGPoint(x: 300, y: 0)
        horizontalScrollView.setContentOffset(offset, animated: true)
        serviceComplantListingAPI()
        updateCartBadge()
        updateNotificationBadge()
        complantHistoryTable.delegate = self
        complantHistoryTable.dataSource = self
        complantHistoryTable.register(UINib(nibName: "ComplantHistoryCell", bundle: nil), forCellReuseIdentifier: "ComplantHistoryCell")
        complantHistoryTable.separatorStyle = .none
    }
    func updateCartBadge() {
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
            
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popToRootViewController(animated: true)
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
    @IBAction func registerComplantBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportRegisterComplantVC") as! CustomerSupportRegisterComplantVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func customerCareBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportCustomerCareVC") as! CustomerSupportCustomerCareVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func troubleSootBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportTroubleShootingVideoVC") as! CustomerSupportTroubleShootingVideoVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
}
extension CustomerSupportComplaintHistoryVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return complantListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = complantHistoryTable.dequeueReusableCell(withIdentifier: "ComplantHistoryCell", for: indexPath) as! ComplantHistoryCell
        cell.productImg.sd_setImage(with: URL(string: complantListModal?.data?[indexPath.row].productImage?.first?.url ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.productCategoryNameLbl.text = complantListModal?.data?[indexPath.row].categoryName ?? ""
        cell.productNameLbl.text = complantListModal?.data?[indexPath.row].productName ?? ""
        cell.modalNameLbl.text = complantListModal?.data?[indexPath.row].modelNo ?? ""
        cell.sinLbl.text = complantListModal?.data?[indexPath.row].sin ?? ""
        cell.dateLbl.text = complantListModal?.data?[indexPath.row].createdAt?.dateAndTime() ?? ""
        let complaintReason = complantListModal?.data?[indexPath.row].complaintReason ?? ""
        if complaintReason == "other"{
            cell.complantIssueLbl.text = complantListModal?.data?[indexPath.row].description ?? ""
        }
        else if complaintReason == "Other"{
            cell.complantIssueLbl.text = complantListModal?.data?[indexPath.row].description ?? ""
        }
        else{
            cell.complantIssueLbl.text = complantListModal?.data?[indexPath.row].complaintReason ?? ""
        }
        let complaintStatus = complantListModal?.data?[indexPath.row].complaintStatus ?? 0
        if complaintStatus == 0{
            cell.complantStatusLbl.text = "Pending"
            cell.complantStatusLbl.textColor = UIColor(hexString: "1B6AC6")
        }
        else if complaintStatus == 1{
            cell.complantStatusLbl.text = "In Processed"
            cell.complantStatusLbl.textColor = .systemGreen
        }
        else if complaintStatus == 2{
            cell.complantStatusLbl.text = "Completed"
            cell.complantStatusLbl.textColor = .systemGreen
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return complantHistoryTable.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ComplaintHistoryDetailVC") as! ComplaintHistoryDetailVC
        vc.id = complantListModal?.data?[indexPath.row]._id ?? ""
        vc.categoryName = complantListModal?.data?[indexPath.row].categoryName ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension CustomerSupportComplaintHistoryVC {
    func serviceComplantListingAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "complaintListing", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.complantListModal = try JSONDecoder().decode(ComplantListModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.complantListModal?.data?.count ?? 0) == 0 ? false : true
                    self.complantHistoryTable.reloadData()
                    
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
