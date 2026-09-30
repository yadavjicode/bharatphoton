//
//  ChangeAddressVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 21/06/24.
//

import UIKit
import DropDown

protocol APICheckOutRequestDelegate: AnyObject{
    func didUpdateCheckOutFetchData()
}
class ChangeAddressVC: UIViewController {
    @IBOutlet weak var addressTF: UITextField!
    @IBOutlet weak var zipCodeTF: UITextField!
    @IBOutlet weak var stateTF: UITextField!
    @IBOutlet weak var cityTF: UITextField!
    var delegate: APICheckOutRequestDelegate?
    var dropDown = DropDown()
    var stateList: StateModal?
    var cityList: CityModal?
    var addressId = ""
    var firstName = ""
    var lastName = ""
    var phoneNo = ""
    var country_code = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        serviceAddressDetailsAPI()
        serviceStateListAPI()
        self.addressTF.delegate = self
        self.zipCodeTF.delegate = self
        self.stateTF.delegate = self
        self.cityTF.delegate = self
    }
    @IBAction func hidePopViewAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    @IBAction func tapSelectState(_ sender: UIButton) {
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
    @IBAction func doneBtnAction(_ sender: UIButton) {
        if addressTF.text!.isEmpty{
            self.alert(message: "Please enter address")
        }
        else if zipCodeTF.text!.isEmpty{
            self.alert(message: "Please enter zipcode")
        }
        else if zipCodeTF.text!.count != 6{
            self.alert(message: "Please enter valid zipcode")
        }
        else if stateTF.text!.isEmpty{
            self.alert(message: "Select State")
        }
        else if cityTF.text!.isEmpty{
            self.alert(message: "Select City")
        }
        else{
            self.serviceEditAddressAPI()
        }
    }
}
extension ChangeAddressVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
         if textField == self.addressTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM 123056890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.zipCodeTF {
            let maxLength = 6
            let allowedCharacters = "1234567890"
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
        if textField == self.addressTF {
            self.zipCodeTF.becomeFirstResponder()
        }else if textField == self.zipCodeTF{
            self.stateTF.becomeFirstResponder()
        }else if textField == self.stateTF {
            self.cityTF.becomeFirstResponder()
        }else if textField == self.cityTF{
            self.cityTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: AddressDetails API ==>
extension ChangeAddressVC {
    func serviceAddressDetailsAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addressList?id=\(self.addressId)", params: parms, token: token) { JSONResponse in
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
                self.firstName = firstName
                self.lastName = lastName
                self.phoneNo = "\(phoneNo)"
                self.country_code = country_code
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
// MARK: EditAddress API ==>
extension ChangeAddressVC {
    func serviceEditAddressAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["id": self.addressId, "firstName": self.firstName, "lastName":self.lastName, "phoneNo": self.phoneNo, "country_code": self.country_code, "address": self.addressTF.text!, "ZipCode": zipCodeTF.text!, "state": stateTF.text!, "city": self.cityTF.text!,"addressType": "0"] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.delegate?.didUpdateCheckOutFetchData()
                self.dismiss(animated: false, completion: nil)
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
extension ChangeAddressVC {
   func serviceStateListAPI(){
       LoaderManager.shared.hideLoader()
       let parms = [:] as [String: Any]
       let token = UserDefaults.standard.string(forKey: "token") ?? ""
       BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "stateListing", params: parms, token: token) { JSONResponse in
           LoaderManager.shared.hideLoader()
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
           LoaderManager.shared.hideLoader()
           self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
   }
}
//MARK: CityListAPI ==>
extension ChangeAddressVC {
   func serviceCityListAPI(stateName: String){
       LoaderManager.shared.hideLoader()
       let token = UserDefaults.standard.string(forKey: "token") ?? ""
       let parms = [:] as [String: Any]
       let text = stateName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
       BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cityListing?stateName=\(text)", params: parms, token: token) { JSONResponse in
           LoaderManager.shared.hideLoader()
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
           LoaderManager.shared.hideLoader()
           self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
   }
}

