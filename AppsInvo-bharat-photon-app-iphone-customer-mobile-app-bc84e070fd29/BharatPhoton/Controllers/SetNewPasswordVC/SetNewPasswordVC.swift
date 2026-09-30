//
//  SetNewPasswordVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 18/06/24.
//

import UIKit
 

class SetNewPasswordVC: UIViewController {
    @IBOutlet weak var newPasswordTF: UITextField!
    @IBOutlet weak var confirmNewPasswordTF: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        self.newPasswordTF.delegate = self
        self.confirmNewPasswordTF.delegate = self
        self.newPasswordTF.returnKeyType = .next
        self.confirmNewPasswordTF.returnKeyType = .done
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
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func saveBtnAction(_ sender: UIButton) {
        if  newPasswordTF.text!.isEmpty{
            self.alert(message: "Please enter new password")
        }
        else if confirmNewPasswordTF.text!.isEmpty{
            self.alert(message: "Please confirm new password")
        }
        else{
            alert(message: "Changed Password Successfully")
        }
    }
}
extension SetNewPasswordVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
           if textField == self.newPasswordTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[].,"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.confirmNewPasswordTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890!#$%&'*+-/=?~!@#$%^&*()_`:(){}[]"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.newPasswordTF {
            self.confirmNewPasswordTF.becomeFirstResponder()
        }else if textField == self.confirmNewPasswordTF{
            self.confirmNewPasswordTF.resignFirstResponder()
        }
        return true
    }
}
extension SetNewPasswordVC {
    func serviceForgotPasswordPhoneAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["password":self.confirmNewPasswordTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "resetPassword", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.alert(message: JSONResponse["message"].stringValue)
//                let token = JSONResponse["data"].stringValue
//                UserDefaults.standard.set(token, forKey: "token")
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
