//
//  SignupForIndividualVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 18/06/24.
//

import UIKit
import CountryPickerView
import SDWebImage
 

class SignupForIndividualVC: UIViewController, CountryPickerViewDelegate, CountryPickerViewDataSource{
    @IBOutlet weak var profileImg: UIImageView!
    @IBOutlet weak var firstNameTF: UITextField!
    @IBOutlet weak var lastNameTF: UITextField!
    @IBOutlet weak var mobileNumberTF: UITextField!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var addressTF: UITextField!
    @IBOutlet weak var selectUnselectBtn: UIButton!
    @IBOutlet weak var countryCodeLbl: UILabel!
    let countryPickerView = CountryPickerView()
    var profileImgUrl = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        selectUnselectBtn.isSelected = true
        self.countryPickerView.delegate = self
        self.firstNameTF.autocapitalizationType = .sentences
       // self.lastNameTF.autocapitalizationType = .sentence
        self.firstNameTF.delegate = self
        self.lastNameTF.delegate = self
        self.mobileNumberTF.delegate = self
        self.emailTF.delegate = self
        self.addressTF.delegate = self
        self.firstNameTF.returnKeyType = .next
        self.lastNameTF.returnKeyType = .next
        self.emailTF.returnKeyType = .next
        self.mobileNumberTF.returnKeyType = .next
        self.addressTF.returnKeyType = .done
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    // MARK: back swipe gesture func ==>
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
    @IBAction func uploadProfilebtnAction(_ sender: UIButton)
    {
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
    @IBAction func termsOfUseBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "TermsAndConditionsVC") as! TermsAndConditionsVC
        vc.userType = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func privacyPolicyBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "PrivacyPolicyVC") as! PrivacyPolicyVC
        vc.userType = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func radioBtnTermsAndConditionAction(_ sender: UIButton) {
        if sender.isSelected == false
        {
            selectUnselectBtn.isSelected = true
        }
        else if sender.isSelected == true
        {
            selectUnselectBtn.isSelected = false
        }
    }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if  firstNameTF.text!.isEmpty{
            self.alert(message: "Please enter first name")
        }
        else if mobileNumberTF.text!.isEmpty{
            self.alert(message: "Please enter mobile number")
        }
        else if mobileNumberTF.text!.count != 10{
            self.alert(message: "Please enter valid mobile number")
        }
        else if emailTF.text!.isEmpty{
            self.alert(message: "Please enter email address")
        }
        else if !emailTF.text!.isValidEmail(){
            self.alert(message: "Enter valid email address")
        }
        else if addressTF.text!.isEmpty{
            alert(message: "Please enter contact address")
        }
        if selectUnselectBtn.isSelected == false{
            self.alert(message: "Accept of Terms Service and Privacy Policy")
        }
        else
        {
            serviceSignUpAPI()
        }
    }
}
extension SignupForIndividualVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.firstNameTF
        {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.lastNameTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"
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
        else if textField == self.emailTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.addressTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
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
            self.emailTF.becomeFirstResponder()
        }else if textField == self.emailTF{
            self.addressTF.becomeFirstResponder()
        }else if textField == addressTF{
            self.addressTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: SignUpAPI ==>
extension SignupForIndividualVC {
    func serviceSignUpAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": "1", "firstName": self.firstNameTF.text!, "lastName": self.lastNameTF.text!, "email": self.emailTF.text!, "profileImage": self.profileImgUrl, "phoneNo": self.mobileNumberTF.text!, "country_code": self.countryCodeLbl.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "register", params: parms, token: "") { JSONResponse in
            print("===> \(parms)")
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let token = JSONResponse["data"].stringValue
                UserDefaults.standard.set(token, forKey: "token")
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "OtpVerificationVC") as! OtpVerificationVC
                self.navigationController?.pushViewController(vc, animated: true)
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: "")
        }
    }
}
