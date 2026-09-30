//
//  EnquireNowVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 09/06/25.
//

import UIKit
import CountryPickerView


class EnquireNowVC: UIViewController, UITextFieldDelegate, CountryPickerViewDelegate, CountryPickerViewDataSource {
    @IBOutlet weak var enterNameTF: UITextField!
    @IBOutlet weak var enterEmailTF: UITextField!
    @IBOutlet weak var enterPhoneNoTF: UITextField!
    @IBOutlet weak var enterProductInterestTF: UITextField!
    @IBOutlet weak var countryCodeLbl: UILabel!
    let countryPickerView = CountryPickerView()
    var categoryName = ""
    var categoryId = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        countryPickerView.delegate = self
        enterPhoneNoTF.delegate = self
        enterProductInterestTF.text = categoryName
    }
    @IBAction func crossBtnAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    //    MARK: KeyboardReturnButton Cilck KeyBoardHide
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    //MARK: UITEXTFiledDelegate Methods
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.enterPhoneNoTF
        {
            let maxLength = 10
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        return true
    }
    @IBAction func countryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
    }
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        self.countryCodeLbl.text! = country.phoneCode
    }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if enterNameTF.text!.isEmpty{
            self.alert(message: "Please enter your name")
        }
        else if enterEmailTF.text!.isEmpty{
            self.alert(message: "Please enter your email")
        }
        else if !enterEmailTF.text!.isValidEmail(){
            self.alert(message: "Enter valid email address")
        }
        if enterPhoneNoTF.text!.isEmpty{
            self.alert(message: "Please enter mobile number")
        }
        else if enterPhoneNoTF.text!.count != 10 {
            self.alert(message: "Please enter valid mobile number")
        }
        else if enterProductInterestTF.text!.isEmpty{
            self.alert(message: "Please enter product")
        }else{
            serviceAddQuoteAPI()
        }
    }
}
// MARK: serviceAddQuote API ==>
extension EnquireNowVC {
    func serviceAddQuoteAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["categoryId": categoryId, "countryCode": "+91", "email": enterEmailTF.text!, "fullName": enterNameTF.text!, "phoneNo": enterPhoneNoTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addQuote", params: parms, token: token) { JSONResponse in
            print(JSONResponse)
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
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
