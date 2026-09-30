//
//  CheckOutVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/06/24.
//

import UIKit
 
class CheckOutVC: UIViewController, AddressListApiCallDelegate, APICartDetailsRequestDelegate {
    func didUpdateCartDetailFetchData(couponCode: String) {
        serviceCheckOutAPI()
    }
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool) {
        serviceCheckOutAPI()
    }
    @IBOutlet weak var couponView: UIView!
    @IBOutlet weak var couponCodeLbl: UILabel!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var alertView: UIView!
    @IBOutlet weak var addressView: UIView!
    @IBOutlet weak var changeBtn: UIButton!
    @IBOutlet weak var addBtn: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var installationCheckUnCheckBtn: UIButton!
    @IBOutlet weak var enterPincodeView: UIView!
    @IBOutlet weak var enterPincodeTF: UITextField!
    @IBOutlet weak var installationAvailableMsgView: UIView!
    @IBOutlet weak var installationAvailableMsgLbl: UILabel!
    @IBOutlet weak var checkProductTable: UITableView!
    @IBOutlet weak var checkProductTableHeight: NSLayoutConstraint!
    @IBOutlet weak var totalPriceLbl: UILabel!
    @IBOutlet weak var taxLbl: UILabel!
    @IBOutlet weak var couponDiscountLbl: UILabel!
    @IBOutlet weak var finalTotalAmountAfterDiscountLbl: UILabel!
    @IBOutlet weak var couponNameLbl: UILabel!
    @IBOutlet weak var payNowBtn: UIButton!
    @IBOutlet weak var nameInAddressLbl: UILabel!
    @IBOutlet weak var fullAddressLbl: UILabel!
    @IBOutlet weak var mobileNoLbl: UILabel!
    var checkOutModal: CheckOutModal?
    var cartId = ""
    var addressId = ""
    var installationStatus = 0
    var city = ""
    var state = ""
    var zipCode = ""
    var address = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        self.scrollView.isHidden = true
        alertView.isHidden = true
        changeBtn.isHidden = true
        addBtn.isHidden = true
        addressView.isHidden = true
        enterPincodeTF.delegate = self
        updateCartBadge()
        updateNotificationBadge()
        serviceCheckOutAPI()
        enterPincodeView.isHidden = true
        installationAvailableMsgView.isHidden = true
        checkProductTable.delegate = self
        checkProductTable.dataSource = self
        checkProductTable.register(UINib(nibName: "CheckOutProductCell", bundle: nil), forCellReuseIdentifier: "CheckOutProductCell")
        self.checkProductTable.rowHeight = UITableView.automaticDimension
        self.checkProductTable.separatorStyle = .none
        self.checkProductTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        let attributedString = NSMutableAttributedString(string: "Installation available @500/PC (You can pay this amount at the time of installation)")
        // Set the color for the last 5 characters
        let lastPartRange = NSRange(location: attributedString.length - 61, length: 7)
        attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor(red: 255/255, green: 141/255, blue: 58/255, alpha: 1), range: lastPartRange)
        // Set the attributed string to the label
        installationAvailableMsgLbl.attributedText = attributedString
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    // MARK: Dynamic table ROW Height Delegate
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.checkProductTable && keyPath == "contentSize" {
                checkProductTableHeight.constant = checkProductTable.contentSize.height
            }
        }
    }
    // MARK: back swipe gesture func ===>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        // Handle the swipe gesture here
        if gestureRecognizer.direction == .right {
            // Perform the back action (e.g., pop to the previous view controller)
            navigationController?.popViewController(animated: true)
        }
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
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func viewCouponAndOfferAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CouponsVC") as! CouponsVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.cartId = self.cartId
        vc.delegate = self
        self.present(vc, animated: true)
    }
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func exploreProductBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(identifier: "TabVC") as? TabVC
        self.navigationController?.pushViewController(vc!, animated: false)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            vc?.selectedIndex = 0
        }
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    @IBAction func installationSelectUnselectAction(_ sender: UIButton) {
        if sender.isSelected == false{
            self.installationCheckUnCheckBtn.isSelected = true
            enterPincodeView.isHidden = false
            installationAvailableMsgView.isHidden = true
        }
        else if sender.isSelected == true{
            self.installationCheckUnCheckBtn.isSelected = false
            enterPincodeView.isHidden = true
            installationAvailableMsgView.isHidden = true
        }
    }
    @IBAction func checkBtnAction(_ sender: UIButton) {
        serviceInstallationCheckAPI()
    }
    @IBAction func addBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.checkOut = 1
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func changeAddressAction(_ sender: UIButton) {
        let  vc = self.storyboard?.instantiateViewController(withIdentifier: "MyAddressListVC") as! MyAddressListVC
        vc.delegate = self
        vc.isAddressSelect = "1"
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func payNowAction(_ sender: UIButton) {
        if self.checkOutModal?.data?.address?._id ?? "" == ""{
            self.alert(message: "Please add address")
        }
        else{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "PaymentVC") as! PaymentVC
            vc.addressId = self.addressId
            vc.cartId = self.cartId
            vc.installationStatus = self.installationStatus
            vc.city = self.city
            vc.state = self.state
            vc.zipCode = self.zipCode
            vc.address = self.address
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}
extension CheckOutVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        let maxLength = 6
        let allowedCharacters = "1234567890"
        let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
        let typedCharacterSet = CharacterSet(charactersIn: string)
        let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
        
        return alphabet && newString_.length <= maxLength
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        self.enterPincodeTF.resignFirstResponder()
        return true
    }
}
extension CheckOutVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return checkOutModal?.data?.cart?.products?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = checkProductTable.dequeueReusableCell(withIdentifier: "CheckOutProductCell", for: indexPath) as! CheckOutProductCell
        //  cell.productCategoryNameLbl.text = checkOutModal?.data?.cart?.products?[indexPath.row].category ?? ""
        cell.productNameLbl.text = checkOutModal?.data?.cart?.products?[indexPath.row].productName ?? ""
        let price = checkOutModal?.data?.cart?.products?[indexPath.row].price ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        cell.qtyLbl.text = "Qty: \(checkOutModal?.data?.cart?.products?[indexPath.row].quantity ?? 0)"
        cell.installationAmount.text = "₹ \(checkOutModal?.data?.cart?.products?[indexPath.row].installationPrice ?? 0) /Pcs."
        let image = self.checkOutModal?.data?.cart?.products?[indexPath.row].image?.first?.url ?? ""
        cell.productImg.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.deleteBtn.tag = indexPath.item
        cell.deleteBtn.addTarget(self, action: #selector(deleteBtnTapped(sender:)), for: .touchUpInside)
        if checkOutModal?.data?.cart?.products?[indexPath.row].installationPrice ?? 0 == 0{
            cell.deleteBtn.isHidden = true
            cell.installationAmount.isHidden = true
            cell.installationLbl.isHidden = true
            self.installationCheckUnCheckBtn.isSelected = false
            self.enterPincodeView.isHidden = true
            self.installationAvailableMsgView.isHidden = true
        }
        else{
            cell.deleteBtn.isHidden = false
            cell.installationAmount.isHidden = false
            cell.installationLbl.isHidden = false
            self.installationCheckUnCheckBtn.isSelected = true
            self.enterPincodeView.isHidden = false
            self.installationAvailableMsgView.isHidden = true
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 110
    }
    @objc func deleteBtnTapped(sender: UIButton) {
        let row = sender.tag
        let installationPrice = checkOutModal?.data?.cart?.products?[row].installationPrice ?? 0
        if installationPrice == 0{
            
        }
        else{
            
        let cartId = checkOutModal?.data?.cart?._id ?? ""
        let id = checkOutModal?.data?.cart?.products?[row]._id ?? ""
      //  self.serviceDeleteCartAPI(cartId: cartId, id: id)
        self.serviceRemoveInstallationAPI(cartId: cartId, id: id)
    }
}
}
// MARK: CheckOut API ==>
extension CheckOutVC {
    func serviceCheckOutAPI(){
        self.startLoading()
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "checkout?cartId=\(self.cartId)", params: parms, token: token) { JSONResponse in
            self.stopLoading()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print(JSONResponse["message"].intValue)
            case 1:
                do {
                    self.checkProductTable.reloadData()
                    self.checkOutModal = try JSONDecoder().decode(CheckOutModal.self, from: JSONResponse.rawData())
                    self.checkProductTable.reloadData()
                    self.setData()
                    self.updateCartBadge()
                } catch {
                    print("Error decoding JSON: \(error)")
                    self.alertView.isHidden = false
                }
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.stopLoading()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
    func setData(){
        self.scrollView.isHidden = false
        if self.checkOutModal?.data?.address?._id ?? "" == ""{
            addBtn.isHidden = false
            changeBtn.isHidden = true
            addressView.isHidden = true
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "MyAddressListVC") as! MyAddressListVC
            vc.delegate = self
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            addBtn.isHidden = true
            changeBtn.isHidden = false
            addressView.isHidden = false
        }
        if self.checkOutModal?.data?.coupon?.couponCodes?.first ?? "" == ""{
            self.couponView.isHidden = true
        }
        else{
            self.couponView.isHidden = false
        }
        self.couponCodeLbl.text = self.checkOutModal?.data?.coupon?.couponCodes?.first ?? ""
        self.couponNameLbl.text = "Coupon \(self.checkOutModal?.data?.coupon?.couponCodes?.first ?? "")"
        let discount = self.checkOutModal?.data?.priceSummary?.discountAmount ?? 0
        self.couponDiscountLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: discount), number: .decimal))"
        let totalAmount = self.checkOutModal?.data?.priceSummary?.totalAmount ?? 0
        self.totalPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: totalAmount), number: .decimal))"
        let taxAmount = self.checkOutModal?.data?.priceSummary?.taxAmount ?? 0
        self.taxLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: taxAmount), number: .decimal))"
        let finalTotalAmount = self.checkOutModal?.data?.priceSummary?.finalTotalAmount ?? 0.0
        self.finalTotalAmountAfterDiscountLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: finalTotalAmount), number: .decimal))"
        self.payNowBtn.setTitle("Pay Now \(finalTotalAmountAfterDiscountLbl.text!)", for: .normal)
        self.nameInAddressLbl.text = "\(self.checkOutModal?.data?.address?.firstName ?? "") \(self.checkOutModal?.data?.address?.lastName ?? "")"
        self.addressId = self.checkOutModal?.data?.address?._id ?? ""
        let city = self.checkOutModal?.data?.address?.city ?? ""
        let state = self.checkOutModal?.data?.address?.state ?? ""
        let zipCode = "\(self.checkOutModal?.data?.address?.zipCode ?? 0)"
        let address = self.checkOutModal?.data?.address?.address ?? ""
        self.fullAddressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
        let countryCode  = self.checkOutModal?.data?.address?.country_code ?? ""
        let phoneNo  = self.checkOutModal?.data?.address?.phoneNo ?? 0
        self.mobileNoLbl.text = "\(countryCode) \(phoneNo)"
        self.city = city
        self.state = state
        self.zipCode = zipCode
        self.address = address
    }
}
// MARK: Installation Check API ==>
extension CheckOutVC {
    func serviceInstallationCheckAPI(){
        self.startLoading()
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let pincode = Int(self.enterPincodeTF.text!) ?? 0
        let parms = ["cartId": self.cartId, "pincode": pincode] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "installation", params: parms, token: token) { JSONResponse in
            self.stopLoading()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.installationStatus = 0
                self.serviceCheckOutAPI()
                self.installationAvailableMsgView.isHidden = true
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "InstallationNotAvailableAlertVC") as! InstallationNotAvailableAlertVC
                vc.modalPresentationStyle = .overCurrentContext
                vc.modalTransitionStyle = .crossDissolve
                self.present(vc, animated: true)
            case 1:
                self.installationStatus = 1
                self.serviceCheckOutAPI()
                self.installationAvailableMsgView.isHidden = true
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.stopLoading()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
//MARK: RemoveInstallation API ==>
extension CheckOutVC {
    func serviceRemoveInstallationAPI(cartId: String, id: String){
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["cartId":cartId, "id":id] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .patchJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "removeInstallation", params: parms, token: token) { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.serviceCheckOutAPI()
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
         }
    }
}
// MARK: DeleteCart API ==>
extension CheckOutVC {
    func serviceDeleteCartAPI(cartId: String, id: String){
        let baseUrl = ServiceEndPoint.BhartPhotonUrl.baseURL
        let parameters = [
            "cartId": cartId,
            "id": id
        ]
        guard let postData = try? JSONSerialization.data(withJSONObject: parameters, options: []) else {
            print("Error serializing JSON")
            return
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        var request = URLRequest(url: URL(string: "\(baseUrl)removeFromCart")!, timeoutInterval: Double.infinity)
        request.addValue(token, forHTTPHeaderField: "authtoken")
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "DELETE"
        request.httpBody = postData
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
                return
            }
            guard let data = data else {
                print("No data returned")
                return
            }
            do {
                if let jsonResponse = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                    // Handle the JSON response
                    print("Response JSON: \(jsonResponse)")
                    DispatchQueue.main.async {
                        if let code = jsonResponse["code"] as? Int {
                            switch code {
                            case 0:
                                print("Error: \(jsonResponse["message"] as? String ?? "Unknown error")")
                            case 1:
                                print("Success! Cart item removed.")
                                self.serviceCheckOutAPI()
                            default:
                                print("Unidentified code: \(code)")
                            }
                        } else {
                            print("No 'code' key found or it's not an Int")
                        }
                    }
                }
            } catch {
                print("Error parsing response: \(error.localizedDescription)")
            }
        }
        task.resume()
    }
}

