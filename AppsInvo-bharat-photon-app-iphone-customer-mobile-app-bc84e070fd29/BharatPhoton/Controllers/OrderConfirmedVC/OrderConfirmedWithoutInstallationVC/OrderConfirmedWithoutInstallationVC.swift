//
//  OrderConfirmedWithoutInstallationVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 04/07/24.
//

import UIKit

class OrderConfirmedWithoutInstallationVC: UIViewController {
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var colourChangeLbl: UILabel!
    @IBOutlet weak var orderNoLbl: UILabel!
    var uuid = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        startPulsingAnimation(on: img)
        orderNoLbl.text! = self.uuid
        let attributedString = NSMutableAttributedString(string: "Your Order is Confirmed!")
        // Set the color for the last 5 characters
        let lastPartRange = NSRange(location: attributedString.length - 10, length: 10)
        attributedString.addAttribute(NSAttributedString.Key.foregroundColor, value: UIColor(red: 255/255, green: 141/255, blue: 58/255, alpha: 1), range: lastPartRange)
        // Set the attributed string to the label
        colourChangeLbl.attributedText = attributedString
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
    @IBAction func backToHomeAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(identifier: "TabVC") as? TabVC
        self.navigationController?.pushViewController(vc!, animated: false)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            vc?.selectedIndex = 0
        }
    }
}


