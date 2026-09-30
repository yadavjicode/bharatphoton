//
//  PaymentVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/07/24.
//

import UIKit
import Razorpay
 
class PaymentVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var onlinePaymentImg: UIImageView!
    @IBOutlet weak var codImg: UIImageView!
    var addressId = ""
    var cartId = ""
    var installationStatus = 0
    var city = ""
    var state = ""
    var zipCode = ""
    var address = ""
    var uuid = ""
    var orderId = ""
    var paymentMode = 0
    var razorpay: RazorpayCheckout? = nil
    let razorPayKey = "rzp_live_pnPeiBrMepL7QJ"
    var payment_id: String?
    var razorpay_order_id: String?
    var razorpay_signature: String?
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        razorpay = RazorpayCheckout.initWithKey(razorPayKey, andDelegate: self)
        paymentMode = 1
        onlinePaymentImg.image = UIImage(named: "radio selected")
        codImg.image = UIImage(named: "radio Unselected")
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
            serviceCreateOrderAPI()
        }
    }
}
extension PaymentVC{
    func showPaymentForm(orderId:String, amount:Int, contactNo: Int) {
        let options: [String: Any] = [
            "key": razorPayKey,
            "amount": amount,
            "currency": "INR",
            "description": "",
            "order_id": orderId,
            "image": "",
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
extension PaymentVC:RazorpayPaymentCompletionProtocol, RazorpayPaymentCompletionProtocolWithData,RazorpayResultProtocol {
    func onComplete(response: [AnyHashable : Any]) {
        print(response)
        if let orderId = response[AnyHashable("razorpay_order_id")] as? String,
           let signature = response[AnyHashable("razorpay_signature")] as? String,
           let paymentId = response[AnyHashable("razorpay_payment_id")] as? String {
            self.serviceVerifyPaymentAPI(razorpay_order_id: orderId, razorpay_payment_id: paymentId, razorpay_signature: signature)
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
// MARK: CreateOrderAPI ==>
extension PaymentVC {
    func serviceCreateOrderAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["cartId": self.cartId, "addressId": self.addressId, "paymentMode": self.paymentMode] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "createOrder", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let uuid = dic["uuid"]?.stringValue ?? ""
                let _id = dic["_id"]?.stringValue ?? ""
              //let cartId = dic["cartId"]?.stringValue ?? ""
                let razorpayOrderId = dic["razorpayOrderId"]?.stringValue ?? ""
                let amountInPaisa = dic["amountInPaisa"]?.intValue ?? 0
                let phoneNo = dic["phoneNo"]?.intValue ?? 0
                self.orderId = _id // MARK: ==>
                self.uuid = uuid
                self.showPaymentForm(orderId: razorpayOrderId, amount: amountInPaisa, contactNo: phoneNo)
            case 2:
                let dic = JSONResponse["data"].dictionaryValue
                let uuid = dic["uuid"]?.stringValue ?? ""
                let _id = dic["_id"]?.stringValue ?? ""
                if self.installationStatus == 0{
                    let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderConfirmedWithoutInstallationVC") as! OrderConfirmedWithoutInstallationVC
                    vc.uuid = uuid
                    self.navigationController?.pushViewController(vc, animated: true)
                }else{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderConfirmedWithInstallationVC") as! OrderConfirmedWithInstallationVC
                vc.uuid = uuid
                vc.orderId = _id
                vc.city = self.city
                vc.state = self.state
                vc.zipCode = self.zipCode
                vc.address = self.address
                self.navigationController?.pushViewController(vc, animated: true)
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
// MARK: VerifyPaymentAPI ==>
extension PaymentVC {
    func serviceVerifyPaymentAPI(razorpay_order_id: String, razorpay_payment_id: String, razorpay_signature: String ){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["razorpay_order_id": razorpay_order_id, "razorpay_payment_id": razorpay_payment_id, "razorpay_signature": razorpay_signature] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyPayment", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                if self.installationStatus == 0{
                    let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderConfirmedWithoutInstallationVC") as! OrderConfirmedWithoutInstallationVC
                    vc.uuid = self.uuid
                    self.navigationController?.pushViewController(vc, animated: true)
                }
                else{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderConfirmedWithInstallationVC") as! OrderConfirmedWithInstallationVC
                vc.uuid = self.uuid
                vc.orderId = self.orderId
                vc.city = self.city
                vc.state = self.state
                vc.zipCode = self.zipCode
                vc.address = self.address
                self.navigationController?.pushViewController(vc, animated: true)
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
