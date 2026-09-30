//
//  ThanksPurchasePlanVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 04/07/24.
//

import UIKit
import DropDown
 
class ThanksPurchasePlanVC: UIViewController {
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var orderNoLbl: UILabel!
    @IBOutlet weak var addressTF: UITextField!
    @IBOutlet weak var zipCodeTF: UITextField!
    @IBOutlet weak var selectStateTF: UITextField!
    @IBOutlet weak var selectCityTF: UITextField!
    @IBOutlet weak var selectUnselectBtn: UIButton!
    @IBOutlet weak var stateBtn: UIButton!
    @IBOutlet weak var cityBtn: UIButton!
    var stateList: StateModal?
    var cityList: CityModal?
    var dropDown = DropDown()
    var uuid = ""
    var orderId = ""
    var city = ""
    var state = ""
    var zipCode = ""
    var address = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        serviceStateListAPI()
        serviceCityListAPI(stateName: state)
        startPulsingAnimation(on: img)
        self.orderNoLbl.text = uuid
        self.addressTF.text = address
        self.zipCodeTF.text = zipCode
        self.selectStateTF.text = state
        self.selectCityTF.text = city
        self.addressTF.delegate = self
        self.zipCodeTF.delegate = self
        self.selectCityTF.delegate = self
        self.selectStateTF.delegate = self
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
    func startPulsingAnimation(on image: UIImageView) {
        let pulseAnimation = CABasicAnimation(keyPath: "transform.scale")
        pulseAnimation.duration = 0.8
        pulseAnimation.fromValue = 1.5
        pulseAnimation.toValue = 1.1
        pulseAnimation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.easeInEaseOut)
        pulseAnimation.autoreverses = true
        pulseAnimation.repeatCount = .infinity
        image.layer.add(pulseAnimation, forKey: "pulsing")
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func tapSelectState(_ sender: UIButton) {
        guard let states = stateList?.data else {
            print("State list data is not available.")
            return
        }
        dropDown.dataSource = states
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            self?.selectStateTF.text = states[index]
            self?.serviceCityListAPI(stateName: self?.selectStateTF.text ?? "")
        }
    }
    @IBAction func tapSelectCity(_ sender: UIButton) {
        guard let cities = cityList?.data else {
            print("City list data is not available.")
            return
        }
        dropDown.dataSource = cities
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            self?.selectCityTF.text = cities[index]
        }
    }
    @IBAction func nextBtnAction(_ sender: UIButton) {
        if addressTF.text!.isEmpty{
            self.alert(message: "Please enter your address")
        }
        else if zipCodeTF.text!.isEmpty{
            self.alert(message: "Please enter zipcode")
        }
        else if zipCodeTF.text!.count != 6{
            self.alert(message: "Please enter valid zipcode")
        }
        else if selectStateTF.text!.isEmpty{
            self.alert(message: "Please select state")
        }
        else if selectCityTF.text!.isEmpty{
            self.alert(message: "Please select city")
        }
        else{
            self.serviceAddInstallationAddressAPI()
        }
    }
    @IBAction func selectUnselectBtnAction(_ sender: UIButton) {
        if sender.isSelected == false
        {
            selectUnselectBtn.isSelected = true
            addressTF.isUserInteractionEnabled = false
            zipCodeTF.isUserInteractionEnabled = false
            selectStateTF.isUserInteractionEnabled = false
            selectCityTF.isUserInteractionEnabled = false
            addressTF.text! = address
            zipCodeTF.text! = zipCode
            selectStateTF.text! = state
            selectCityTF.text! = city
            stateBtn.isHidden = true
            cityBtn.isHidden = true
        }
        else if sender.isSelected == true
        {
            selectUnselectBtn.isSelected = false
            addressTF.isUserInteractionEnabled = true
            zipCodeTF.isUserInteractionEnabled = true
            selectStateTF.isUserInteractionEnabled = true
            selectCityTF.isUserInteractionEnabled = true
            stateBtn.isHidden = false
            cityBtn.isHidden = false
        }
    }
}
extension ThanksPurchasePlanVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        
         if textField == self.addressTF {
            let maxLength = 100
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnm QWERTYUIOPASDFGHJKLZXCVBNM 123056890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.zipCodeTF {
            let maxLength = 6
            let allowedCharacters = "1234567890"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.selectStateTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        else if textField == self.selectCityTF {
            let maxLength = 20
            let allowedCharacters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM"
            let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
            let typedCharacterSet = CharacterSet(charactersIn: string)
            let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
            
            return alphabet && newString_.length <= maxLength
        }
        return false
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if textField == self.addressTF {
            self.zipCodeTF.becomeFirstResponder()
        }else if textField == self.zipCodeTF{
            self.selectStateTF.becomeFirstResponder()
        }else if textField == self.selectStateTF {
            self.selectCityTF.becomeFirstResponder()
        }else if textField == self.selectCityTF{
            self.selectCityTF.resignFirstResponder()
        }
        return true
    }
}
// MARK: AddInstallation API ==>
extension ThanksPurchasePlanVC {
    func serviceAddInstallationAddressAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["orderId": self.orderId, "address": self.addressTF.text!, "ZipCode": self.zipCodeTF.text!, "state": self.selectStateTF.text!, "city": self.selectCityTF.text!] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addInstallationAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let  vc = self.storyboard?.instantiateViewController(withIdentifier: "ThanksPurchasePlaneUploadSitePhotoVC") as! ThanksPurchasePlaneUploadSitePhotoVC
                vc.orderId = self.orderId
                vc.uuid = self.uuid
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
//MARK: StateList API ==>
extension ThanksPurchasePlanVC {
    func serviceStateListAPI(){
        LoaderManager.shared.hideLoader()
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "stateListing", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.stateList = try JSONDecoder().decode(StateModal.self, from: JSONResponse.rawData())
                } catch {
                    print("Error decoding JSON: \(error)")
                }
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
//MARK: CityListAPI ==>
extension ThanksPurchasePlanVC {
    func serviceCityListAPI(stateName: String){
        LoaderManager.shared.hideLoader()
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let text = stateName.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cityListing?stateName=\(text)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.cityList = try JSONDecoder().decode(CityModal.self, from: JSONResponse.rawData())
                } catch {
                    print("Error decoding JSON: \(error)")
                }
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
