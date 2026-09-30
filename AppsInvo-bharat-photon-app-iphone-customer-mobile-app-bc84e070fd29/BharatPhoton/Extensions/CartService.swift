//
//  CartService.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 23/09/24.
//

import Foundation
import UIKit
class CartService {
    static let shared = CartService()

    private init() {}

    // Function to fetch cart count and update the badge
    func fetchCartCount(for cartBtn: UIButton, completion: @escaping (Int?) -> Void) {
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cartItemCount?deviceId=\(deviceId)", params: parms, token: token) { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                completion(nil)  // Handle error case
            case 1:
                let cartCount = JSONResponse["count"].intValue
                if cartCount == 0{
                 print("cart Empty")
                }
                else{
                    self.addCartBadgeToIcon(cartCount: cartCount, cartBtn: cartBtn)
                    completion(cartCount)
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
    func addCartBadgeToIcon(cartCount: Int, cartBtn: UIButton) {
        // Remove existing badge if any
        if let subviews = cartBtn.subviews.first(where: { $0.tag == 50 }) {
            subviews.removeFromSuperview()
        }

        // Create a new UILabel for the badge
        let badgeLabel = UILabel()
        badgeLabel.tag = 50  // A tag to easily find this badge later if needed
        badgeLabel.text = "\(cartCount)"
        badgeLabel.textColor = .white
        badgeLabel.backgroundColor = UIColor(hexString: "FF8D3A")
        badgeLabel.font = UIFont.systemFont(ofSize: 10)
        badgeLabel.textAlignment = .center
        badgeLabel.layer.cornerRadius = 10
        badgeLabel.clipsToBounds = true
        
        // Adjust badge size based on the number of digits in cartCount
        let badgeSize: CGFloat = cartCount < 10 ? 20 : 25
        badgeLabel.frame = CGRect(x: cartBtn.frame.width - badgeSize / 1.5, y: -badgeSize / 3, width: badgeSize, height: badgeSize)
        
        // Add the badge as a subview to the cart button
        cartBtn.addSubview(badgeLabel)
        
        // Make sure the layout updates correctly after adding the badge
        cartBtn.setNeedsLayout()
        cartBtn.layoutIfNeeded()
    }
}
