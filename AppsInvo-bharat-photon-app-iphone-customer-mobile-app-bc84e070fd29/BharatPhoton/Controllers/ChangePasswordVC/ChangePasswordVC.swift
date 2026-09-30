//
//  ChangePasswordVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 11/11/24.
//

import UIKit
 
class ChangePasswordVC: UIViewController {
    @IBOutlet weak var newPasswordTF: UITextField!
    @IBOutlet weak var oldPasswordTF: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func saveBtnAction(_ sender: UIButton) {
        if  oldPasswordTF.text!.isEmpty{
            self.alert(message: "Please old password")
        }
        else if newPasswordTF.text!.isEmpty{
            self.alert(message: "Please enter new password")
        }
        else{
            serviceChangePasswordAPI()
        }
    }
}
// MARK: ChangePassword API ==>
extension ChangePasswordVC{
    func serviceChangePasswordAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["password": self.newPasswordTF.text!, "oldPassword": self.oldPasswordTF.text!,] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "changePassword", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.alert(message: JSONResponse["message"].stringValue)
                self.navigationController?.popViewController(animated: true)
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
