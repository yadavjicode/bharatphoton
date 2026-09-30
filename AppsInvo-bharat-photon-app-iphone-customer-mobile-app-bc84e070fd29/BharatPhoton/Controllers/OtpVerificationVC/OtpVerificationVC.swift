//
//  OtpVerificationVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 18/06/24.
//

import UIKit
 

class OtpVerificationVC: UIViewController, UITextFieldDelegate {
    @IBOutlet weak var otp1tf: UITextField!
    @IBOutlet weak var otp2tf: UITextField!
    @IBOutlet weak var otp3tf: UITextField!
    @IBOutlet weak var otp4tf: UITextField!
    @IBOutlet weak var otp5tf: UITextField!
    @IBOutlet weak var otp6tf: UITextField!
    @IBOutlet weak var timeLbl: UILabel!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var resendCodeBtn: UIButton!
    @IBOutlet weak var donotReceiveCodeLbl: UILabel!
    var businessEmail = 0
    var mobileNumber = ""
    var email = ""
    var timer : Timer?
    var counter = 60
    var msg = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        donotReceiveCodeLbl.isHidden = true
        resendCodeBtn.isHidden = true
        otp1tf.delegate = self
        otp2tf.delegate = self
        otp3tf.delegate = self
        otp4tf.delegate = self
        otp5tf.delegate = self
        otp6tf.delegate = self
        
        otp1tf.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        otp2tf.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        otp3tf.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        otp4tf.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        otp5tf.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        otp6tf.addTarget(self, action: #selector(textFieldDidChange(textField:)), for: UIControl.Event.editingChanged)
        
        otp1tf.addTarget(self, action: #selector(keyboardInputShouldDelete(textField:)), for: UIControl.Event.editingChanged)
        otp2tf.addTarget(self, action: #selector(keyboardInputShouldDelete(textField:)), for: UIControl.Event.editingChanged)
        otp3tf.addTarget(self, action: #selector(keyboardInputShouldDelete(textField:)), for: UIControl.Event.editingChanged)
        otp4tf.addTarget(self, action: #selector(keyboardInputShouldDelete(textField:)), for: UIControl.Event.editingChanged)
        otp5tf.addTarget(self, action: #selector(keyboardInputShouldDelete(textField:)), for: UIControl.Event.editingChanged)
        otp6tf.addTarget(self, action: #selector(keyboardInputShouldDelete(textField:)), for: UIControl.Event.editingChanged)
        self.timer = Timer.scheduledTimer(timeInterval:1, target:self, selector:#selector(self.prozessTimer), userInfo: nil, repeats: true)
        
        if businessEmail == 1{
            self.lbl.text = "Please enter the OTP to verify your account. A OTP has been sent to \(email)"
        }else{
            self.lbl.text = "Please enter the OTP to verify your account. A OTP has been sent to \(mobileNumber)"
        }
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    // MARK: back swipe gesture func ==>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        // Handle the swipe gesture here
        if gestureRecognizer.direction == .right {
            // Perform the back action (e.g., pop to the previous view controller)
            navigationController?.popViewController(animated: true)
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @objc func prozessTimer() {
        counter -= 1
        if counter < 0
        {
            timer?.invalidate()
            timer = nil
            counter = 60
            resendCodeBtn.isHidden = false
            donotReceiveCodeLbl.isHidden = false
            timeLbl.isHidden = true
        }
        else
        {
            timeLbl.text! = "00:\(counter)"
            resendCodeBtn.isHidden = true
            donotReceiveCodeLbl.isHidden = true
            timeLbl.isHidden = false
        }
    }
    @IBAction func resendOtpBtnAction(_ sender: UIButton)
    {
        serviceResendOtpAPI()
    }
    @IBAction func verifyBtnAction(_ sender: UIButton) {
        if otp1tf.text!.isEmpty || otp2tf.text!.isEmpty || otp3tf.text!.isEmpty || otp4tf.text!.isEmpty || otp5tf.text!.isEmpty || otp6tf.text!.isEmpty{
            self.alert(message: "Please enter otp")
        }
        else{
            if businessEmail == 1{
                serviceEmailOtpVerifyAPI()
            }
            else{
                serviceVerifyOtpAPI()
            }
        }
    }
    @objc func keyboardInputShouldDelete(textField: UITextField) {
        let text = textField.text
        if (text?.utf16.count)! == 0{
            switch textField{
            case otp6tf:
                otp5tf.becomeFirstResponder()
            case otp5tf:
                otp4tf.becomeFirstResponder()
            case otp4tf:
                otp3tf.becomeFirstResponder()
            case otp3tf:
                otp2tf.becomeFirstResponder()
            case otp2tf:
                otp1tf.becomeFirstResponder()
            default:
                break
            }
        }else{
            
        }
    }
    @objc func textFieldDidChange(textField: UITextField) {
        let text = textField.text
        if (text?.utf16.count)! >= 1{
            switch textField{
            case otp1tf:
                otp2tf.text! = ""
                otp2tf.becomeFirstResponder()
            case otp2tf:
                otp3tf.text! = ""
                otp3tf.becomeFirstResponder()
            case otp3tf:
                otp4tf.text! = ""
                otp4tf.becomeFirstResponder()
            case otp4tf:
                otp5tf.text! = ""
                otp5tf.becomeFirstResponder()
            case otp5tf:
                otp6tf.text! = ""
                otp6tf.becomeFirstResponder()
            case otp6tf:
                 otp6tf.resignFirstResponder()
                 serviceVerifyOtpAPI()
            default:
                break
            }
        }else{
            
        }
    }
    //MARK: UITEXTFiledDelegate Methods
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        
        let currentString: NSString = textField.text! as NSString
        let newString: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
        if textField == self.otp1tf
        {
            let maxLength = 1
            let allowedCharacters = "1234567890 "
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        else if textField == self.otp2tf
        {
            let maxLength = 1
            let allowedCharacters = "1234567890 "
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        else if textField == self.otp3tf
        {
            let maxLength = 1
            let allowedCharacters = "1234567890 "
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        else if textField == self.otp4tf
        {
            let maxLength = 1
            let allowedCharacters = "1234567890 "
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        else if textField == self.otp5tf
        {
            let maxLength = 1
            let allowedCharacters = "1234567890 "
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        else if textField == self.otp6tf
        {
            let maxLength = 1
            let allowedCharacters = "1234567890 "
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString.length <= maxLength
        }
        return true
    }
}
// MARK: OtpVerification API ==>
extension OtpVerificationVC {
    func serviceVerifyOtpAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "auth_token") ?? ""
        let parms = ["type": 1, "phone_otp": self.otp1tf.text!+self.otp2tf.text!+self.otp3tf.text!+self.otp4tf.text!+self.otp5tf.text!+self.otp6tf.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyOtp", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let _id = dic["_id"]?.stringValue ?? ""
                let token = dic["token"]?.stringValue ?? ""
                UserDefaults.standard.set(token, forKey: "token")
                UserDefaults.standard.set(_id, forKey: "_id")
                UserDefaults.standard.removeObject(forKey: "guestUser")
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                let sceneDelegate = windowScene.delegate as? SceneDelegate {
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                let tabVC = storyboard.instantiateViewController(withIdentifier: "TabVC") as! TabVC
                let nav = UINavigationController(rootViewController: tabVC)
                sceneDelegate.window?.rootViewController = nav
                sceneDelegate.window?.makeKeyAndVisible()
                }
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
// MARK: EmailOtpVerify API ==>
extension OtpVerificationVC {
    func serviceEmailOtpVerifyAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "auth_token") ?? ""
        let parms = ["email_otp": self.otp1tf.text!+self.otp2tf.text!+self.otp3tf.text!+self.otp4tf.text!+self.otp5tf.text!+self.otp6tf.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "verifyOtp", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                UserDefaults.standard.removeObject(forKey: "guestUser")
                if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
                let sceneDelegate = windowScene.delegate as? SceneDelegate {
                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                let tabVC = storyboard.instantiateViewController(withIdentifier: "TabVC") as! TabVC
                let nav = UINavigationController(rootViewController: tabVC)
                sceneDelegate.window?.rootViewController = nav
                sceneDelegate.window?.makeKeyAndVisible()
            }
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: "OTP has been resent. Please check your email")
        }
    }
}
// MARK: resendOtp API ==>
extension OtpVerificationVC {
    func serviceResendOtpAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["phoneNo":self.mobileNumber] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "resendOtp", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.alert(message: JSONResponse["message"].stringValue)
                self.timer = Timer.scheduledTimer(timeInterval:1, target:self, selector:#selector(self.prozessTimer), userInfo: nil, repeats: true)
            default:
                print("Unidentified code found")
                break
            }
        }failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            //self.alert(message: JSONResponse["message"].stringValue, title: Error)
            self.alert(message: "OTP has been resent. Please check your phone")
        }
    }
}
