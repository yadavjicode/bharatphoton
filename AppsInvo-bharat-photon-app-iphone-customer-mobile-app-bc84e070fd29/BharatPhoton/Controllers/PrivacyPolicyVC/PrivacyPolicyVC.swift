//
//  PrivacyPolicyVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 28/06/24.
//

import UIKit
 
class PrivacyPolicyVC: UIViewController {
    @IBOutlet weak var descriptionsTextView: UITextView!
    @IBOutlet weak var descriptionsTextViewHeight: NSLayoutConstraint!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    var privacyPolicyModal: AboutUsModal?
    var userType = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        servicePrivacyAndPolicyAPI()
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
// MARK: PrivacyAndPolicy API ==>
extension PrivacyPolicyVC {
    func servicePrivacyAndPolicyAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cmsData?userType=\(self.userType)&type=privacyAndPolicy", params: parms, token: "") { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.privacyPolicyModal = try JSONDecoder().decode(AboutUsModal.self, from: JSONResponse.rawData())
                    self.setData()
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
    func setData(){
        LoaderManager.shared.hideLoader()
        descriptionsTextView.isEditable = false
        descriptionsTextView.isScrollEnabled = false
        descriptionsTextView.dataDetectorTypes = [.link]
        descriptionsTextView.linkTextAttributes = [.foregroundColor: UIColor.systemBlue,.underlineStyle: NSUnderlineStyle.single.rawValue]
        let htmlString = privacyPolicyModal?.data?.descriptions ?? ""
        if let attributedText = htmlString.htmlToAttributedString() {
            descriptionsTextView.attributedText = attributedText
            // Update dynamic height
            let fixedWidth = descriptionsTextView.frame.width
            let newSize = descriptionsTextView.sizeThatFits(CGSize(width: fixedWidth, height: CGFloat.greatestFiniteMagnitude))
            descriptionsTextViewHeight.constant = newSize.height
        }
    }
}
