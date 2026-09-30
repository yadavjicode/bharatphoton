//
//  AddAddressVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 26/06/24.
//

import UIKit
import DropDown
import CountryPickerView
 

protocol AddressListApiCallDelegate: AnyObject {
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool)
}
class AddAddressVC: UIViewController, CountryPickerViewDelegate, CountryPickerViewDataSource {
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var firstNameTF: UITextField!
    @IBOutlet weak var lastNameTF: UITextField!
    @IBOutlet weak var mobileNumberTF: UITextField!
    @IBOutlet weak var addressTF: UITextField!
    @IBOutlet weak var zipCodeTF: UITextField!
    @IBOutlet weak var stateTF: UITextField!
    @IBOutlet weak var cityTF: UITextField!
    @IBOutlet weak var countryCodeLbl: UILabel!
    var delegate: AddressListApiCallDelegate?
    let countryPickerView = CountryPickerView()
    var dropDown = DropDown()
    var stateList: StateModal?
    var cityList: CityModal?
    var controllerName = ""
    var id = ""
    var checkOut = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceStateListAPI()
        countryPickerView.delegate = self
        firstNameTF.delegate = self
        lastNameTF.delegate = self
        mobileNumberTF.delegate = self
        addressTF.delegate = self
        zipCodeTF.delegate = self
        stateTF.delegate = self
        cityTF.delegate = self
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
            view.addGestureRecognizer(tapGesture)
        if controllerName == ""{
            self.lbl.text = "Add Address"
        }
        else{
            self.lbl.text = "Edit Address"
            self.serviceAddressDetailsAPI()
        }
    }
    @objc override func dismissKeyboard() {
        self.view.endEditing(true)  // Dismiss the keyboard
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
    @IBAction func countryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
    }
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        self.countryCodeLbl.text! = country.phoneCode
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
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func tapSelectState(_ sender: UIButton) {
        dismissKeyboard()
        guard let states = stateList?.data else {
              print("State list data is not available.")
              return
          }
          dropDown.dataSource = states
          dropDown.anchorView = sender
          dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
          dropDown.show()
          dropDown.selectionAction = { [weak self] (index: Int, item: String) in
              self?.stateTF.text = states[index]
              self?.serviceCityListAPI(stateName: self?.stateTF.text ?? "")
          }
     }
    @IBAction func tapSelectCity(_ sender: UIButton) {
        dismissKeyboard()
        guard let cities = cityList?.data else {
              print("City list data is not available.")
              return
          }
          dropDown.dataSource = cities
          dropDown.anchorView = sender
          dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
          dropDown.show()
          dropDown.selectionAction = { [weak self] (index: Int, item: String) in
              self?.cityTF.text = cities[index]
          }
     }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if firstNameTF.text!.isEmpty{
            self.alert(message: "Please enter first name")
        }
        else if lastNameTF.text!.isEmpty{
            self.alert(message: "Please enter last name")
        }
        else if mobileNumberTF.text!.isEmpty{
            self.alert(message: "Please enter mobile number")
        }
        else if mobileNumberTF.text!.count != 10{
            self.alert(message: "Please enter valid mobile number")
        }
        else if addressTF.text!.isEmpty{
            self.alert(message: "Please enter your address")
        }
        else if zipCodeTF.text!.isEmpty{
            self.alert(message: "Please enter zipcode")
        }
        else if zipCodeTF.text!.count != 6{
            self.alert(message: "Please enter valid zipcode")
        }
        else if stateTF.text!.isEmpty{
            self.alert(message: "Select state")
        }
        else if cityTF.text!.isEmpty{
            self.alert(message: "Select city")
        }
        else{
            if controllerName == ""{
                self.serviceAddAddressAPI()
            }
            else{
                self.serviceEditAddressAPI()
            }
        }
    }
}
extension AddAddressVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.firstNameTF
        {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.lastNameTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.mobileNumberTF {
            let maxLength = 10
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.addressTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234569870 12234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.zipCodeTF {
            let maxLength = 6
            let allowedCharacters = "0123456789"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.stateTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.cityTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.firstNameTF {
            self.lastNameTF.becomeFirstResponder()
        }else if textField == self.lastNameTF{
            self.mobileNumberTF.becomeFirstResponder()
        }else if textField == self.mobileNumberTF {
            self.addressTF.becomeFirstResponder()
        }else if textField == self.addressTF{
            self.zipCodeTF.becomeFirstResponder()
        }else if textField == zipCodeTF{
            self.stateTF.becomeFirstResponder()
        }else if textField == stateTF{
            self.cityTF.becomeFirstResponder()
        }else if textField == cityTF{
            self.cityTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: AddAddress API ==>
extension AddAddressVC {
    func serviceAddAddressAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["firstName": self.firstNameTF.text!, "lastName":self.lastNameTF.text!, "phoneNo": self.mobileNumberTF.text!, "country_code": self.countryCodeLbl.text!, "address": self.addressTF.text!, "ZipCode": zipCodeTF.text!, "state": stateTF.text!, "city": self.cityTF.text!,"addressType": "0"] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                if self.checkOut == 1{
                    self.navigationController?.popViewController(animated: true)
                    self.delegate?.didUpdateAddressAddAddressFetchData(address: self.addressTF.text!, zipCode: self.zipCodeTF.text!, state: self.stateTF.text!, city: self.cityTF.text!, addressAdd: true)
                }
                else{
                    self.delegate?.didUpdateAddressAddAddressFetchData(address: self.addressTF.text!, zipCode: self.zipCodeTF.text!, state: self.stateTF.text!, city: self.cityTF.text!, addressAdd: true)
                    self.navigationController?.popViewController(animated: true)
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
// MARK: EditAddress API ==>
extension AddAddressVC {
    func serviceEditAddressAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["id": self.id, "firstName": self.firstNameTF.text!, "lastName":self.lastNameTF.text!, "phoneNo": self.mobileNumberTF.text!, "country_code": self.countryCodeLbl.text!, "address": self.addressTF.text!, "ZipCode": zipCodeTF.text!, "state": stateTF.text!, "city": self.cityTF.text!,"addressType": "0"] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.delegate?.didUpdateAddressAddAddressFetchData(address: self.addressTF.text!, zipCode: self.zipCodeTF.text!, state: self.stateTF.text!, city: self.cityTF.text!, addressAdd: true)
                self.navigationController?.popViewController(animated: true)
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
// MARK: AddressDetails API ==>
extension AddAddressVC {
    func serviceAddressDetailsAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addressList?id=\(self.id)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let firstName = dic["firstName"]?.stringValue ?? ""
                let lastName = dic["lastName"]?.stringValue ?? ""
                let phoneNo = dic["phoneNo"]?.intValue ?? 0
                let country_code = dic["country_code"]?.stringValue ?? ""
                let address = dic["address"]?.stringValue ?? ""
                let zipCode = dic["ZipCode"]?.intValue ?? 0
                let state = dic["state"]?.stringValue ?? ""
                let city = dic["city"]?.stringValue ?? ""
                self.firstNameTF.text = firstName
                self.lastNameTF.text = lastName
                self.mobileNumberTF.text = "\(phoneNo)"
                self.countryCodeLbl.text = country_code
                self.addressTF.text = address
                self.zipCodeTF.text = "\(zipCode)"
                self.stateTF.text = state
                self.cityTF.text = city
                self.serviceCityListAPI(stateName: state)
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
//MARK: StateListAPI ==>
extension AddAddressVC {
   func serviceStateListAPI(){
       let parms = [:] as [String: Any]
       let token = UserDefaults.standard.string(forKey: "token") ?? ""
       BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "stateListing", params: parms, token: token) { JSONResponse in
           let code = JSONResponse["code"].intValue
           switch code {
           case 0:
               self.alert(message: JSONResponse["message"].stringValue)
           case 1:
               do {
                   self.stateList = try JSONDecoder().decode(StateModal.self, from: JSONResponse.rawData())
               } catch {
                   print("Error decoding JSON: \(error)")
               }
           default:
               print("Unidentified code found")
               break
           }
       } failure: { Error, JSONResponse in
           self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
   }
}
//MARK: CityListAPI ==>
extension AddAddressVC {
   func serviceCityListAPI(stateName: String){
       let parms = [:] as [String: Any]
       let token = UserDefaults.standard.string(forKey: "token") ?? ""
       let text = stateName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
       BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cityListing?stateName=\(text)", params: parms, token: token) { JSONResponse in
           let code = JSONResponse["code"].intValue
           switch code {
           case 0:
               self.alert(message: JSONResponse["message"].stringValue)
           case 1:
               do {
                   self.cityList = try JSONDecoder().decode(CityModal.self, from: JSONResponse.rawData())
               } catch {
                   print("Error decoding JSON: \(error)")
               }
           default:
               print("Unidentified code found")
               break
           }
       } failure: { Error, JSONResponse in
           self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
   }
}

