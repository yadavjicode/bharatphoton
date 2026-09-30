//
//  ForgotPasswordVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/06/24.
//

import UIKit
import CountryPickerView
 
class ForgotPasswordVC: UIViewController, CountryPickerViewDelegate, CountryPickerViewDataSource {
    @IBOutlet weak var view1: UIView!
    @IBOutlet weak var view2: UIView!
    @IBOutlet weak var countryCodeLbl: UILabel!
    @IBOutlet weak var enterEmailTF: UITextField!
    let countryPickerView = CountryPickerView()
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        countryPickerView.delegate = self
        view1.isHidden = false
        view2.isHidden = true
        enterEmailTF.delegate = self
        enterEmailTF.returnKeyType = .done
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        let placeholderText = "Enter Email / Phone No."
        let attributes: [NSAttributedString.Key: Any] = [.foregroundColor: UIColor.gray]
        let attributedPlaceholder = NSAttributedString(string: placeholderText, attributes: attributes)
        enterEmailTF.attributedPlaceholder = attributedPlaceholder
    }
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        if gestureRecognizer.direction == .right {
            navigationController?.popViewController(animated: true)
        }
    }
    @IBAction func countryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
    }
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        self.countryCodeLbl.text! = country.phoneCode
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func continueBtnAction(_ sender: UIButton) {
        let emailOrPhone = enterEmailTF.text!
        if emailOrPhone.isEmpty {
            self.alert(message: "Please enter email or phone number")
        } else {
            if emailOrPhone.contains("@") {
                if !emailOrPhone.isValidEmail() {
                    self.alert(message: "Please enter a valid email")
                } else {
                    print("Entered email: \(emailOrPhone)")
                    self.serviceForgotPasswordAPI()
                }
            } else {
                print("Entered phone number or username: \(emailOrPhone)")
                serviceForgotPasswordPhoneAPI()
            }
        }
    }
}
extension ForgotPasswordVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString = currentString.replacingCharacters(in: range, with: string)
        // Check if the new string is numeric (potential mobile number)
        let isNumeric = CharacterSet.decimalDigits.isSuperset(of: CharacterSet(charactersIn: newString))
        // Toggle views based on input
        if isNumeric {
            view1.isHidden = true
            view2.isHidden = false
        } else {
            view1.isHidden = false
            view2.isHidden = true
        }
        // Character set restriction
        let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
        let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
        let typedCharacterSet = CharacterSet(charactersIn: string)
        let isValidCharacters = allowedCharacterSet.isSuperset(of: typedCharacterSet)
        // Limit to 10 if numeric (mobile), 50 otherwise
        let maxLength = isNumeric ? 10 : 50
        return isValidCharacters && newString.count <= maxLength
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        self.enterEmailTF.resignFirstResponder()
        return true
    }
}
// MARK: ForgotPassword API ==>
extension ForgotPasswordVC {
    func serviceForgotPasswordAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": "2", "email": self.enterEmailTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "forgetPassword", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.alert(message: "Otp sent On your email")
                //  let token = JSONResponse["data"].stringValue
                //  UserDefaults.standard.set(token, forKey: "token")
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
extension ForgotPasswordVC {
    func serviceForgotPasswordPhoneAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": "2", "phoneNo": self.enterEmailTF.text!, "country_code": countryCodeLbl.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "forgetPassword", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.alert(message: JSONResponse["message"].stringValue)
                // let token = JSONResponse["data"].stringValue
                // UserDefaults.standard.set(token, forKey: "token")
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
