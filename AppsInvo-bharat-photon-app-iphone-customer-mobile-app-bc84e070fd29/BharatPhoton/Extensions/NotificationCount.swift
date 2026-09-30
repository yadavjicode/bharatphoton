//
//  NotificationCount.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 24/12/24.
//

 

import Foundation
import UIKit

class NotificationCount {
    static let shared = NotificationCount()

    private init() {}

    // Function to fetch cart count and update the badge
    func fetchNotificationCount(for notificationBtn: UIButton, completion: @escaping (Int?) -> Void) {
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "notificationCount", params: parms, token: token) { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                completion(nil)  // Handle error case
            case 1:
                let notificationCount = JSONResponse["count"].intValue
                if notificationCount == 0{
                 print("Notification Empty")
                }
                else{
                    self.addNotificationBadgeToIcon(notificationCount: notificationCount, notificationBtn: notificationBtn)
                    completion(notificationCount)
                }
            default:
                completion(nil)
                print("Unidentified code found")
            }
        } failure: { Error, JSONResponse in
            completion(nil)
        }
    }

    // Function to add badge to the cart button
    func addNotificationBadgeToIcon(notificationCount: Int, notificationBtn: UIButton) {
        // Remove existing badge if any
        if let subviews = notificationBtn.subviews.first(where: { $0.tag == 50 }) {
            subviews.removeFromSuperview()
        }

        // Create a new UILabel for the badge
        let badgeLabel = UILabel()
        badgeLabel.tag = 50  // A tag to easily find this badge later if needed
        badgeLabel.text = "\(notificationCount)"
        badgeLabel.textColor = .white
        badgeLabel.backgroundColor = UIColor(hexString: "FF8D3A")
        badgeLabel.font = UIFont.systemFont(ofSize: 10)
        badgeLabel.textAlignment = .center
        badgeLabel.layer.cornerRadius = 10
        badgeLabel.clipsToBounds = true
        
        // Adjust badge size based on the number of digits in cartCount
        let badgeSize: CGFloat = notificationCount < 10 ? 20 : 25
        badgeLabel.frame = CGRect(x: notificationBtn.frame.width - badgeSize / 1.5, y: -badgeSize / 3, width: badgeSize, height: badgeSize)
        
        // Add the badge as a subview to the cart button
        notificationBtn.addSubview(badgeLabel)
        
        // Make sure the layout updates correctly after adding the badge
        notificationBtn.setNeedsLayout()
        notificationBtn.layoutIfNeeded()
    }
}
