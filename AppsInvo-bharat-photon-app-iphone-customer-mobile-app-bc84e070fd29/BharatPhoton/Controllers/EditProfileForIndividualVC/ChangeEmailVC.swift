//
//  ChangeEmailVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 29/11/24.
//

import UIKit
 

class ChangeEmailVC: UIViewController {
    @IBOutlet weak var verifyBtn: UIButton!
    @IBOutlet weak var doneBtn: UIButton!
    @IBOutlet weak var verifyOtp: UIView!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var enterOtpTF: UITextField!
    @IBOutlet weak var popupView: UIView!
    private let maxPopupHeight: CGFloat = 450
    var delegate: ProfileFetchDataDelegate?
    var email = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        setupSlidingPopup(for: popupView, maxHeight: maxPopupHeight)
        verifyOtp.isHidden = true
        verifyBtn.isHidden = true
        doneBtn.isHidden = false
        self.emailTF.text = email
        self.emailTF.delegate = self
        self.enterOtpTF.delegate = self
        self.emailTF.returnKeyType = .next
        self.enterOtpTF.returnKeyType = .done
        
    }
    @IBAction func hidePhoneNoPopup(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    @IBAction func doneBtnAction(_ sender: UIButton) {
        if emailTF.text!.isEmpty{
            self.alert(message: "Please enter email address")
        }
        else if !emailTF.text!.isValidEmail(){
            self.alert(message: "Enter valid email address")
        }
        else{
            serviceChangeEmailAPI()
        }
    }
    @IBAction func verifyBtnAction(_ sender: UIButton) {
        if emailTF.text!.isEmpty{
            self.alert(message: "Please enter email")
        }
        else if !emailTF.text!.isValidEmail(){
            self.alert(message: "Enter valid email address")
        }
        else if enterOtpTF.text!.isEmpty{
            self.alert(message: "Please enter your 6 digit OTP")
        }
        else{
            serviceVerifyEmailAPI()
        }
    }
}
extension ChangeEmailVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
           if textField == self.emailTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.enterOtpTF {
            let maxLength = 6
            let allowedCharacters = "12304567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.emailTF {
            self.enterOtpTF.becomeFirstResponder()
        }else if textField == self.enterOtpTF{
            self.enterOtpTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: ChangeEmail API ==>
extension ChangeEmailVC{
    func serviceChangeEmailAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["email": self.emailTF.text!] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "chnageEmailPhoneno", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.verifyOtp.isHidden = false
                self.verifyBtn.isHidden = false
                self.doneBtn.isHidden = true
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: "Alert")
         }
    }
}
// MARK: VerifyEmail API ==>
extension ChangeEmailVC{
    func serviceVerifyEmailAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["email_otp": self.enterOtpTF.text!] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyPhoneEmail", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.dismiss(animated: false, completion: nil)
                self.delegate?.didUpdateProfileFetchData()
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: "Alert")
         }
    }
}
 

 
