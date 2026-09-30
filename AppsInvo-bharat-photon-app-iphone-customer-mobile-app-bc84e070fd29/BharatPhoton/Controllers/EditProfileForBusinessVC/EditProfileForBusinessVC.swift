//
//  EditProfileForBusinessVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 02/07/24.
//

import UIKit
import DropDown
import CountryPickerView
import SDWebImage
 

protocol FetchGetProfileDelegate: AnyObject {
    func didUpdateProfileFetchData()
}
class EditProfileForBusinessVC: UIViewController, CountryPickerViewDelegate, CountryPickerViewDataSource, AddressListApiCallDelegate, ProfileFetchDataDelegate{
    func didUpdateProfileFetchData() {
        serviceUserProfileAPI()
    }
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool) {
        self.enterYourAddressTF.text = address
        self.zipCodeTF.text = zipCode
        self.selectStateTF.text = state
        self.selectCityTF.text = city
        self.addressAdd = addressAdd
        self.serviceEditBusinessProfileAPI()
    }
     
    @IBOutlet weak var profileImg: UIImageView!
    @IBOutlet weak var companyNameTF: UITextField!
    @IBOutlet weak var gstNoTF: UITextField!
    @IBOutlet weak var enterMobileNumberTF: UITextField!
    @IBOutlet weak var enterSecondaryMobileNoTF: UITextField!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var enterYourAddressTF: UITextField!
    @IBOutlet weak var zipCodeTF: UITextField!
    @IBOutlet weak var selectStateTF: UITextField!
    @IBOutlet weak var selectCityTF: UITextField!
    @IBOutlet weak var gstCertificateImg: UIImageView!
    @IBOutlet weak var contactPersionNameTF: UITextField!
    @IBOutlet weak var designationTF: UITextField!
    @IBOutlet weak var contactPersionMobileNoTF: UITextField!
    @IBOutlet weak var countryCodeLbl: UILabel!
    @IBOutlet weak var secondryCountryCodeLbl: UILabel!
    @IBOutlet weak var thirdCountryCodeLbl: UILabel!
    let countryPickerView = CountryPickerView()
    weak var delegate: FetchGetProfileDelegate?
    var dropDown = DropDown()
    var stateList: StateModal?
    var cityList: CityModal?
    var countryTag = ""
    var profileImgUrl = ""
    var gstCertificateImgUrl = ""
    var addressAdd = false
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        serviceUserProfileAPI()
        serviceStateListAPI()
        countryPickerView.delegate = self
        companyNameTF.delegate = self
        gstNoTF.delegate = self
        enterMobileNumberTF.delegate = self
        enterSecondaryMobileNoTF.delegate = self
        emailTF.delegate = self
        enterYourAddressTF.delegate = self
        zipCodeTF.delegate = self
        selectStateTF.delegate = self
        selectCityTF.delegate = self
        contactPersionNameTF.delegate = self
        designationTF.delegate = self
        contactPersionMobileNoTF.delegate = self
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
            view.addGestureRecognizer(tapGesture)
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
    @IBAction func countryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
        self.countryTag = "1"
    }
    @IBAction func secondaryCountryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
        self.countryTag = "2"
    }
    @IBAction func thirdCountryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
        self.countryTag = "3"
    }
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        if countryTag == "1"{
            self.countryCodeLbl.text! = country.phoneCode
        }
        else if countryTag == "2"{
            self.secondryCountryCodeLbl.text! = country.phoneCode
        }
        else if countryTag == "3"{
            self.thirdCountryCodeLbl.text = country.phoneCode
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
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
              self?.selectStateTF.text = states[index]
              self?.serviceCityListAPI(stateName: self?.selectStateTF.text ?? "")
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
              self?.selectCityTF.text = cities[index]
          }
     }
    @IBAction func addNewAddressBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func uploadProfileImageBtnAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
        CameraHandler.shared.imagePickedBlock = { (image,path,url) in
            AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error  in
                if error != nil {
                    print(error as Any)
                }
                else {
                    DispatchQueue.main.async {
                        self.profileImgUrl = response as? String ?? ""
                        self.profileImg.image = image
                    }
                }
            })
        }
    }
    @IBAction func uploadGstCertificateAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
        CameraHandler.shared.imagePickedBlock = { (image,path,url) in
            AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error  in
                if error != nil {
                    print(error as Any)
                }
                else {
                    DispatchQueue.main.async {
                        self.gstCertificateImgUrl = response as? String ?? ""
                        self.gstCertificateImg.image = image
                    }
                }
            })
        }
    }
    @IBAction func editMobileNoAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ChangePhoneNoVC") as! ChangePhoneNoVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.phoneNo = self.enterMobileNumberTF.text!
        vc.delegate = self
        self.present(vc, animated: true)
        
    }
    @IBAction func editEmailAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ChangeEmailVC") as! ChangeEmailVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.email = self.emailTF.text!
        vc.delegate = self
        self.present(vc, animated: true)
    }
    @IBAction func saveBtnAction(_ sender: UIButton) {
        if  companyNameTF.text!.isEmpty{
            self.alert(message: "Please enter company name")
        }
        else if  enterMobileNumberTF.text!.isEmpty{
            self.alert(message: "Please enter mobile number")
        }
        else if enterMobileNumberTF.text!.count != 10{
            self.alert(message: "Please enter valid mobile number")
        }
//        else if enterSecondaryMobileNoTF.text!.isEmpty{
//            self.alert(message: "Please enter secondary mobile number")
//        }
//        else if enterSecondaryMobileNoTF.text!.count != 10{
//            self.alert(message: "Please enter valid secondary mobile number")
//        }
        else if emailTF.text!.isEmpty{
            self.alert(message: "Please enter email")
        }
        else if !emailTF.text!.isValidEmail(){
            self.alert(message: "Please enter valid email")
        }
        else if gstNoTF.text!.isEmpty{
            self.alert(message: "Please enter GST number")
        }
        else if enterYourAddressTF.text!.isEmpty{
            self.alert(message: "Please enter your address")
        }
        else if zipCodeTF.text!.isEmpty{
            self.alert(message: "Please enter zipcode")
        }
        else if zipCodeTF.text!.count != 6 {
            self.alert(message: "Please enter valid zipcode")
        }
        else if selectStateTF.text!.isEmpty{
            self.alert(message: "Select state")
        }
        else if selectCityTF.text!.isEmpty{
            self.alert(message: "Select city")
        }
        else if contactPersionNameTF.text!.isEmpty{
            self.alert(message: "Please enter contact person name")
        }
        else if designationTF.text!.isEmpty{
            self.alert(message: "Please enter designation")
        }
        else if contactPersionMobileNoTF.text!.isEmpty{
            self.alert(message: "Please enter contact persion mobile number")
        }
        else{
            self.serviceEditBusinessProfileAPI()
        }
        
    }
}
extension EditProfileForBusinessVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.companyNameTF
        {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM -"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.enterMobileNumberTF
        {
            let maxLength = 10
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.enterSecondaryMobileNoTF {
            let maxLength = 10
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.emailTF {
            let maxLength = 30
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.gstNoTF {
            let maxLength = 15
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        
        else if textField == self.enterYourAddressTF {
            let maxLength = 50
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM 1234567890 -"
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
        else if textField == self.selectStateTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.selectCityTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.contactPersionNameTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.designationTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.contactPersionMobileNoTF {
            let maxLength = 20
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.enterMobileNumberTF {
            self.enterSecondaryMobileNoTF.becomeFirstResponder()
        }else if textField == self.enterSecondaryMobileNoTF{
            self.emailTF.becomeFirstResponder()
        }else if textField == self.emailTF {
            self.enterYourAddressTF.becomeFirstResponder()
        }else if textField == self.enterYourAddressTF{
            self.zipCodeTF.becomeFirstResponder()
        }else if textField == zipCodeTF{
            self.selectStateTF.becomeFirstResponder()
        }else if textField == selectStateTF{
            self.selectCityTF.becomeFirstResponder()
        }else if textField == selectCityTF{
            self.contactPersionNameTF.becomeFirstResponder()
        }else if textField == contactPersionNameTF{
            self.designationTF.becomeFirstResponder()
        } else if textField == designationTF{
            self.contactPersionMobileNoTF.becomeFirstResponder()
        }else if textField == contactPersionMobileNoTF{
            self.contactPersionMobileNoTF.resignFirstResponder()
        }
        return true
    }
}
extension EditProfileForBusinessVC {
    func serviceUserProfileAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "userProfile", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let profileImage = dic["profileImage"]?.stringValue ?? ""
                let gstFile = dic["gstFile"]?.stringValue ?? ""
                let country_code = dic["country_code"]?.stringValue ?? ""
                let businessEmail = dic["businessEmail"]?.stringValue ?? ""
                let businessPhoneNo = dic["businessPhoneNo"]?.intValue ?? 0
                let contactNo = dic["contactNo"]?.intValue ?? 0
                let secondayPhoneNo = dic["secondayPhoneNo"]?.intValue ?? 0
                let designation = dic["designation"]?.stringValue ?? ""
                let contactPerson = dic["contactPerson"]?.stringValue ?? ""
                let address = dic["address"]?.stringValue ?? ""
                let city = dic["city"]?.stringValue ?? ""
                let state = dic["state"]?.stringValue ?? ""
                let zipCode = dic["zipCode"]?.stringValue ?? ""
                let companyName = dic["companyName"]?.stringValue ?? ""
                let gstNumber = dic["gstNumber"]?.stringValue ?? ""
                if secondayPhoneNo == 0{
                    self.enterSecondaryMobileNoTF.text = ""
                }
                else{
                    self.enterSecondaryMobileNoTF.text = "\(secondayPhoneNo)"
                }
                self.companyNameTF.text = companyName
                self.gstNoTF.text = gstNumber
                self.zipCodeTF.text = zipCode
                self.enterYourAddressTF.text = address
                self.selectCityTF.text = city
                self.selectStateTF.text = state
                self.countryCodeLbl.text = country_code
                self.secondryCountryCodeLbl.text = country_code
                self.thirdCountryCodeLbl.text = country_code
                self.emailTF.text = businessEmail
                self.enterMobileNumberTF.text = "\(businessPhoneNo)"
                self.designationTF.text = designation
                self.contactPersionNameTF.text = contactPerson
                self.contactPersionMobileNoTF.text = "\(contactNo)"
                self.profileImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
                self.profileImg.sd_setImage(with: URL(string: profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
                self.gstCertificateImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
                self.gstCertificateImg.sd_setImage(with: URL(string: gstFile), placeholderImage: UIImage(named: ""))
                self.gstCertificateImgUrl = gstFile
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
//MARK: EditUserProfile API ==>
extension EditProfileForBusinessVC {
    func serviceEditBusinessProfileAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["userType": 2, "profileImage": self.profileImgUrl, "companyName": companyNameTF.text!, "gstNumber": gstNoTF.text!, "gstFile": self.gstCertificateImgUrl, "address": self.enterYourAddressTF.text!, "latitude": "", "longitude": "", "zipCode": self.zipCodeTF.text!, "state": self.selectStateTF.text!, "city": selectCityTF.text!, "contactPerson": self.contactPersionNameTF.text!, "designation": self.designationTF.text!, "contactNo":self.contactPersionMobileNoTF.text!, "secondayPhoneNo": self.enterSecondaryMobileNoTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .patchJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "editUser", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                if self.addressAdd == true {
                    self.delegate?.didUpdateProfileFetchData()
                }
                else{
                    self.navigationController?.popViewController(animated: true)
                    self.delegate?.didUpdateProfileFetchData()
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
 //MARK: StateList API ==>
extension EditProfileForBusinessVC {
    func serviceStateListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
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
//MARK: CityList API ==>
extension EditProfileForBusinessVC {
    func serviceCityListAPI(stateName: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
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
           // self.alert(message: JSONResponse["message"].stringValue, title: Error)
         }
    }
}

