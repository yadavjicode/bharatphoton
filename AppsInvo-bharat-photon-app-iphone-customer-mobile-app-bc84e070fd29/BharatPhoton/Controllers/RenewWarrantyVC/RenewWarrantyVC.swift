//
//  ReNewPlanVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit
import SDWebImage

class RenewWarrantyVC: UIViewController {
    @IBOutlet weak var installationBtn: UIButton!
    @IBOutlet weak var withOutInstallationBtn: UIButton!
    @IBOutlet weak var alertPopup: UIView!
    @IBOutlet weak var alertPopupMsgLbl: UILabel!
    @IBOutlet weak var renewWarrantyBtn: UIButton!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var reNewWarrantyTable: UITableView!
    var renewWarrantyListModal: RenewWarrantyListModal?
    var selectedIndexes: [Int] = []
    var finNoArray: [String] = []
    var orderId = ""
    var sin = ""
    var productId = ""
    var type = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        msgLbl.text = "Product Not Available!"
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        renewWarrantyBtn.isHidden = true
        alertPopup.isHidden = true
        msgLbl.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        serviceWarrentyListAPI()
        reNewWarrantyTable.delegate = self
        reNewWarrantyTable.dataSource = self
        reNewWarrantyTable.register(UINib(nibName: "RenewWarrantyCell", bundle: nil), forCellReuseIdentifier: "RenewWarrantyCell")
        reNewWarrantyTable.separatorStyle = .none
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        self.installationBtn.backgroundColor = UIColor(hexString: "FF8D3A")
        self.withOutInstallationBtn.backgroundColor = .white
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
    @IBAction func installationBtnAction(_ sender: UIButton) {
        self.type = 0
        selectedIndexes.removeAll()
        renewWarrantyBtn.isHidden = selectedIndexes.isEmpty
        self.installationBtn.backgroundColor = UIColor(hexString: "FF8D3A")
        self.withOutInstallationBtn.backgroundColor = .white
        serviceWarrentyListAPI()
    }
    @IBAction func withOutinstallationBtnAction(_ sender: UIButton) {
        self.type = 1
        selectedIndexes.removeAll()
        renewWarrantyBtn.isHidden = selectedIndexes.isEmpty
        self.installationBtn.backgroundColor = .white
        self.withOutInstallationBtn.backgroundColor = UIColor(hexString: "FF8D3A")
        serviceWarrentyListAPI()
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
    @IBAction func renewWarrantyBtnAction(_ sender: UIButton) {
        guard !selectedIndexes.isEmpty else {
            print("No item selected")
            return
        }
        // Get remainingDays for selected rows
        let selectedRemainingDays = selectedIndexes.compactMap {
            renewWarrantyListModal?.data?[$0].remainingWarrantyDays
        }
        // Check if any selected item has remainingDays > 0 (still active)
        if selectedRemainingDays.contains(where: { $0 > 0 }) {
            alertPopup.isHidden = false
            alertPopupMsgLbl.text = "Only expired items can be selected for this Order ID"
            return
        }
        // Get subscriptionUUIDs for selected rows
        let selectedUUIDs = selectedIndexes.compactMap {
            renewWarrantyListModal?.data?[$0].uuid
        }
        // Check if all selected UUIDs are the same
        if Set(selectedUUIDs).count == 1 {
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "WarrantyPlanVC") as! WarrantyPlanVC
            vc.orderId = self.orderId
           // vc.sin = self.sin
            vc.productId = self.productId
            vc.selectedQuantity = selectedIndexes.count
            vc.selectedFinNo = self.finNoArray
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
extension RenewWarrantyVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return renewWarrantyListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = reNewWarrantyTable.dequeueReusableCell(withIdentifier: "RenewWarrantyCell", for: indexPath) as! RenewWarrantyCell
        let image = self.renewWarrantyListModal?.data?[indexPath.row].uninstalledProductImage ?? ""
        cell.productImg.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.productNameLbl.text = renewWarrantyListModal?.data?[indexPath.row].productName ?? ""
        cell.categoryNameLbl.text = renewWarrantyListModal?.data?[indexPath.row].categoryName ?? ""
        cell.modalNoLbl.text = renewWarrantyListModal?.data?[indexPath.row].modelNo ?? ""
        cell.oderIdLbl.text = "Order Id :\(renewWarrantyListModal?.data?[indexPath.row].uuid ?? "")"
       // cell.sinNoLbl.text = "FIN : \((renewWarrantyListModal?.data?[indexPath.row].finNo ?? "").uppercased())"
        cell.renewWarrantyLbl.textColor = UIColor(hexString: "FF8D3A")
        cell.renewWarrantyBtn.tag = indexPath.item
        cell.renewWarrantyBtn.addTarget(self, action: #selector(renewWarrantyBtnTapped(sender:)), for: .touchUpInside)
        cell.selectUnselectBtn.tag = indexPath.row
        cell.selectUnselectBtn.addTarget(self, action: #selector(selectUnselectBtnAction(sender:)), for: .touchUpInside)
        if type == 1{
            cell.sinNoLbl.text = "FIN : \((renewWarrantyListModal?.data?[indexPath.row].finNo ?? "").uppercased())"
        }
        else{
            cell.sinNoLbl.text = "SIN : \((renewWarrantyListModal?.data?[indexPath.row].sin ?? "").uppercased())"
        }
        if selectedIndexes.contains(indexPath.row) {
            cell.selectUnselectBtn.isSelected = true // Selected
        } else {
            cell.selectUnselectBtn.isSelected = false // Unselected
        }
        let remainingDays = renewWarrantyListModal?.data?[indexPath.row].remainingWarrantyDays ?? 0
        if remainingDays <= 0{
            cell.remainingWarrantyDaysLbl.text = "Plan Expired"
            cell.remainingWarrantyDaysLbl.textColor = .red
        }
        else{
            cell.remainingWarrantyDaysLbl.textColor = .black
            cell.remainingWarrantyDaysLbl.text = "Expiring : \(renewWarrantyListModal?.data?[indexPath.row].remainingWarrantyDays ?? 0) Days"
        }
        cell.selectionStyle = .none
     
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return reNewWarrantyTable.rowHeight
    }
    @objc func selectUnselectBtnAction(sender: UIButton) {
        let index = sender.tag
        let finNo = self.renewWarrantyListModal?.data?[index].finNo ?? ""
        if selectedIndexes.contains(index) {
            selectedIndexes.removeAll { $0 == index } // Deselect
            finNoArray.removeAll { $0 == finNo }
        } else {
            selectedIndexes.append(index) // Select
            self.orderId = renewWarrantyListModal?.data?[index].orderId ?? ""
            self.sin = renewWarrantyListModal?.data?[index].sin ?? ""
            self.productId = renewWarrantyListModal?.data?[index].productId ?? ""
            finNoArray.append(finNo)
            print(finNoArray)
        }
        // Hide or show the button based on selectedIndexes count
        renewWarrantyBtn.isHidden = selectedIndexes.isEmpty
        reNewWarrantyTable.reloadRows(at: [IndexPath(row: index, section: 0)], with: .none)
    }
    @objc func renewWarrantyBtnTapped(sender: UIButton) {
        let row = sender.tag
        let remainingDays = renewWarrantyListModal?.data?[row].remainingWarrantyDays ?? 0
        if remainingDays <= 0{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "WarrantyPlanVC") as! WarrantyPlanVC
            vc.productId = self.renewWarrantyListModal?.data?[row].productId ?? ""
            vc.orderId = self.renewWarrantyListModal?.data?[row].orderId ?? ""
            let finNo = self.renewWarrantyListModal?.data?[row].finNo ?? ""
            vc.selectedFinNo = [finNo]
            vc.selectedQuantity = 1
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            alertPopup.isHidden = false
            alertPopupMsgLbl.text = "You can renew subscription after \(remainingDays) Days"
            finNoArray.removeAll()
            print(finNoArray)
         }
     }
}
extension RenewWarrantyVC {
    func serviceWarrentyListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "warrentyList?page=1&limit=10&type=\(self.type)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.renewWarrantyListModal = try JSONDecoder().decode(RenewWarrantyListModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.renewWarrantyListModal?.data?.count ?? 0) == 0 ? false : true
                    self.reNewWarrantyTable.reloadData()
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
