//
//  LogOutPopupVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 24/07/24.
//

import UIKit
protocol LogOutPopupDelegate: AnyObject {
    func didConfirmLogout()
}
class LogOutPopupVC: UIViewController {
    weak var delegate: LogOutPopupDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
    }
    @IBAction func logOutAction(_ sender: UIButton) {
        UserDefaults.standard.removeObject(forKey: "token")
        UserDefaults.standard.removeObject(forKey: "email")
        UserDefaults.standard.removeObject(forKey: "selectedIndex")
        UserDefaults.standard.removeObject(forKey: "_id")
        UserDefaults.standard.removeObject(forKey: "installationCheck")
        UserDefaults.standard.removeObject(forKey: "installationUnCheck")
        UserDefaults.standard.removeObject(forKey: "userType")
        delegate?.didConfirmLogout()
        self.dismiss(animated: true, completion: nil)
        
    }
    @IBAction func cancelAction(_ sender: UIButton) {
        self.dismiss(animated: true, completion: nil)
    
    }
}
