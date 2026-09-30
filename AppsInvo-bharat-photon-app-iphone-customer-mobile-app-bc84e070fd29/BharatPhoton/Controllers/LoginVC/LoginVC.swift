//
//  LoginVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/06/24.


import UIKit
import AuthenticationServices
import GoogleSignIn
import FBSDKLoginKit
import CountryPickerView
 

class LoginVC: UIViewController, UITextFieldDelegate, CountryPickerViewDelegate, CountryPickerViewDataSource{
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var mobileNumberTf: UITextField!
    @IBOutlet weak var flagImg: UIImageView!
    @IBOutlet weak var countryNameLbl: UILabel!
    @IBOutlet weak var countryCodeLbl: UILabel!
    let countryPickerView = CountryPickerView()
    var guestUser = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        lbl.text = "Please enter your mobile number to proceed further"
        mobileNumberTf.delegate = self
        GIDSignIn.sharedInstance()?.delegate = self
        GIDSignIn.sharedInstance()?.presentingViewController = self
        countryPickerView.delegate = self
        let placeholderText = "Phone Number"
        let attributes: [NSAttributedString.Key: Any] = [.foregroundColor: UIColor.gray]
        let attributedPlaceholder = NSAttributedString(string: placeholderText, attributes: attributes)
        mobileNumberTf.attributedPlaceholder = attributedPlaceholder
    }
    @IBAction func guestUserAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "TabVC") as! TabVC
        vc.guestUser = self.guestUser
        UserDefaults.standard.set(self.guestUser, forKey: "guestUser")
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func countryAction(_ sender: UIButton) {
        countryPickerView.showCountriesList(from: self)
    }
    func countryPickerView(_ countryPickerView: CountryPickerView, didSelectCountry country: Country) {
        self.flagImg.image = country.flag
        self.countryNameLbl.text = "\(country.name) (\(country.code))"
        self.countryCodeLbl.text! = country.phoneCode
    }
    @IBAction func businessTabClickAction(_ sender: UIButton) {
        UserDefaults.standard.removeObject(forKey: "guestUser")
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "BusinessLoginWithEmailVC") as! BusinessLoginWithEmailVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func loginBtnAction(_ sender: UIButton) {
        if  mobileNumberTf.text!.isEmpty{
            self.alert(message: "Please enter phone number")
        }
        else if mobileNumberTf.text!.count != 10{
            self.alert(message: "Please enter valid phone number")
        }
        else{
            serviceLoginApi()
        }
    }
    @IBAction func googleLoginBtnAction(_ sender: UIButton) {
        GIDSignIn.sharedInstance()?.signIn()
    }
    func showAlert(title : String, message : String){
        let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let alertAction = UIAlertAction(title: "OK", style: .default) { (action) in
            alert.dismiss(animated: true, completion: nil)
        }
        alert.addAction(alertAction)
        self.present(alert, animated: false, completion: nil)
    }
    @IBAction func faceBookLoginBtnAction(_ sender: UIButton) {
        let fbLoginManager : LoginManager = LoginManager()
        fbLoginManager.logIn(permissions:["email","public_profile"], from: self, handler: {(result, error) -> Void in
            print("\n\n result: \(String(describing: result))")
            print("\n\n Error: \(String(describing: error))")
            if (error == nil)
            {
                if let fbloginresult = result
                {
                    if(fbloginresult.isCancelled)
                    {
                        //Show Cancel alert to the user
                        let alert = UIAlertController(title: "Facebook login", message: "The user canceled the sign-in flow.", preferredStyle: UIAlertController.Style.alert)
                        alert.addAction(UIAlertAction(title: "OK", style: UIAlertAction.Style.default, handler: {    (action:UIAlertAction!) in
                            //print("you have pressed the ok button")

                        }))
                        self.present(alert, animated: true, completion: nil)
                    }
                    else
                    {
                        print("going to getFBLoggedInUserData.. ")
                        self.getFBUserData()
                    }
                }
            }
        })
    }
    @IBAction func appleLoginBtnAction(_ sender: UIButton) {
        UserDefaults.standard.removeObject(forKey: "guestUser")
        performAppleSignIn()
    }
    func performAppleSignIn() {
        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.email, .fullName] // You can specify the requested user data here
        
        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = self
        authorizationController.presentationContextProvider = self
        authorizationController.performRequests()
    }
    @IBAction func createAccountBtnAction(_ sender: UIButton) {
        UserDefaults.standard.removeObject(forKey: "guestUser")
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "SignupForIndividualVC") as! SignupForIndividualVC
        self.navigationController?.pushViewController(vc, animated: true)
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
        
        if textField == self.mobileNumberTf
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
}
extension LoginVC: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
            let userIdentifier = appleIDCredential.user
            // Use the user's data for app-specific purposes
            print("User ID: \(userIdentifier)")
            UserDefaults.standard.set(userIdentifier, forKey: "appleUserId")
        }
    }
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        // Handle errors here
        print("Apple Sign In Error: \(error.localizedDescription)")
    }
}
extension LoginVC: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return self.view.window!
    }
}
extension LoginVC : GIDSignInDelegate {
    func sign(_ signIn: GIDSignIn!, didSignInFor user: GIDGoogleUser!, withError error: Error!) {
        if let error = error {
            if (error as NSError).code == GIDSignInErrorCode.hasNoAuthInKeychain.rawValue {
                print("The user has not signed in before or they have since signed out.")
            } else {
                print("\(error.localizedDescription)")
            }
            return
        }
        // Get the Google ID (userId)
        let googleId = user.userID
        debugPrint("Google ID: \(googleId ?? "")")
        // Other user info
        debugPrint(user.profile.email ?? "")
        debugPrint(user.profile.name ?? "")
        debugPrint(user.profile.givenName ?? "")
        debugPrint(user.profile.familyName ?? "")
        debugPrint(user.profile.imageURL(withDimension: 100) ?? "")
        self.serviceGoogleSocialLoginAPI(email: user.profile.email ?? "", googleId: googleId ?? "", profileImage: user.profile.imageURL(withDimension: 100)?.absoluteString ?? "", firstName: user.profile.givenName ?? "", lastName: user.profile.familyName ?? "")
    }
    func sign(_ signIn: GIDSignIn!, didDisconnectWith user: GIDGoogleUser!, withError error: Error!) {
        // Handle the disconnection if needed
    }
    func sign(_ signIn: GIDSignIn!, present viewController: UIViewController!) {
        self.present(viewController, animated: true, completion: nil)
    }
    func sign(_ signIn: GIDSignIn!, dismiss viewController: UIViewController!) {
        self.dismiss(animated: true, completion: nil)
    }
}
// MARK: Facebook Extension
extension LoginVC: LoginButtonDelegate{
    func loginButton(_ loginButton: FBLoginButton, didCompleteWith result: LoginManagerLoginResult?, error: Error?) {
        let token = result?.token?.tokenString
        
        let request = FBSDKLoginKit.GraphRequest(graphPath: "me", parameters: ["fields": "id, email, first_name, last_name, picture, short_name, name, middle_name, name_format,age_range"], tokenString: token, version: nil, httpMethod: .get)
        request.start { (connection, result, error) in
            print("\(result ?? "")")
        }
    }
    func loginButtonDidLogOut(_ loginButton: FBLoginButton) {
        print("Logout")
    }
    func getFBUserData()
    {
        if((AccessToken.current) != nil)
        {
            
            let graphRequest : GraphRequest = GraphRequest(graphPath: "me", parameters: ["fields":"email,first_name,last_name,name,picture.type(normal)"])
            graphRequest.start { connection, result, error in
                if error == nil {
                    if let data = result as? NSDictionary {
//                        let firstName  = data.object(forKey: "first_name") as? String ?? ""
//                        let lastName = data.object(forKey: "last_name") as? String ?? ""
//                        let email = data.object(forKey: "email") as? String ?? ""
//                        let oauth_provider = "Facebook"
                        let userID = data.object(forKey: "id") as? String ?? ""
//                        let type = "facebook"
//                        let devicetype = "I"
                        
//                          self.serviceFacebookSocialLoginAPI(social_type: "F", apple_id: "", google_id: "", facebook_id: userID, first_name: firstName, last_name: lastName, profile_pic: "", email: email)
                        self.serviceFacebookSocialLoginAPI(facebookId: userID)
                    }
                }
            }
            
        }
    }
}
// MARK: LoginAPI ==>
extension LoginVC {
    func serviceLoginApi(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["country_code": self.countryCodeLbl.text!, "phoneNo": self.mobileNumberTf.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "login", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                UserDefaults.standard.removeObject(forKey: "guestUser")
                let dic = JSONResponse["data"].dictionaryValue
                let token = dic["auth_token"]?.stringValue ?? ""
                UserDefaults.standard.set(token, forKey: "auth_token")
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "OtpVerificationVC") as! OtpVerificationVC
                vc.mobileNumber = self.mobileNumberTf.text!
                self.navigationController?.pushViewController(vc, animated: true)
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: "User record not found")
            //self.alert(message: JSONResponse["message"].stringValue, title: "Error")
         }
    }
}
// MARK: SocialGoogleLoginAPI ==>
extension LoginVC {
    func serviceGoogleSocialLoginAPI(email: String, googleId: String, profileImage: String, firstName: String, lastName: String ){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": "1", "email": email, "googleId": googleId, "socialType": "G", "deviceType": "ios", "profileImage": profileImage, "firstName": firstName, "lastName": lastName] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "socialLogin", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                UserDefaults.standard.removeObject(forKey: "guestUser")
                let dic = JSONResponse["data"].dictionaryValue
                let token = dic["auth_token"]?.stringValue ?? ""
                let _id = dic["_id"]?.stringValue ?? ""
                UserDefaults.standard.set(_id, forKey: "_id")
                UserDefaults.standard.set(token, forKey: "token")
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
// MARK: SocialFaceBookLoginAPI ==>
extension LoginVC {
    func serviceFacebookSocialLoginAPI(facebookId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["userType": "1", "facebookId": facebookId, "socialType": "F", "deviceType": "ios" ] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "socialLogin", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                UserDefaults.standard.removeObject(forKey: "guestUser")
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
 
 
