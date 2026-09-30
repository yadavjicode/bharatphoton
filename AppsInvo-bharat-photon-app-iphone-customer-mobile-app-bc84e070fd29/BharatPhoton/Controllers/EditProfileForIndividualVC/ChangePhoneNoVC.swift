//
//  ChangeEmailOrPhoneNoVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 29/11/24.
//

import UIKit

protocol ProfileFetchDataDelegate: AnyObject{
    func didUpdateProfileFetchData()
     
}
class ChangePhoneNoVC: UIViewController {
    @IBOutlet weak var verifyBtn: UIButton!
    @IBOutlet weak var doneBtn: UIButton!
    @IBOutlet weak var verifyOtp: UIView!
    @IBOutlet weak var phoneNoTF: UITextField!
    @IBOutlet weak var countryCodeLbl: UILabel!
    @IBOutlet weak var enterOtpTF: UITextField!
    @IBOutlet weak var popupView: UIView!
    private let maxPopupHeight: CGFloat = 450
    var delegate: ProfileFetchDataDelegate?
    var phoneNo = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        setupSlidingPopup(for: popupView, maxHeight: maxPopupHeight)
        verifyOtp.isHidden = true
        verifyBtn.isHidden = true
        doneBtn.isHidden = false
        self.phoneNoTF.text = phoneNo
        self.phoneNoTF.delegate = self
        self.enterOtpTF.delegate = self
        self.phoneNoTF.returnKeyType = .next
        self.enterOtpTF.returnKeyType = .done
      
    }
    @IBAction func hidePhoneNoPopup(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    @IBAction func doneBtnAction(_ sender: UIButton) {
        if phoneNoTF.text!.isEmpty{
            self.alert(message: "Please enter phone number")
        }
        else{
            serviceChangePhoneAPI()
        }
    }
    @IBAction func verifyBtnAction(_ sender: UIButton) {
        if phoneNoTF.text!.isEmpty{
            self.alert(message: "Please enter phone number")
        }
        else if enterOtpTF.text!.isEmpty{
            self.alert(message: "Please enter your 6 digit OTP")
        }
        else{
            serviceVerifyPhoneNoAPI()
        }
    }
}
extension ChangePhoneNoVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
           if textField == self.phoneNoTF {
            let maxLength = 10
            let allowedCharacters = "12304567890"
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
        if textField == self.phoneNoTF {
            self.enterOtpTF.becomeFirstResponder()
        }else if textField == self.enterOtpTF{
            self.enterOtpTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: ChangePhoneNo API ==>
extension ChangePhoneNoVC{
    func serviceChangePhoneAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["phoneNo": self.phoneNoTF.text!, "country_code": self.countryCodeLbl.text!,] as [String: Any]
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
// MARK: VerifyPhoneNo API
extension ChangePhoneNoVC{
    func serviceVerifyPhoneNoAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["phone_otp": self.enterOtpTF.text!] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyPhoneEmail", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.delegate?.didUpdateProfileFetchData()
                self.dismiss(animated: false, completion: nil)
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
 
