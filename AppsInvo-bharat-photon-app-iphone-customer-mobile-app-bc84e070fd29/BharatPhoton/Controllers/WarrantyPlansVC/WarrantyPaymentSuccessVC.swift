//
//  WarrantyPaymentSuccessVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 13/12/24.
//

import UIKit

class WarrantyPaymentSuccessVC: UIViewController {
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var colourChangeLbl: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        startPulsingAnimation(on: img)
        let attributedString = NSMutableAttributedString(string: "Your Product Warranty is Updated Successfully!")
        // Set the color for the last 5 characters
        let lastPartRange = NSRange(location: attributedString.length - 13, length: 13)
        attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor(red: 255/255, green: 141/255, blue: 58/255, alpha: 1), range: lastPartRange)
        // Set the attributed string to the label
        colourChangeLbl.attributedText = attributedString
        
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
    @IBAction func backtohomeAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(identifier: "TabVC") as? TabVC
        self.navigationController?.pushViewController(vc!, animated: false)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            vc?.selectedIndex = 0
        }
    }
}


