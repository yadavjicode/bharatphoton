//
//  RenewSubscriptionVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 19/02/25.
//

import UIKit
import SDWebImage
 
class RenewSubscriptionVC: UIViewController {
    @IBOutlet weak var alertPopup: UIView!
    @IBOutlet weak var alertPopupMsgLbl: UILabel!
    @IBOutlet weak var renewSubscriptionBtn: UIButton!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var reSubscriptionTable: UITableView!
    var renewSubscriptionModal: RenewSubscriptionModal?
    var selectedIndexes: [Int] = []
    var sinNoArray: [String] = []
    var orderId = ""
    var sin = ""
    var productId = ""
    var currentPage = 1
    var isLoading = false
    var hasMoreData = true
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        msgLbl.text = "No plan expired till now!"
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        renewSubscriptionBtn.isHidden = true
        alertPopup.isHidden = true
        msgLbl.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        serviceRenewSubscriptionListingAPI(page: currentPage)
        reSubscriptionTable.delegate = self
        reSubscriptionTable.dataSource = self
        reSubscriptionTable.register(UINib(nibName: "RenewSubscriptioPlanCell", bundle: nil), forCellReuseIdentifier: "RenewSubscriptioPlanCell")
        reSubscriptionTable.separatorStyle = .none
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
    @IBAction func renewSubscriptionBtnAction(_ sender: UIButton) {
        guard !selectedIndexes.isEmpty else {
            print("No item selected")
            return
        }
        // Get remainingDays for selected rows
        let selectedRemainingDays = selectedIndexes.compactMap {
            renewSubscriptionModal?.data?[$0].remainingDays
        }
        // Check if any selected item has remainingDays > 0 (still active)
        if selectedRemainingDays.contains(where: { $0 > 0 }) {
            alertPopup.isHidden = false
            alertPopupMsgLbl.text = "Only expired items can be selected for this Order ID"
            return
        }
        // Get subscriptionUUIDs for selected rows
        let selectedUUIDs = selectedIndexes.compactMap {
            renewSubscriptionModal?.data?[$0].subscriptionuuid
        }
        // Check if all selected UUIDs are the same
        if Set(selectedUUIDs).count == 1 {
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "RenewPlanVC") as! RenewPlanVC
            vc._id = self.orderId
            vc.sin = self.sin
            vc.productId = self.productId
            vc.isRenewPlan = 1
            vc.selectedQuantity = selectedIndexes.count
            vc.sinNoArray = self.sinNoArray
            self.navigationController?.pushViewController(vc, animated: true)
        } else {
            alertPopup.isHidden = false
            alertPopupMsgLbl.text = "You can only select items from the same Order ID."
        }
    }
    @IBAction func okayBtnAction(_ sender: UIButton) {
        alertPopup.isHidden = true
    }
    @IBAction func hidePopUpBtnAction(_ sender: UIButton) {
        alertPopup.isHidden = true
    }
}
extension RenewSubscriptionVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return renewSubscriptionModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = reSubscriptionTable.dequeueReusableCell(withIdentifier: "RenewSubscriptioPlanCell", for: indexPath) as! RenewSubscriptioPlanCell
        let image = self.renewSubscriptionModal?.data?[indexPath.row].productImage?.first?.url ?? ""
        cell.productImage.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.productNameLbl.text = renewSubscriptionModal?.data?[indexPath.row].productName ?? ""
        cell.modelNoLbl.text = "Modal No : \(renewSubscriptionModal?.data?[indexPath.row].modelNo ?? "")"
        cell.uuidLbl.text = "Order Id :\(renewSubscriptionModal?.data?[indexPath.row].subscriptionuuid ?? "")"
        cell.sinNoLbl.text = "Sin :\(renewSubscriptionModal?.data?[indexPath.row].sin ?? "")"
        
        cell.renewSubscriptionBtn.tag = indexPath.row
        cell.renewSubscriptionBtn.addTarget(self, action: #selector(renewSubscriptionBtnTapped(sender:)), for: .touchUpInside)
        let remainingDays = renewSubscriptionModal?.data?[indexPath.row].remainingDays ?? 0
        if remainingDays <= 0{
            cell.remainingSubscriptionDaysLbl.text = "Expired"
            cell.remainingSubscriptionDaysLbl.textColor = .red
            // cell.renewSubscriptionLbl.textColor = UIColor(hexString: "FF8D3A")
            // cell.renewSubscriptionBtn.isHidden = false
        }
        else{
            cell.remainingSubscriptionDaysLbl.text = "Expiring in: \(renewSubscriptionModal?.data?[indexPath.row].remainingDays ?? 0) Days"
            cell.remainingSubscriptionDaysLbl.textColor = .black
            // cell.renewSubscriptionLbl.textColor = UIColor(hexString: "F2C493")
            // cell.renewSubscriptionBtn.isHidden = true
        }
        cell.selectUnselectBtn.tag = indexPath.row
        cell.selectUnselectBtn.addTarget(self, action: #selector(selectUnselectBtnAction(sender:)), for: .touchUpInside)
        if selectedIndexes.contains(indexPath.row) {
            cell.selectUnselectBtn.isSelected = true // Selected
        } else {
            cell.selectUnselectBtn.isSelected = false // Unselected
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return reSubscriptionTable.rowHeight
    }
    @objc func selectUnselectBtnAction(sender: UIButton) {
        let index = sender.tag
        let sinNo = self.renewSubscriptionModal?.data?[index].sin ?? ""
        if selectedIndexes.contains(index) {
            selectedIndexes.removeAll { $0 == index } // Deselect
            sinNoArray.removeAll { $0 == sinNo }
        } else {
            selectedIndexes.append(index) // Select
            sinNoArray.append(sinNo)
            self.orderId = renewSubscriptionModal?.data?[index].orderId ?? ""
            self.sin = renewSubscriptionModal?.data?[index].sin ?? ""
            self.productId = renewSubscriptionModal?.data?[index].productId ?? ""
        }
        // Hide or show the button based on selectedIndexes count
        renewSubscriptionBtn.isHidden = selectedIndexes.isEmpty
        reSubscriptionTable.reloadRows(at: [IndexPath(row: index, section: 0)], with: .none)
    }
    @objc func renewSubscriptionBtnTapped(sender: UIButton) {
        let row = sender.tag
        let remainingDays = renewSubscriptionModal?.data?[row].remainingDays ?? 0
        if remainingDays <= 0{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "RenewPlanVC") as! RenewPlanVC
            vc._id = renewSubscriptionModal?.data?[row].orderId ?? ""
            vc.sin = renewSubscriptionModal?.data?[row].sin ?? ""
            vc.productId = renewSubscriptionModal?.data?[row].productId ?? ""
            vc.isRenewPlan = 1
            vc.selectedQuantity = 1
            let sin = renewSubscriptionModal?.data?[row].sin ?? ""
            vc.sinNoArray = [sin]
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            alertPopup.isHidden = false
            alertPopupMsgLbl.text = "You can renew subscription after \(remainingDays) Days"
        }
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let tableViewHeight = scrollView.frame.size.height
        if offsetY > contentHeight - tableViewHeight - 100 {  // Load more when 100px from bottom
            if !isLoading && hasMoreData {
                currentPage += 1
                serviceRenewSubscriptionListingAPI(page: currentPage)
            }
        }
    }
}
extension RenewSubscriptionVC {
    func serviceRenewSubscriptionListingAPI(page: Int) {
        guard !isLoading, hasMoreData else { return }
        isLoading = true
        if page == 1 {
            LoaderManager.shared.showLoader(in: self.view)
        } else {
            LoaderManager.shared.hideLoader()
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = ServiceEndPoint.BhartPhotonUrl.baseURL + "purchasedSubListing?type=1&page=\(page)&limit=10"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: [:], token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    let newData = try JSONDecoder().decode(RenewSubscriptionModal.self, from: JSONResponse.rawData())
                    if page == 1 {
                        self.renewSubscriptionModal = newData
                    } else {
                        self.renewSubscriptionModal?.data?.append(contentsOf: newData.data ?? [])
                    }
                    self.hasMoreData = !(newData.data?.isEmpty ?? true)
                    self.msgLbl.isHidden = (self.renewSubscriptionModal?.data?.count ?? 0) > 0
                    self.reSubscriptionTable.reloadData()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
