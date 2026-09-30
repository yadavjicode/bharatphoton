//
//  WelcomeVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/06/24.
//

import UIKit

class WelcomeVC: UIViewController {
    @IBOutlet weak var arrowBtn: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        startPulsingAnimation(on: arrowBtn)
    }
    @IBAction func getStartedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    func startPulsingAnimation(on button: UIButton) {
        let pulseAnimation = CABasicAnimation(keyPath: "transform.scale")
        pulseAnimation.duration = 0.8
        pulseAnimation.fromValue = 1.5
        pulseAnimation.toValue = 1.1
        pulseAnimation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.easeInEaseOut)
        pulseAnimation.autoreverses = true
        pulseAnimation.repeatCount = .infinity
        button.layer.add(pulseAnimation, forKey: "pulsing")
    }
}
