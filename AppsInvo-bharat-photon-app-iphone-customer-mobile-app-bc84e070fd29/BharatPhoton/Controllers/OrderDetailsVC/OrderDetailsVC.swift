//
//  OrderDetailsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 25/06/24.
//

import UIKit
import Razorpay
 
class OrderDetailsVC: UIViewController, UITextViewDelegate{
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var productListTable: UITableView!
    @IBOutlet weak var productListTableHeight: NSLayoutConstraint!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var oderIdLbl: UILabel!
    @IBOutlet weak var dateTimeLbl: UILabel!
    @IBOutlet weak var orderTotalLbl: UILabel!
    @IBOutlet weak var totalPriceLbl: UILabel!
    @IBOutlet weak var installationPriceLbl: UILabel!
    @IBOutlet weak var membershipLbl: UILabel!
    @IBOutlet weak var couponView: UIView!
    @IBOutlet weak var couponLbl: UILabel!
    @IBOutlet weak var taxLbl: UILabel!
    @IBOutlet weak var subTotalLbl: UILabel!
    @IBOutlet weak var nameInAddressLbl: UILabel!
    @IBOutlet weak var fullAddressLbl: UILabel!
    @IBOutlet weak var installationAddressLbl: UILabel!
    @IBOutlet weak var mobileNoLbl: UILabel!
    @IBOutlet weak var writeReviewView: UIView!
    @IBOutlet weak var writeReviewViewHeight: NSLayoutConstraint!
    @IBOutlet weak var payNowBtn: UIButton!
    @IBOutlet weak var trackOrderBtn: UIButton!
    @IBOutlet weak var cancelOrderBtn: UIButton!
    @IBOutlet weak var downloadInvoiceBtn: UIButton!
    @IBOutlet weak var orderAgainBtn: UIButton!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var image1: UIImageView!
    @IBOutlet weak var image2: UIImageView!
    @IBOutlet weak var image3: UIImageView!
    @IBOutlet weak var popupView: UIView!
    @IBOutlet weak var yourReviewTextView: UITextView!
    @IBOutlet weak var cosmosView: CosmosView!
    @IBOutlet weak var installationAddressView: UIView!
    @IBOutlet weak var bottomLogo: UIImageView!
    var orderDetailModal: OrderDetailModal?
    var payNowModal: PayNowModal?
    var ratingValue = 0.0
    var placeholder = "Write your review"
    var productId = ""
    var reviewPhotos = [[String: Any]]()
    var orderId = ""
    var id = ""
    var paymentMethod = 0
    var razorpay: RazorpayCheckout? = nil
    let razorPayKey = "rzp_live_pnPeiBrMepL7QJ"
    var payment_id: String?
    var razorpay_order_id: String?
    var razorpay_signature: String?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        scrollView.isHidden = true
        installationAddressView.isHidden = true
        bottomLogo.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        razorpay = RazorpayCheckout.initWithKey(razorPayKey, andDelegate: self)
        couponView.isHidden = true
        popupView.isHidden = true
        writeReviewView.isHidden = true
        writeReviewViewHeight.constant = 0
        downloadInvoiceBtn.isHidden = true
        orderAgainBtn.isHidden = true
        lbl.isHidden = true
        payNowBtn.isHidden = true
        trackOrderBtn.isHidden = true
        cancelOrderBtn.isHidden = true
        yourReviewTextView.text = placeholder
        yourReviewTextView.textColor = UIColor.lightGray
        yourReviewTextView.delegate = self
        cosmosView.settings.updateOnTouch = true
        cosmosView.settings.fillMode = .half
        serviceOrderDetailsAPI()
        productListTable.delegate = self
        productListTable.dataSource = self
        productListTable.separatorStyle = .none
        productListTable.register(UINib(nibName: "ProductTableViewCell", bundle: nil), forCellReuseIdentifier: "ProductTableViewCell")
        productListTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        cosmosView.settings.fillMode = .full
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
            if obj == self.productListTable && keyPath == "contentSize" {
                productListTableHeight.constant = productListTable.contentSize.height
            }
        }
    }
    func textViewDidBeginEditing(_ textView: UITextView) {
        if textView.text == placeholder {
            textView.text = ""
            textView.textColor = UIColor.black
        }
    }
    func textViewDidEndEditing(_ textView: UITextView) {
        if textView.text.isEmpty {
            textView.text = placeholder
            textView.textColor = UIColor.lightGray
        }
    }
    func getRatingValue() {
        let ratingValue = cosmosView.rating
        print("Rating value: \(ratingValue)")
        self.ratingValue = ratingValue
    }
    func updateCartBadge(){
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    @IBAction func firstUploadPhotoAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
        CameraHandler.shared.imagePickedBlock = { (image, path, url) in
            self.startLoading()
            self.image1.image = image
            AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error in
                DispatchQueue.main.async {
                    self.stopLoading()
                    if let error = error {
                        print(error.localizedDescription)
                    } else if let imgUrl = response as? String {
                        print(imgUrl)
                        self.reviewPhotos.append(["url": imgUrl])
                    }
                }
            })
        }
    }
    @IBAction func secondUploadPhotoAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
        CameraHandler.shared.imagePickedBlock = { (image, path, url) in
            self.startLoading()
            self.image2.image = image
            AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error in
                DispatchQueue.main.async {
                    self.stopLoading()
                    if let error = error {
                        print(error.localizedDescription)
                    } else if let imgUrl = response as? String {
                        print(imgUrl)
                        self.reviewPhotos.append(["url": imgUrl])
                    }
                }
            })
        }
    }
    @IBAction func thirdUploadPhotoAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
        CameraHandler.shared.imagePickedBlock = { (image, path, url) in
            self.startLoading()
            self.image3.image = image
            AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error in
                DispatchQueue.main.async {
                    self.stopLoading()
                    if let error = error {
                        print(error.localizedDescription)
                    } else if let imgUrl = response as? String {
                        print(imgUrl)
                        self.reviewPhotos.append(["url": imgUrl])
                    }
                }
            })
        }
    }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if yourReviewTextView.text == "Write your review"{
            self.alert(message: "Please write your review")
        }
        else{
            getRatingValue()
            serviceWriteReviewAPI()
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
    @IBAction func planDetailAction(_ sender: UIButton) {
        //  let vc = self.storyboard?.instantiateViewController(withIdentifier: "PlanDetailVC") as! PlanDetailVC
        //   elf.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func writeReviewBtnAction(_ sender: UIButton) {
        popupView.isHidden = false
    }
    @IBAction func trackOrderBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderTrackingVC") as! OrderTrackingVC
        vc.id = self.id
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func cancelOrderBtnAction(_ sender: UIButton) {
        showCancelOrderAlert()
    }
    @IBAction func payNowBtnAction(_ sender: UIButton) {
        self.servicePayNowAPI()
    }
    @IBAction func hideReviewPopupAction(_ sender: UIButton) {
        popupView.isHidden = true
    }
    @IBAction func downloadInvoiceBtnAction(_ sender: UIButton) {
        let invoiceUrl = orderDetailModal?.data?.orderDetails?.invoiceUrl ?? ""
        self.downloadInvoice(invoiceUrl: invoiceUrl)
    }
    @IBAction func orderAgainBtnAction(_ sender: UIButton) {
     //   self.serviceReorderAPI()
    }
    func showCancelOrderAlert() {
        let alertController = UIAlertController(
            title: "Cancel Order",
            message: "Are you sure you want to cancel the order?",
            preferredStyle: .alert
        )
        let cancelAction = UIAlertAction(title: "Cancel", style: .cancel) { _ in
            print("Order cancellation was cancelled.")
        }
        alertController.addAction(cancelAction)
        let confirmAction = UIAlertAction(title: "Confirm", style: .destructive) { _ in
            print("Order has been cancelled.")
            self.serviceCancelOrderAPI()
        }
        alertController.addAction(confirmAction)
        present(alertController, animated: true, completion: nil)
    }
}
extension OrderDetailsVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return orderDetailModal?.data?.orderDetails?.products?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ProductTableViewCell", for: indexPath) as! ProductTableViewCell
        cell.productNameLbl.text = self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].categoryName ?? ""
        cell.descriptionLbl.text = self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].product_name ?? ""
        let price = self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].price ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        cell.qtyLbl.text = "QTY : \(self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].quantity ?? 0)"
        let image = self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].image ?? ""
        cell.productImg.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.trackOrderBtn.tag = indexPath.row
        cell.trackOrderBtn.addTarget(self, action: #selector(trackOrderBtnAction(sender:)), for: .touchUpInside)
        cell.addReviewBtn.tag = indexPath.row
        cell.addReviewBtn.addTarget(self, action: #selector(addReviewBtnAction(sender:)), for: .touchUpInside)
        cell.reOrderBrn.tag = indexPath.row
        cell.reOrderBrn.addTarget(self, action: #selector(reOrderBtnAction(sender:)), for: .touchUpInside)
        cell.cancelOrderBtn.tag = indexPath.row
        cell.cancelOrderBtn.addTarget(self, action: #selector(cancelOrderBtnAction(sender:)), for: .touchUpInside)
        let installationPrice = self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].installationPrice ?? 0
        if installationPrice == 0{
            cell.installationPriceLbl.isHidden = true
        }
        else{
            cell.installationPriceLbl.isHidden = false
            cell.installationPriceLbl.text = "Installation Price : \(self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].installationPrice ?? 0) /Pcs"
        }
        let orderStatus = self.orderDetailModal?.data?.orderDetails?.products?[indexPath.row].orderStatus ?? 0
        if orderStatus == 0{
            cell.orderStatusLbl.text = "Pending"
            cell.orderStatusView.backgroundColor = UIColor(hexString: "ffe0ee")
            cell.orderStatusLbl.textColor = UIColor(hexString: "cf005a")
            cell.addReviewView.isHidden = true
            cell.trackOrderView.isHidden = false
            cell.cancelOrderView.isHidden = false
            cell.reOrderView.isHidden = true
            //payNowBtn.isHidden = false
        }
        else if orderStatus == 1{
            cell.orderStatusLbl.text = "Processed"
            cell.orderStatusView.backgroundColor = UIColor(hexString: "FFE8D4")
            cell.orderStatusLbl.textColor = UIColor(hexString: "FF8D3A")
            cell.addReviewView.isHidden = true
            cell.trackOrderView.isHidden = false
            cell.cancelOrderView.isHidden = false
            cell.reOrderView.isHidden = true
           // payNowBtn.isHidden = false
        }
        else if orderStatus == 2{
            cell.orderStatusLbl.text = "Dispatched"
            cell.orderStatusView.backgroundColor = UIColor(hexString: "D4E4FB")
            cell.orderStatusLbl.textColor = UIColor(hexString: "1B6AC6")
            cell.addReviewView.isHidden = true
            cell.trackOrderView.isHidden = false
            cell.cancelOrderView.isHidden = false
            cell.reOrderView.isHidden = true
           // payNowBtn.isHidden = false
        }
        else if orderStatus == 3{
            cell.orderStatusLbl.text = "Delivered"
            cell.orderStatusView.backgroundColor = UIColor(hexString: "CEF2DF")
            cell.orderStatusLbl.textColor = UIColor(hexString: "00B130")
            cell.addReviewView.isHidden = false
            cell.trackOrderView.isHidden = false
            cell.reOrderView.isHidden = false
            cell.cancelOrderView.isHidden = true
            payNowBtn.isHidden = true
        }
        else if orderStatus == 5{
            cell.orderStatusLbl.text = "Canceled"
            cell.orderStatusView.backgroundColor = UIColor(hexString: "ffbfbf")
            cell.orderStatusLbl.textColor = UIColor(hexString: "ff2c2c")
            cell.addReviewView.isHidden = true
            cell.trackOrderView.isHidden = true
            cell.cancelOrderView.isHidden = true
            cell.reOrderView.isHidden = true
            payNowBtn.isHidden = true
//            cell.orderStatusLbl.text = "Shipped"
//            cell.orderStatusView.backgroundColor = UIColor(hexString: "CEF2DF")
//            cell.orderStatusLbl.textColor = UIColor(hexString: "00B130")
//            cell.addReviewView.isHidden = true
//            cell.trackOrderView.isHidden = false
//            cell.cancelOrderView.isHidden = false
        }
        else if orderStatus == 7{
            cell.orderStatusLbl.text = "Canceled"
            cell.orderStatusView.backgroundColor = UIColor(hexString: "ffbfbf")
            cell.orderStatusLbl.textColor = UIColor(hexString: "ff2c2c")
            cell.addReviewView.isHidden = true
            cell.trackOrderView.isHidden = true
            cell.cancelOrderView.isHidden = true
            cell.reOrderView.isHidden = true
            payNowBtn.isHidden = true
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 150
    }
    @objc func trackOrderBtnAction(sender: UIButton) {
        let row = sender.tag
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderTrackingVC") as! OrderTrackingVC
        vc.id = self.orderDetailModal?.data?.orderDetails?.products?[row]._id ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @objc func addReviewBtnAction(sender: UIButton) {
        let row = sender.tag
        self.popupView.isHidden = false
        self.productId = self.orderDetailModal?.data?.orderDetails?.products?[row].productId ?? ""
    }
    @objc func cancelOrderBtnAction(sender: UIButton) {
        let row = sender.tag
        self.productId = self.orderDetailModal?.data?.orderDetails?.products?[row]._id ?? ""
        self.showCancelOrderAlert()
    }
    @objc func reOrderBtnAction(sender: UIButton){
        let row = sender.tag
        serviceReorderAPI(orderId: self.orderDetailModal?.data?.orderDetails?.products?[row]._id ?? "")
    }
}
extension OrderDetailsVC{
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
extension OrderDetailsVC:RazorpayPaymentCompletionProtocol, RazorpayPaymentCompletionProtocolWithData,RazorpayResultProtocol {
    func onComplete(response: [AnyHashable : Any]) {
        print(response)
        if let orderId = response[AnyHashable("razorpay_order_id")] as? String,
           let signature = response[AnyHashable("razorpay_signature")] as? String,
           let paymentId = response[AnyHashable("razorpay_payment_id")] as? String {
            self.serviceVerifyOnlinePaymentAPI(razorpay_order_id: orderId, razorpay_payment_id: paymentId, razorpay_signature: signature)
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
extension OrderDetailsVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        let maxLength = 100
        let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
        let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
        let typedCharacterSet = CharacterSet(charactersIn: string)
        let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
        return alphabet && newString_.length <= maxLength
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        self.yourReviewTextView.resignFirstResponder()
        return true
    }
}
// MARK: OrderDetailsAPI ==>
extension OrderDetailsVC{
    func serviceOrderDetailsAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "orderDetails?orderId=\(self.orderId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.orderDetailModal = try JSONDecoder().decode(OrderDetailModal.self, from: JSONResponse.rawData())
                    self.productListTable.reloadData()
                    self.setdata()
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
    func setdata(){
        scrollView.isHidden = false
        bottomLogo.isHidden = false
        let dateString =  orderDetailModal?.data?.orderDetails?.createdAt ?? ""
        dateTimeLbl.text = dateString.toDisplayDateAndTime()
        let paymentMethod = orderDetailModal?.data?.orderDetails?.paymentMethod ?? ""
        if paymentMethod == "cod"{
            self.payNowBtn.isHidden = false
            self.paymentMethod = 2
        }
        else{
            self.payNowBtn.isHidden = true
            self.paymentMethod = 1
        }
        let diccount = orderDetailModal?.data?.orderDetails?.discountedAmount ?? 0
        if diccount == 0{
            self.couponView.isHidden = true
        }
        else{
            self.couponView.isHidden = false
            self.couponLbl.text = "₹\(orderDetailModal?.data?.orderDetails?.discountedAmount ?? 0)"
        }
        let installastionAddrress = self.orderDetailModal?.data?.installastionAddrress?._id ?? ""
        if installastionAddrress == ""{
            self.installationAddressView.isHidden = true
        }
        else{
            self.installationAddressView.isHidden = false
        }
        self.oderIdLbl.text = orderDetailModal?.data?.orderDetails?.uuid ?? ""
        self.nameInAddressLbl.text = "\(self.orderDetailModal?.data?.orderDetails?.firstName ?? "") \(self.orderDetailModal?.data?.orderDetails?.lastName ?? "")"
        let city = self.orderDetailModal?.data?.orderDetails?.city ?? ""
        let state = self.orderDetailModal?.data?.orderDetails?.state ?? ""
        let zipCode = "\(self.orderDetailModal?.data?.orderDetails?.ZipCode ?? 0)"
        let address = self.orderDetailModal?.data?.orderDetails?.address ?? ""
        let iCity = self.orderDetailModal?.data?.installastionAddrress?.city ?? ""
        let iState = self.orderDetailModal?.data?.installastionAddrress?.state ?? ""
        let iZipCode = self.orderDetailModal?.data?.installastionAddrress?.ZipCode ?? 0
        let iAddress = self.orderDetailModal?.data?.installastionAddrress?.address ?? ""
        self.fullAddressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
        self.installationAddressLbl.text = "\(iAddress) \(iCity), \(iState), \("Pincode") \(iZipCode)"
        let countryCode  = self.orderDetailModal?.data?.orderDetails?.countryCode ?? ""
        let phoneNo  = self.orderDetailModal?.data?.orderDetails?.phoneNo ?? 0
        self.mobileNoLbl.text = "\(countryCode) \(phoneNo)"
        let taxAmount = orderDetailModal?.data?.orderDetails?.taxAmount ?? 0
        self.taxLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: taxAmount), number: .decimal))"
        let subTotal = orderDetailModal?.data?.orderDetails?.subTotal ?? 0
        self.totalPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: subTotal), number: .decimal))"
        let finalTotalAmount = orderDetailModal?.data?.orderDetails?.finalTotalAmount ?? 0
        self.subTotalLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: finalTotalAmount), number: .decimal))"
        downloadInvoiceBtn.isHidden = false
        lbl.isHidden = false
        writeReviewView.isHidden = true
        writeReviewViewHeight.constant = 0
        trackOrderBtn.isHidden = true
        cancelOrderBtn.isHidden = true
    }
}
// MARK: CancelOrderAPI ==>
extension OrderDetailsVC {
    func serviceCancelOrderAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["type": self.paymentMethod, "orderId": self.orderId, "productId": self.productId] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cancelOrder", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.serviceOrderDetailsAPI()
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
// MARK: PayNowAPI ==>
extension OrderDetailsVC {
    func servicePayNowAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["orderId": self.orderId] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "payNow", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.payNowModal = try JSONDecoder().decode(PayNowModal.self, from: JSONResponse.rawData())
                    let razorpayOrderId = self.payNowModal?.data?.razorpayOrderId ?? ""
                    let amountInPaisa = self.payNowModal?.data?.amountInPaisa ?? 0
                    let phoneNo = self.payNowModal?.data?.phoneNo ?? 0
                    self.showPaymentForm(orderId: razorpayOrderId, amount: amountInPaisa, contactNo: phoneNo)
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
// MARK: VerifyPaymentAPI ==>
extension OrderDetailsVC {
    func serviceVerifyOnlinePaymentAPI(razorpay_order_id: String, razorpay_payment_id: String, razorpay_signature: String ){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["razorpay_order_id": razorpay_order_id, "razorpay_payment_id": razorpay_payment_id, "razorpay_signature": razorpay_signature] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyOnlinePayment", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.serviceOrderDetailsAPI()
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
// MARK: OrderAgainAPI ==>
extension OrderDetailsVC {
    func serviceReorderAPI(orderId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["orderId": self.orderId] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "reorder", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
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
// MARK: WriteReviewAPI ==>
extension OrderDetailsVC {
    func serviceWriteReviewAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["productId": self.productId, "rating": self.ratingValue, "review": self.yourReviewTextView.text!, "image": self.reviewPhotos] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addReview", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.popupView.isHidden = true
            case 1:
                self.popupView.isHidden = true
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
