//
//  InstallationNotAvailableAlertVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 03/07/24.
//

import UIKit

class InstallationNotAvailableAlertVC: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        
    }
    @IBAction func hidePopViewAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
 
    @IBAction func okayBtnAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    
}
