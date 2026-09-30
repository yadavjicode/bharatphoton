//
//  EditProfileForIndividualVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 27/06/24.
//

import UIKit
import DropDown
import SDWebImage


protocol APIRequestDelegate: AnyObject {
    func didUpdateProfileFetchData()
}
class EditProfileForIndividualVC: UIViewController, AddressListApiCallDelegate, ProfileFetchDataDelegate{
    func didUpdateProfileFetchData() {
        serviceUserProfileAPI()
    }
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool) {
        self.enterAddressTF.text = address
        self.zipCodeTF.text = zipCode
        self.selectStateTF.text = state
        self.selectCityTF.text = city
        self.addressAdd = addressAdd
        self.serviceEditUserProfileAPI()
    }
    @IBOutlet weak var profileImg: UIImageView!
    @IBOutlet weak var firstNameTF: UITextField!
    @IBOutlet weak var lastNameTF: UITextField!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var phoneNoTF: UITextField!
    @IBOutlet weak var enterAddressTF: UITextField!
    @IBOutlet weak var zipCodeTF: UITextField!
    @IBOutlet weak var selectCityTF: UITextField!
    @IBOutlet weak var selectStateTF: UITextField!
    weak var delegate: APIRequestDelegate?
    var dropDown = DropDown()
    var stateList: StateModal?
    var cityList: CityModal?
    var profileImgUrl = ""
    var addressAdd = false
    override func viewDidLoad() {
        super.viewDidLoad()
        serviceUserProfileAPI()
        serviceStateListAPI()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        self.enterAddressTF.autocapitalizationType = .sentences
        self.selectCityTF.autocapitalizationType = .sentences
        self.selectStateTF.autocapitalizationType = .sentences
        self.emailTF.delegate = self
        self.enterAddressTF.delegate = self
        self.zipCodeTF.delegate = self
        self.selectCityTF.delegate = self
        self.selectStateTF.delegate = self
        self.phoneNoTF.delegate = self
        self.phoneNoTF.returnKeyType = .next
        self.emailTF.returnKeyType = .next
        self.enterAddressTF.returnKeyType = .next
        self.zipCodeTF.returnKeyType = .next
        self.selectStateTF.returnKeyType = .next
        self.selectCityTF.returnKeyType = .done
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
    @IBAction func addNewAddressBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
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
    @IBAction func editMobileNoAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ChangePhoneNoVC") as! ChangePhoneNoVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.phoneNo = self.phoneNoTF.text!
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
        if phoneNoTF.text!.isEmpty{
            self.alert(message: "Please enter mobile number")
        }
        else if phoneNoTF.text!.count != 10 {
            self.alert(message: "Please enter valid mobile number")
        }
        else if emailTF.text!.isEmpty{
            self.alert(message: "Please enter email")
        }
        else if !emailTF.text!.isValidEmail(){
            self.alert(message: "Enter valid email address")
        }
        else if enterAddressTF.text!.isEmpty{
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
        else{
            self.serviceEditUserProfileAPI()
        }
    }
}
extension EditProfileForIndividualVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.phoneNoTF
        {
            let maxLength = 10
            let allowedCharacters = "0123456789"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
       else if textField == self.emailTF
        {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.enterAddressTF {
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
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.phoneNoTF {
            self.emailTF.becomeFirstResponder()
        }else if textField == self.emailTF {
            self.enterAddressTF.becomeFirstResponder()
        }else if textField == self.enterAddressTF{
            self.zipCodeTF.becomeFirstResponder()
        }else if textField == self.zipCodeTF {
            self.selectStateTF.becomeFirstResponder()
        }else if textField == self.selectStateTF{
            self.selectCityTF.becomeFirstResponder()
        }else if textField == selectCityTF{
            self.selectCityTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: GetUserProfileAPI ==>
extension EditProfileForIndividualVC {
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
                let firstName = dic["firstName"]?.stringValue ?? ""
                let lastName = dic["lastName"]?.stringValue ?? ""
                let email = dic["email"]?.stringValue ?? ""
                let address = dic["address"]?.stringValue ?? ""
                let city = dic["city"]?.stringValue ?? ""
                let state = dic["state"]?.stringValue ?? ""
                let zipCode = dic["zipCode"]?.intValue ?? 0
               // let userType = dic["userType"]?.intValue ?? 0
                let phoneNo = dic["phoneNo"]?.intValue ?? 0
                self.phoneNoTF.text = "\(phoneNo)"
                self.firstNameTF.text = firstName
                self.lastNameTF.text = lastName
                self.emailTF.text = email
                self.enterAddressTF.text = address
                self.selectStateTF.text = state
                self.selectCityTF.text = city
                self.profileImgUrl = profileImage
                self.profileImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
                self.profileImg.sd_setImage(with: URL(string: profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
                self.serviceCityListAPI(stateName: state)
                if zipCode == 0{
                    self.zipCodeTF.text = ""
                }
                else{
                    self.zipCodeTF.text = "\(zipCode)"
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
//MARK: EditUserProfileAPI ==>
extension EditProfileForIndividualVC {
    func serviceEditUserProfileAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": 1, "phoneNo": self.phoneNoTF.text!, "profileImage": self.profileImgUrl, "address": self.enterAddressTF.text!, "latitude": "", "longitude": "", "zipCode": self.zipCodeTF.text!, "state": self.selectStateTF.text!, "city": selectCityTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .patchJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "editUser", params: parms, token: "") { JSONResponse in
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
 //MARK: StateListAPI ==>
extension EditProfileForIndividualVC{
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
extension EditProfileForIndividualVC {
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
           // self.alert(message: JSONResponse["message"].stringValue, title: Error)
         }
    }
}

