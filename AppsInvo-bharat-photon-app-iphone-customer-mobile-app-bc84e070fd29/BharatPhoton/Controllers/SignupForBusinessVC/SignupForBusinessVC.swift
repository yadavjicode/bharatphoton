//
//  SignupForBusinessVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 18/06/24.
//

import UIKit
import CountryPickerView
 
class SignupForBusinessVC: UIViewController, CountryPickerViewDelegate, CountryPickerViewDataSource{
    @IBOutlet weak var profileImg: UIImageView!
    @IBOutlet weak var businessCompanyNameTF: UITextField!
    @IBOutlet weak var businessContactNumberTF: UITextField!
    @IBOutlet weak var businessEmailAddressTF: UITextField!
    @IBOutlet weak var gstNumberTF: UITextField!
    @IBOutlet weak var uploadCertificateImg: UIImageView!
    @IBOutlet weak var contactPersonNameTF: UITextField!
    @IBOutlet weak var designationTF: UITextField!
    @IBOutlet weak var contactPersonMobileNoTF: UITextField!
    @IBOutlet weak var selectUnselectBtn: UIButton!
    @IBOutlet weak var gstCertificateImg: UIImageView!
    @IBOutlet weak var passwordTF: UITextField!
    @IBOutlet weak var countryCodeLbl: UILabel!
    let countryPickerView = CountryPickerView()
    var profileImgUrl = ""
    var gstCertificateImgUrl = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        selectUnselectBtn.isSelected = true
        self.countryPickerView.delegate = self
        self.businessCompanyNameTF.autocapitalizationType = .sentences
        self.gstNumberTF.autocapitalizationType = .sentences
        self.contactPersonNameTF.autocapitalizationType = .sentences
        self.designationTF.autocapitalizationType = .sentences
        self.businessCompanyNameTF.delegate = self
        self.businessContactNumberTF.delegate = self
        self.businessEmailAddressTF.delegate = self
        self.gstNumberTF.delegate = self
        self.contactPersonNameTF.delegate = self
        self.designationTF.delegate = self
        self.contactPersonMobileNoTF.delegate = self
        self.businessCompanyNameTF.returnKeyType = .next
        self.businessContactNumberTF.returnKeyType = .next
        self.businessEmailAddressTF.returnKeyType = .next
        self.gstNumberTF.returnKeyType = .next
        self.contactPersonNameTF.returnKeyType = .next
        self.designationTF.returnKeyType = .next
        self.contactPersonMobileNoTF.returnKeyType = .done
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
    @IBAction func termsOfUseBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "TermsAndConditionsVC") as! TermsAndConditionsVC
        //vc.userType = 2
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func privacyPolicyBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "PrivacyPolicyVC") as! PrivacyPolicyVC
        //vc.userType = 2
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if  businessCompanyNameTF.text!.isEmpty{
            self.alert(message: "Please enter business company name")
        }
        else if businessContactNumberTF.text!.isEmpty{
            self.alert(message: "Please enter business contact number")
        }
        else if businessContactNumberTF.text!.count != 10{
            self.alert(message: "Please enter valid business contact number")
        }
        else if businessEmailAddressTF.text!.isEmpty{
            self.alert(message: "Please enter business email address")
        }
        else if !businessEmailAddressTF.text!.isValidEmail(){
            self.alert(message: "Please enter valid business email address")
        }
        else if gstNumberTF.text!.isEmpty{
            self.alert(message: "Please enter gst number")
        }
        else if contactPersonNameTF.text!.isEmpty{
            self.alert(message: "Please enter contact person name")
        }
        else if designationTF.text!.isEmpty{
            self.alert(message: "Please enter designation")
        }
        else if contactPersonMobileNoTF.text!.isEmpty{
            self.alert(message: "Please enter contact person mobile number")
        }
        else if contactPersonMobileNoTF.text!.count != 10{
            self.alert(message: "Please enter valid contact person mobile number")
        }
        else if passwordTF.text!.isEmpty{
            self.alert(message: "Please enter password")
        }
        if selectUnselectBtn.isSelected == false{
            self.alert(message: "Accept of Terms Service and Privacy Policy")
        }
        else
        {
            self.serviceBusinessSignUpAPI()
        }
    }
}
extension SignupForBusinessVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.businessCompanyNameTF
        {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.businessContactNumberTF {
            let maxLength = 10
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.businessEmailAddressTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.gstNumberTF {
            let maxLength = 15
            let allowedCharacters = "1234567890qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.contactPersonNameTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.designationTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.contactPersonMobileNoTF {
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
        if textField == self.businessCompanyNameTF {
            self.businessContactNumberTF.becomeFirstResponder()
        }else if textField == self.businessContactNumberTF{
            self.businessEmailAddressTF.becomeFirstResponder()
        }else if textField == self.businessEmailAddressTF {
            self.gstNumberTF.becomeFirstResponder()
        }else if textField == self.gstNumberTF{
            self.contactPersonNameTF.becomeFirstResponder()
        }else if textField == contactPersonNameTF{
            self.designationTF.becomeFirstResponder()
        }else if textField == designationTF{
            self.contactPersonMobileNoTF.becomeFirstResponder()
        }else if textField == contactPersonMobileNoTF{
            self.contactPersonMobileNoTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: SignUpAPI ==>
extension SignupForBusinessVC {
    func serviceBusinessSignUpAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": "2", "profileImage": self.profileImgUrl, "companyName": self.businessCompanyNameTF.text!, "businessEmail": self.businessEmailAddressTF.text!, "password": self.passwordTF.text!, "businessPhoneNo": self.businessContactNumberTF.text!, "gstNumber": self.gstNumberTF.text!, "gstFile": self.gstCertificateImgUrl, "contactPerson": self.contactPersonNameTF.text!, "designation":self.designationTF.text!,"contactNo": self.contactPersonMobileNoTF.text!, "country_code": self.countryCodeLbl.text!, "contactPersoncountryCode": "+91"] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "register", params: parms, token: "") { JSONResponse in
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

