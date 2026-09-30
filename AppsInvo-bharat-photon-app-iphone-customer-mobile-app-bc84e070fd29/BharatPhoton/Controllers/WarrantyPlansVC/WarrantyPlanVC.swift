//
//  WarrantyPlanVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 15/07/24.
//

import UIKit
import Razorpay
 

class WarrantyPlanVC: UIViewController {
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var view1: UIView!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var warrantyPlanTable: UITableView!
    @IBOutlet weak var warrantyPlanTableHeight: NSLayoutConstraint!
    var warrantyPlanListModal: WarrantyPlanListModal?
    var renewWarrantyModal: RenewWarrantyModal?
    var productId = ""
    var orderId = ""
    var mode = ""
    var warrentyId = ""
    var selectedIndex = 0
    var selectedQuantity = 0
    var selectedFinNo: [String] = []
    var razorpay: RazorpayCheckout? = nil
    let razorPayKey = "rzp_live_pnPeiBrMepL7QJ"
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        print("FIN NO \(self.selectedFinNo)")
        print("QTY \(self.selectedQuantity)")
        updateNotificationBadge()
        razorpay = RazorpayCheckout.initWithKey(razorPayKey, andDelegate: self)
        msgLbl.isHidden = true
        view1.isHidden = false
        warrantyPlanTable.delegate = self
        warrantyPlanTable.dataSource = self
        warrantyPlanTable.register(UINib(nibName: "WarrantyPlanCell", bundle: nil), forCellReuseIdentifier: "WarrantyPlanCell")
        warrantyPlanTable.separatorStyle = .none
        warrantyPlanTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        serviceAvilableWarrentyListAPI()
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
    // MARK: Dynamic table ROW Height Delegate
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.warrantyPlanTable && keyPath == "contentSize" {
                warrantyPlanTableHeight.constant = warrantyPlanTable.contentSize.height
            }
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
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    @IBAction func continueBtnAction(_ sender: UIButton) {
        serviceRenewWarrentyAPI()
    }
}
extension WarrantyPlanVC{
    func showPaymentForm(orderId:String, amount:Int, contactNo: Int) {
        let options: [String: Any] = [
            "key": razorPayKey,
            "amount": amount, // Must be an integer, not a string
            "currency": "INR",
            "description": "",
            "order_id": orderId, // Make sure this is a valid order_id from your backend
            "image": "", // Use a valid image URL or remove this key if not needed
            "name": "Bhart Photon",
            "prefill": [
                "contact": contactNo,
                "email": UserDefaults.standard.string(forKey: "email") ?? ""
            ],
            "theme": [
                "color": "#FF8D3A"
            ]
        ]
        if let rzp = self.razorpay {
            rzp.open(options, displayController: self)
        } else {
            print("Unable to open Razorpay")
        }
    }
}
extension WarrantyPlanVC:RazorpayPaymentCompletionProtocol, RazorpayPaymentCompletionProtocolWithData,RazorpayResultProtocol {
    func onComplete(response: [AnyHashable : Any]) {
        print(response)
        if let orderId = response[AnyHashable("razorpay_order_id")] as? String,
           let signature = response[AnyHashable("razorpay_signature")] as? String,
           let paymentId = response[AnyHashable("razorpay_payment_id")] as? String {
            self.serviceVerifyWarrentyPaymentAPI(razorpay_order_id: orderId, razorpay_payment_id: paymentId, razorpay_signature: signature)
        } else {
            print("Failed to extract some values from the response.")
        }
    }
    func onPaymentError(_ code: Int32, description str: String) {
        print("Payment error: \(code) | \(str)")
        self.alert(message: str, title: "Alert")
    }
    func onPaymentSuccess(_ payment_id: String) {
        print("Payment success: \(payment_id)")
    }
    // If using RazorpayPaymentCompletionProtocolWithData
    func onPaymentError(_ code: Int32, description str: String, andData response: [AnyHashable : Any]?) {
        print("Payment error with data: \(code) | \(str) | \(String(describing: response))")
    }
    func onPaymentSuccess(_ payment_id: String, andData response: [AnyHashable : Any]?) {
        print("Payment success with data: \(payment_id)")
        if let paymentResponse = response {
            if let razorpay_order_id = paymentResponse["razorpay_order_id"] as? String,
               let razorpay_payment_id = paymentResponse["razorpay_payment_id"] as? String,
               let razorpay_signature = paymentResponse["razorpay_signature"] as? String {
                print("Order ID: \(razorpay_order_id), Payment ID: \(razorpay_payment_id), Signature: \(razorpay_signature)")
            } else {
                print("Success details not found")
            }
        }
    }
}
extension WarrantyPlanVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return warrantyPlanListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "WarrantyPlanCell", for: indexPath) as! WarrantyPlanCell
        if indexPath.row == selectedIndex{
            //cell.selectedView.borderColor = UIColor(hexString: "FF8D3A")
            cell.selectedImg.image = UIImage(named: "radio selected")
        }
        else{
           // cell.selectedView.borderColor = .clear
            cell.selectedImg.image = UIImage(named: "radio Unselected")
        }
        cell.priceLbl.text = "₹ \(warrantyPlanListModal?.data?[indexPath.row].price ?? 0)"
        let mode = warrantyPlanListModal?.data?[indexPath.row].mode ?? ""
        if mode == "T"{
            cell.lbl.text = "2 Years"
        }
        else if mode == "Y"{
            cell.lbl.text = "1 Year"
        }
        else if mode == "H"{
            cell.lbl.text = "6 Months"
        }
        else if mode == "Q"{
            cell.lbl.text = "3 Months"
        }
        else if mode == "M"{
            cell.lbl.text = "1 Month"
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 120
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedIndex = indexPath.row
        warrantyPlanTable.reloadData()
        self.mode = warrantyPlanListModal?.data?[indexPath.row].mode ?? ""
        self.warrentyId = warrantyPlanListModal?.data?[indexPath.row]._id ?? ""
    }
}
//mode:// M - monthly, Q - quarterly, H - half-yearly, Y--yearly
// MARK: AvilableWarrenty API ==>
extension WarrantyPlanVC {
    func serviceAvilableWarrentyListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "avilableWarrenty?productId=\(self.productId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.warrantyPlanListModal = try JSONDecoder().decode(WarrantyPlanListModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.warrantyPlanListModal?.data?.count ?? 0) == 0 ? false : true
                    self.view1.isHidden = (self.warrantyPlanListModal?.data?.count ?? 0) == 0 ? true : false
                    self.warrantyPlanTable.reloadData()
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
// MARK: RenewWarrenty API ==>
extension WarrantyPlanVC {
    func serviceRenewWarrentyAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["finNo": self.selectedFinNo, "productId": self.productId, "orderId": self.orderId, "warrentyId": self.warrentyId, "mode": self.mode, "quantity": self.selectedQuantity] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "renewWarrenty", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.renewWarrantyModal = try JSONDecoder().decode(RenewWarrantyModal.self, from: JSONResponse.rawData())
                    let razorpayOrderId = self.renewWarrantyModal?.data?.razorpayOrderId ?? ""
                    let amountInPaisa = self.renewWarrantyModal?.data?.amountInPaisa ?? 0
                    self.showPaymentForm(orderId: razorpayOrderId, amount: amountInPaisa, contactNo: 0)
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
// MARK: VerifyWarrentyPayment API ==>
extension WarrantyPlanVC {
    func serviceVerifyWarrentyPaymentAPI(razorpay_order_id: String, razorpay_payment_id: String, razorpay_signature: String ){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["razorpay_order_id": razorpay_order_id, "razorpay_payment_id": razorpay_payment_id, "razorpay_signature": razorpay_signature] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyWarrentyPayment", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "WarrantyPaymentSuccessVC") as! WarrantyPaymentSuccessVC
                self.navigationController?.pushViewController(vc, animated: true)
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
