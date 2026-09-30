//
//  CustomerSupportRegisterComplantVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 04/07/24.
//

import UIKit
import DropDown

class CustomerSupportRegisterComplantVC: UIViewController, UITextViewDelegate, AddressListApiCallDelegate {
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool) {
        serviceAddressListAPI()
    }
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var siteImg: UIImageView!
    @IBOutlet weak var categoryTF: UITextField!
    @IBOutlet weak var productTF: UITextField!
    @IBOutlet weak var selectSinNoTF: UITextField!
    @IBOutlet weak var selectAddressTF: UITextField!
    @IBOutlet weak var yourComplantTextView: UITextView!
    @IBOutlet weak var sensingRangeIsLowBtn: UIButton!
    @IBOutlet weak var noOperationBtn: UIButton!
    @IBOutlet weak var waterFlowingContinuslyBtn: UIButton!
    @IBOutlet weak var waterFlowIsLessBtn: UIButton!
    @IBOutlet weak var otherBtn: UIButton!
    @IBOutlet weak var yourComplantView: UIView!
    var categoryListModal: CategoryModal?
    var productListingModal: ProductListingModal?
    var addressListModal: AddressListModal?
    var dropDown = DropDown()
    let placeholder = "Enter your complain"
    var complaintReason = ""
    var siteImageUrl = ""
    var categoryId = ""
    var productId = ""
    var addressId = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceCategoryListAPI()
        serviceAddressListAPI()
        yourComplantView.isHidden = true
        yourComplantTextView.delegate = self
        yourComplantTextView.text = placeholder
        yourComplantTextView.textColor = UIColor.lightGray
        otherBtn.isSelected = false
        categoryTF.delegate = self
        productTF.delegate = self
        selectSinNoTF.delegate = self
        selectSinNoTF.placeholder = "Enter sin"
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
    @IBAction func clickToUploadSitePhotoAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
        CameraHandler.shared.imagePickedBlock = { (image,path,url) in
            AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error  in
                if error != nil {
                    print(error as Any)
                }
                else {
                    DispatchQueue.main.async {
                        self.siteImageUrl = response as? String ?? ""
                        self.siteImg.image = image
                    }
                }
            })
        }
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
    @IBAction func categoryAction(_ sender: UIButton) {
        guard let categories = categoryListModal?.data else { return }
        dropDown.dataSource = categories.map { $0.categoryName ?? "" }
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            let selectedCategory = categories[index]
            self?.categoryTF.text = selectedCategory.categoryName
            self?.categoryId = selectedCategory._id ?? ""
            self?.serviceProductListAPI(categoryId: selectedCategory._id ?? "")
        }
    }
    @IBAction func productAction(_ sender: UIButton) {
        guard let products = productListingModal?.data else { return }
        let productNames = products.map { $0.productName ?? "" }
        dropDown.dataSource = productNames
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            // Find product by name to safely retrieve the full object
            let selectedProduct = products[index]
            self?.productTF.text = selectedProduct.productName ?? ""
            self?.productId = selectedProduct._id ?? ""
        }
    }
    @IBAction func selectSinNoAction(_ sender: UIButton) {
    }
    @IBAction func sensingRangeIsLowBtnAction(_ sender: UIButton) {
        self.complaintReason = "Technical deficiency in the product"
        yourComplantView.isHidden = true
        sensingRangeIsLowBtn.isSelected = true
        noOperationBtn.isSelected = false
        waterFlowIsLessBtn.isSelected = false
        waterFlowingContinuslyBtn.isSelected = false
        otherBtn.isSelected = false
    }
    @IBAction func noOperationBtnAction(_ sender: UIButton) {
        self.complaintReason = "Product not working"
        yourComplantView.isHidden = true
        noOperationBtn.isSelected = true
        sensingRangeIsLowBtn.isSelected = false
        waterFlowIsLessBtn.isSelected = false
        waterFlowingContinuslyBtn.isSelected = false
        otherBtn.isSelected = false
    }
    @IBAction func waterFlowingContinuslyBtnAction(_ sender: UIButton) {
        self.complaintReason = "Need/change Accessories"
        yourComplantView.isHidden = true
        waterFlowingContinuslyBtn.isSelected = true
        sensingRangeIsLowBtn.isSelected = false
        waterFlowIsLessBtn.isSelected = false
        noOperationBtn.isSelected = false
        otherBtn.isSelected = false
    }
    @IBAction func waterFlowIsLessBtnAction(_ sender: UIButton) {
//        self.complaintReason = ""
//        yourComplantView.isHidden = true
//        waterFlowIsLessBtn.isSelected = true
//        sensingRangeIsLowBtn.isSelected = false
//        waterFlowingContinuslyBtn.isSelected = false
//        noOperationBtn.isSelected = false
//        otherBtn.isSelected = false
    }
    @IBAction func otherBtnAction(_ sender: UIButton) {
        self.complaintReason = "Other"
        yourComplantView.isHidden = false
        otherBtn.isSelected = true
        sensingRangeIsLowBtn.isSelected = false
        waterFlowingContinuslyBtn.isSelected = false
        noOperationBtn.isSelected = false
        waterFlowIsLessBtn.isSelected = false
    }
    @IBAction func addNewAddress(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func tapSelectAddress(_ sender: UIButton) {
        guard let addressList = addressListModal?.data else { return }
        dropDown.dataSource = addressList.map {
            //let _id = $0._id ?? ""
            let address = $0.address ?? ""
            let city = $0.city ?? ""
            let state = $0.state ?? ""
            let zipCode = $0.ZipCode ?? 0
            return "\(address), \(city), \(state) \(zipCode)"
        }
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            guard let self = self else { return }
            let selectedAddress = addressList[index]
            let _id = selectedAddress._id ?? ""
            let address = selectedAddress.address ?? ""
            let city = selectedAddress.city ?? ""
            let state = selectedAddress.state ?? ""
            let zipCode = selectedAddress.ZipCode ?? 0
            self.addressId = _id
            self.selectAddressTF.text = "\(address) \(city) \(state) \(zipCode)"
        }
    }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if categoryTF.text!.isEmpty {
            self.alert(message: "Please select category")
        }
        else if productTF.text!.isEmpty {
            self.alert(message: "Please select product")
        }
        else if selectAddressTF.text!.isEmpty{
            self.alert(message: "Please select address")
        }
        else if complaintReason == ""{
            self.alert(message: "Please select reason")
        }
        else if complaintReason == "Other" && (yourComplantTextView.text?.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty == true || yourComplantTextView.text == "Enter your complain") {
            self.alert(message: "Please enter your complaint")
        }
        else if siteImageUrl == ""{
            self.alert(message: "Please upload an image")
        }
        else {
            serviceRegisterComplaintAPI()
        }
    }
    @IBAction func customerCareBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportCustomerCareVC") as! CustomerSupportCustomerCareVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func complaintHistoryBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportComplaintHistoryVC") as! CustomerSupportComplaintHistoryVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func troubleSootBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportTroubleShootingVideoVC") as! CustomerSupportTroubleShootingVideoVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
}
extension CustomerSupportRegisterComplantVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.categoryTF
        {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM 123056890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.productTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM 123056890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.selectSinNoTF {
            guard let currentText = textField.text as NSString? else { return false }
            let updatedText = currentText.replacingCharacters(in: range, with: string).uppercased()
            let rawText = updatedText.replacingOccurrences(of: "-", with: "")
            if rawText.count > 6 {
                return false
            }
            let allowedCharacters = CharacterSet.uppercaseLetters.union(.decimalDigits)
            let typedCharacterSet = CharacterSet(charactersIn: string.uppercased())
            if !allowedCharacters.isSuperset(of: typedCharacterSet) && !string.isEmpty {
                return false
            }
            var formatted = ""
            for (index, char) in rawText.enumerated() {
                if index > 0 && index % 2 == 0 {
                    formatted.append("-")
                }
                formatted.append(char)
            }
            textField.text = formatted
            return false
        }
        else if textField == self.yourComplantTextView {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.categoryTF {
            self.productTF.becomeFirstResponder()
        }else if textField == self.productTF{
            self.selectSinNoTF.becomeFirstResponder()
        }else if textField == self.selectSinNoTF {
            self.selectSinNoTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: RegisterComplaint API ==>
extension CustomerSupportRegisterComplantVC {
    func serviceRegisterComplaintAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["type": 0, "orderId": "", "purchasedSubscriptionId": "", "addressId": self.addressId, "categoryId": self.categoryId, "productId": self.productId, "sin": self.selectSinNoTF.text!, "complaintReason": self.complaintReason, "description": yourComplantTextView.text!, "image": [["url": self.siteImageUrl]]] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "registerComplaint", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let uuid = dic["uuid"]?.stringValue ?? ""
                let createdAt = dic["createdAt"]?.stringValue ?? ""
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "ComplaintRegisterSuccessfullyVC") as! ComplaintRegisterSuccessfullyVC
                vc.complantNo = uuid
                vc.complantDate = createdAt
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
// MARK: CategoryListing API ==>
extension CustomerSupportRegisterComplantVC {
    func serviceCategoryListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "categoryListing", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.categoryListModal = try JSONDecoder().decode(CategoryModal.self, from: JSONResponse.rawData())
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
// MARK: ProductListing API ==>
extension CustomerSupportRegisterComplantVC {
    func serviceProductListAPI(categoryId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productByCategoryId?categoryId=\(categoryId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productListingModal = try JSONDecoder().decode(ProductListingModal.self, from: JSONResponse.rawData())
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
// MARK: AddressList API ==>
extension CustomerSupportRegisterComplantVC {
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
        let address = addressListModal?.data?.first?.address ?? ""
        let city = addressListModal?.data?.first?.city ?? ""
        let state = addressListModal?.data?.first?.state ?? ""
        let zipCode = "\(addressListModal?.data?.first?.ZipCode ?? 0)"
        self.addressId = addressListModal?.data?.first?._id ?? ""
        self.selectAddressTF.text = "\(address) \(city) \(state) \(zipCode)"
    }
}
