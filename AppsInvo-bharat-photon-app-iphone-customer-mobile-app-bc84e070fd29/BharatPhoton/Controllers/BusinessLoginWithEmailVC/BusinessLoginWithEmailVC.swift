//
//  BusinessLoginWithEmailVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/06/24.
//

import UIKit
import CountryPickerView
 

class BusinessLoginWithEmailVC: UIViewController, CountryPickerViewDelegate, CountryPickerViewDataSource {
    @IBOutlet weak var hideShowPasswordBtn: UIButton!
    @IBOutlet weak var countryNameView: UIView!
    @IBOutlet weak var mobileNoView: UIView!
    @IBOutlet weak var emailView: UIView!
    @IBOutlet weak var passwordView: UIView!
    @IBOutlet weak var forgotPasswordBtn: UIButton!
    @IBOutlet weak var forgotPasswordBtnHeight: NSLayoutConstraint!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var passwordTF: UITextField!
    @IBOutlet weak var phoneNumberTF: UITextField!
    @IBOutlet weak var flagImg: UIImageView!
    @IBOutlet weak var countryNameLbl: UILabel!
    @IBOutlet weak var countryCodeLbl: UILabel!
    @IBOutlet weak var phoneLoginEmailLoginSelectUnSelectBtn: UIButton!
    let countryPickerView = CountryPickerView()
    var guestUser = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        lbl.text = "Please login to your account"
        self.emailTF.delegate = self
        self.passwordTF.delegate = self
        self.phoneNumberTF.delegate = self
        self.emailTF.returnKeyType = .next
        self.passwordTF.returnKeyType = .done
        self.passwordTF.autocapitalizationType = .sentences
        self.countryPickerView.delegate = self
        
    }
    @IBAction func countryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
    }
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        self.flagImg.image = country.flag
        self.countryNameLbl.text = "\(country.name) (\(country.code))"
        self.countryCodeLbl.text! = country.phoneCode
    }
    @IBAction func guestUserAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "TabVC") as! TabVC
        vc.guestUser = self.guestUser
        UserDefaults.standard.set(self.guestUser, forKey: "guestUser")
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func individualTabClickAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func forgotPasswordAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ForgotPasswordVC") as! ForgotPasswordVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func hideAndShowPasswordAction(_ sender: UIButton) {
        if sender.isSelected == false
        {
            hideShowPasswordBtn.isSelected = true
            passwordTF.isSecureTextEntry = false
        }
        else if sender.isSelected == true
        {
            hideShowPasswordBtn.isSelected = false
            passwordTF.isSecureTextEntry = true
        }
    }
    @IBAction func phoneLoginEmailLoginBtnAction(_ sender: UIButton){
           if sender.isSelected == false{
               lbl.text = "Please enter your mobile number to proceed further"
               phoneLoginEmailLoginSelectUnSelectBtn.isSelected = true
               self.countryNameView.isHidden = false
               self.mobileNoView.isHidden = false
               emailView.isHidden = true
               passwordView.isHidden = true
               forgotPasswordBtn.isHidden = true
               forgotPasswordBtnHeight.constant = 0
           }
           else if sender.isSelected == true
           {
               lbl.text = "Please login to your account"
               phoneLoginEmailLoginSelectUnSelectBtn.isSelected = false
               emailView.isHidden = false
               passwordView.isHidden = false
               forgotPasswordBtn.isHidden = false
               forgotPasswordBtnHeight.constant = 20
               countryNameView.isHidden = true
               mobileNoView.isHidden = true
           }
     }
    @IBAction func loginBtnAction(_ sender: UIButton) {
        // Check if email and password are provided
            if let email = emailTF.text, !email.isEmpty, let password = passwordTF.text, !password.isEmpty {
                guard email.isValidEmail() else {
                    self.alert(message: "Please enter a valid email address")
                    return
                }
                // Proceed with email login
                serviceLoginWithEmailAPI()
            }
            // Check if phone number is provided
            else if let phone = phoneNumberTF.text, !phone.isEmpty {
                guard phone.isValidPhoneNumber() else {
                    self.alert(message: "Please enter a valid phone number")
                    return
                }
                self.serviceLoginWithPhoneAPI()
             }
            else {
                // If neither email/password nor phone number are provided
                self.alert(message: "Please enter either email and password or phone number")
            }
        }
    @IBAction func createBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "SignupForBusinessVC") as! SignupForBusinessVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
  }
    // Extension to validate email format
    extension String {
        // Extension to validate phone number format (simple example)
        func isValidPhoneNumber() -> Bool {
            let phoneRegEx = "^[0-9]{10}$" // This regex assumes a 10-digit phone number
            let phonePred = NSPredicate(format: "SELF MATCHES %@", phoneRegEx)
            return phonePred.evaluate(with: self)
        }
    }
extension BusinessLoginWithEmailVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
           if textField == self.emailTF {
            let maxLength = 50
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.passwordTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.phoneNumberTF {
            let maxLength = 10
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.emailTF {
            self.passwordTF.becomeFirstResponder()
        }else if textField == self.passwordTF{
            self.passwordTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: LoginAPI ==>
extension BusinessLoginWithEmailVC {
    func serviceLoginWithPhoneAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["country_code": self.countryCodeLbl.text!, "businessPhoneNo": self.phoneNumberTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "login", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let token = dic["auth_token"]?.stringValue ?? ""
                UserDefaults.standard.set(token, forKey: "auth_token")
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "OtpVerificationVC") as! OtpVerificationVC
                vc.mobileNumber = self.phoneNumberTF.text!
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
extension BusinessLoginWithEmailVC {
    func serviceLoginWithEmailAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["businessEmail": emailTF.text!, "password": passwordTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "login", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let token = dic["auth_token"]?.stringValue ?? ""
                UserDefaults.standard.set(token, forKey: "token")
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                let sceneDelegate = windowScene.delegate as? SceneDelegate {
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                let tabVC = storyboard.instantiateViewController(withIdentifier: "TabVC") as! TabVC
                let nav = UINavigationController(rootViewController: tabVC)
                sceneDelegate.window?.rootViewController = nav
                sceneDelegate.window?.makeKeyAndVisible()
            }
            case 2:
                print(JSONResponse["data"].dictionaryValue)
                let dic = JSONResponse["data"].dictionaryValue
                let token = dic["auth_token"]?.stringValue ?? ""
                UserDefaults.standard.set(token, forKey: "auth_token")
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "OtpVerificationVC") as! OtpVerificationVC
                vc.businessEmail = 1
                vc.email = self.emailTF.text!
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
 

