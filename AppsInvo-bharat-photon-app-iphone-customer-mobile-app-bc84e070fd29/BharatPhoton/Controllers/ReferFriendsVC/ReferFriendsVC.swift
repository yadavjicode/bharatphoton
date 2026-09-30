//
//  ReferFriendsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 28/06/24.
//

import UIKit


class ReferFriendsVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var referrelCodeLbl: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
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
    func updateCartBadge() {
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    @IBAction func tapToCopyAction(_ sender: UIButton) {
        let textToCopy = "yGg741"
        UIPasteboard.general.string = textToCopy
        self.alert(message: "Text copied to clipboard.", title: "")
    }
    @IBAction func shareAction(_ sender: UIButton) {
        let textToShare = "This is bharat photon application and this is your refferal code yGg741"
        // Create an activity view controller to share the text
        let activityViewController = UIActivityViewController(activityItems: [textToShare], applicationActivities: nil)
        // Exclude certain activity types if necessary (optional)
        activityViewController.excludedActivityTypes = [.addToReadingList, .postToFacebook]
        // Present the activity view controller
        self.present(activityViewController, animated: true, completion: nil)
        // Handle iPad presentation (for iPads, you need to set a popover anchor)
        if let popoverController = activityViewController.popoverPresentationController {
            popoverController.sourceView = sender // Button where the popover will anchor
            popoverController.sourceRect = sender.bounds // Set the anchor position of the popover
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
}


