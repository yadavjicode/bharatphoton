//
//  SubscribePaymentVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 07/11/24.
//

import UIKit
import Razorpay

class SubscribePaymentVC: UIViewController, AddressListApiCallDelegate{
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool) {
        serviceAddressListAPI()
    }
    @IBOutlet weak var codLbl: UILabel!
    @IBOutlet weak var codView: UIView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var addressListTable: UITableView!
    @IBOutlet weak var addressListTableHeight: NSLayoutConstraint!
    @IBOutlet weak var onlinePaymentImg: UIImageView!
    @IBOutlet weak var codImg: UIImageView!
    var addressListModal: AddressListModal?
    var addressId = ""
    var city = ""
    var state = ""
    var zipCode = ""
    var address = ""
    var subscriptionCartId = ""
    var orderId = ""
    var uuid = ""
    var paymentMode = 0
    var razorpay: RazorpayCheckout? = nil
    let razorPayKey = "rzp_live_pnPeiBrMepL7QJ"
    var payment_id:String?
    var razorpay_order_id: String?
    var razorpay_signature: String?
    var type = 1
    var id = ""
    var isRenewPlan = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        print(isRenewPlan)
        updateCartBadge()
        updateNotificationBadge()
         razorpay = RazorpayCheckout.initWithKey(razorPayKey, andDelegate: self)
         statusBarColor()
         serviceAddressListAPI()
         addressListTable.delegate = self
         addressListTable.dataSource = self
         addressListTable.register(UINib(nibName: "MyAddressListCell", bundle: nil), forCellReuseIdentifier: "MyAddressListCell")
         addressListTable.separatorStyle = .none
         addressListTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
         paymentMode = 1
         onlinePaymentImg.image = UIImage(named: "radio selected")
         codImg.image = UIImage(named: "radio Unselected")
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        if isRenewPlan == 1{
            self.codLbl.isHidden = true
            self.codView.isHidden = true
        }
        else{
            self.codLbl.isHidden = false
            self.codView.isHidden = false
        }
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
            if obj == self.addressListTable && keyPath == "contentSize" {
                addressListTableHeight.constant = addressListTable.contentSize.height
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
    @IBAction func addAddressBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func proceedWithOnlinePaymentAction(_ sender: UIButton) {
        self.paymentMode = 1
        onlinePaymentImg.image = UIImage(named: "radio selected")
        codImg.image = UIImage(named: "radio Unselected")
    }
    @IBAction func codAction(_ sender: UIButton) {
        self.paymentMode = 2
        codImg.image = UIImage(named: "radio selected")
        onlinePaymentImg.image = UIImage(named: "radio Unselected")
    }
    @IBAction func doneBtnAction(_ sender: UIButton) {
        if paymentMode == 0{
            alert(message: "Please select payment mode")
        }
        else{
        self.servicePurchaseSubscriptionAPI()
        }
    }
}
extension SubscribePaymentVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return addressListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = addressListTable.dequeueReusableCell(withIdentifier: "MyAddressListCell", for: indexPath) as! MyAddressListCell
        cell.lbl.text = "\(addressListModal?.data?[indexPath.row].firstName ?? "") \(addressListModal?.data?[indexPath.row].lastName ?? "")"
        let countryCode = addressListModal?.data?[indexPath.row].country_code ?? ""
        let phoneNo = "\(addressListModal?.data?[indexPath.row].phoneNo ?? 0)"
        let address = addressListModal?.data?[indexPath.row].address ?? ""
        let city = addressListModal?.data?[indexPath.row].city ?? ""
        let state = addressListModal?.data?[indexPath.row].state ?? ""
        let zipCode = "\(addressListModal?.data?[indexPath.row].ZipCode ?? 0)"
        cell.mobileNoLbl.text = "\(countryCode) \(phoneNo)"
        cell.addressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
        cell.selectionStyle = .none
        cell.sideMenuBtn.tag = indexPath.row
        cell.sideMenuBtn.addTarget(self, action: #selector(menuBtnTapped(sender:)), for: .touchUpInside)
        cell.deleteBtn.isHidden = true
        cell.editBtn.tag = indexPath.row
        cell.editBtn.addTarget(self, action: #selector(editAddressBtnTapped(sender:)), for: .touchUpInside)
        let currentAddress = addressListModal?.data?[indexPath.row]
        let selectedAddress = addressListModal?.data?[indexPath.row].selectedAddress ?? ""
        if selectedAddress == "1"{
            cell.selectedView.borderColor = UIColor(hexString: "FF8D3A")
            cell.selectedImg.image = UIImage(named: "Check")
            cell.selectedView.borderLineWidth = 1
            self.addressId = currentAddress?._id ?? ""
            self.city = currentAddress?.city ?? ""
            self.state = currentAddress?.state ?? ""
            self.zipCode = "\(currentAddress?.ZipCode ?? 0)"
            self.address = currentAddress?.address ?? ""
        }
        else{
            cell.selectedView.borderColor = .clear
            cell.selectedImg.image = UIImage(named: "UnCheck")
        }
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return addressListTable.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.serviceUpdateDeliveryAddressAPI(addressId: addressListModal?.data?[indexPath.row]._id ?? "")
    }
    // MARK: MenuBtnAction ==>
    @objc func menuBtnTapped(sender: UIButton) {
        let indexPath = IndexPath(row: sender.tag, section: 0)
        let cell = addressListTable.cellForRow(at: indexPath) as! MyAddressListCell
        cell.editDeletePopUpView.isHidden = false
    }
    //MARK: EditAddressBtnTapped Action ==>
    @objc func editAddressBtnTapped(sender: UIButton) {
        let indexPath = IndexPath(row: sender.tag, section: 0)
        let cell = addressListTable.cellForRow(at: indexPath) as! MyAddressListCell
        cell.editDeletePopUpView.isHidden = true
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.controllerName = "Edit Address"
        vc.id = addressListModal?.data?[indexPath.row]._id ?? ""
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: false)
    }
}
// MARK: RazorPay PaymentGateway Function ==>
extension SubscribePaymentVC{
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
extension SubscribePaymentVC: RazorpayPaymentCompletionProtocol, RazorpayPaymentCompletionProtocolWithData, RazorpayResultProtocol {
    func onComplete(response: [AnyHashable : Any]) {
        print(response)
        if let orderId = response[AnyHashable("razorpay_order_id")] as? String,
           let signature = response[AnyHashable("razorpay_signature")] as? String,
           let paymentId = response[AnyHashable("razorpay_payment_id")] as? String {
           self.serviceVerifySubscriptionPaymentAPI(razorpay_order_id: orderId, razorpay_payment_id: paymentId, razorpay_signature: signature)
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
// MARK: AddressList API ===>
extension SubscribePaymentVC {
    func serviceAddressListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addressList", params: parms, token:token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.addressListModal = try JSONDecoder().decode(AddressListModal.self, from: JSONResponse.rawData())
                    self.addressListTable.reloadData()
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
}
// MARK: CreateOrder API ==>
extension SubscribePaymentVC {
    func servicePurchaseSubscriptionAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["type": self.type, "subscriptionCartId": self.subscriptionCartId, "addressId": self.addressId, "paymentMode": self.paymentMode] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "purchaseSubscription", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                let msg = JSONResponse["message"].stringValue
                if msg == "addressId is not allowed to be empty"{
                    self.alert(message: "Please select address")
                }
                else{
                    self.alert(message: msg)
                }
            case 1:
                // Online Payment
                let dic = JSONResponse["data"].dictionaryValue
                let uuid = dic["uuid"]?.stringValue ?? ""
              //  let userId = dic["userId"]?.stringValue ?? ""
                let razorpayOrderId = dic["razorpayOrderId"]?.stringValue ?? ""
                let amountInPaisa = dic["amountInPaisa"]?.intValue ?? 0
                let phoneNo = dic["phoneNo"]?.intValue ?? 0
                let _id = dic["_id"]?.stringValue ?? ""
                self.orderId = _id
                self.uuid = uuid
                self.showPaymentForm(orderId: razorpayOrderId, amount: amountInPaisa, contactNo: phoneNo)
            case 2:
                // COD
                let dic = JSONResponse["data"].dictionaryValue
                let uuid = dic["uuid"]?.stringValue ?? ""
                let _id = dic["_id"]?.stringValue ?? ""
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "ThanksPurchasePlanVC") as! ThanksPurchasePlanVC
                vc.city = self.city
                vc.state = self.state
                vc.zipCode = self.zipCode
                vc.address = self.address
                vc.orderId = _id
                vc.uuid = uuid
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
// MARK: VerifySubscriptionPayment API ==>
extension SubscribePaymentVC {
    func serviceVerifySubscriptionPaymentAPI(razorpay_order_id: String, razorpay_payment_id: String, razorpay_signature: String ){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["razorpay_order_id": razorpay_order_id, "razorpay_payment_id": razorpay_payment_id, "razorpay_signature": razorpay_signature] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifysubscriptionPayment", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "ThanksPurchasePlanVC") as! ThanksPurchasePlanVC
                vc.city = self.city
                vc.state = self.state
                vc.zipCode = self.zipCode
                vc.address = self.address
                vc.orderId = self.orderId
                vc.uuid = self.uuid
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
// MARK: UpdateDeliveryAddress API ==>
extension SubscribePaymentVC {
    func serviceUpdateDeliveryAddressAPI(addressId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["addressId": addressId] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "updateDeliveryAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print(self.alert(message: JSONResponse["message"].stringValue))
            case 1:
                self.serviceAddressListAPI()
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
            LoaderManager.shared.hideLoader()
         }
    }
}
