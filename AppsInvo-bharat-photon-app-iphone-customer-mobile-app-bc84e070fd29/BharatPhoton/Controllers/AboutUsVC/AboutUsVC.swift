//
//  AboutusVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/09/24.
//

import UIKit


class AboutUsVC: UIViewController {
    @IBOutlet weak var descriptionsTextView: UITextView!
    @IBOutlet weak var descriptionsTextViewHeight: NSLayoutConstraint!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    var aboutUsModal: AboutUsModal?
    var userType = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }
        serviceAboutUsAPI()
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
// MARK: AboutUS API ==>
extension AboutUsVC {
    func serviceAboutUsAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cmsData?userType=\(self.userType)&type=aboutUs", params: parms, token: "") { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.aboutUsModal = try JSONDecoder().decode(AboutUsModal.self, from: JSONResponse.rawData())
                    self.setData()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
                break
            }
        }failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
    func setData(){
        LoaderManager.shared.hideLoader()
        descriptionsTextView.isEditable = false
        descriptionsTextView.isScrollEnabled = false
        descriptionsTextView.dataDetectorTypes = [.link]
        descriptionsTextView.linkTextAttributes = [
            .foregroundColor: UIColor.systemBlue,
            .underlineStyle: NSUnderlineStyle.single.rawValue
        ]
        let htmlString = aboutUsModal?.data?.descriptions ?? ""
        if let attributedText = htmlString.htmlToAttributedStringA() {
            let resizedText = attributedText.resizingImageAttachments(toFitWidth: descriptionsTextView.frame.width)
            descriptionsTextView.attributedText = resizedText
            
            // Update dynamic height
            let fixedWidth = descriptionsTextView.frame.width
            let newSize = descriptionsTextView.sizeThatFits(CGSize(width: fixedWidth, height: CGFloat.greatestFiniteMagnitude))
            descriptionsTextViewHeight.constant = newSize.height
        }
    }
}
extension String {
    func htmlToAttributedStringA(fontName: String = "System", fontSize: Float = 17) -> NSAttributedString? {
        let styledHTML = """
               <style>
               body {
                   font-family: -apple-system, \(fontName);
                   font-size: \(fontSize)px;
               }
               a {
                   color: #007AFF;
                   text-decoration: underline;
               }
               </style>
               \(self)
               """
        guard let data = styledHTML.data(using: .utf8) else { return nil }
        return try? NSAttributedString(
            data: data,
            options: [
                .documentType: NSAttributedString.DocumentType.html,
                .characterEncoding: String.Encoding.utf8.rawValue
            ],
            documentAttributes: nil
        )
    }
}
extension NSAttributedString {
    /// Resize image attachments to fit given width
    func resizingImageAttachments(toFitWidth maxWidth: CGFloat) -> NSAttributedString {
        let mutable = NSMutableAttributedString(attributedString: self)
        mutable.enumerateAttribute(.attachment, in: NSRange(location: 0, length: mutable.length), options: []) { value, range, _ in
            if let attachment = value as? NSTextAttachment,
               let image = attachment.image ?? (attachment.fileWrapper?.regularFileContents.flatMap { UIImage(data: $0) }) {
                
                let scaleFactor = maxWidth / image.size.width
                let newSize = CGSize(width: maxWidth, height: image.size.height * scaleFactor)
                
                let resizedImage = UIGraphicsImageRenderer(size: newSize).image { _ in
                    image.draw(in: CGRect(origin: .zero, size: newSize))
                }
                
                let newAttachment = NSTextAttachment()
                newAttachment.image = resizedImage
                
                mutable.removeAttribute(.attachment, range: range)
                mutable.addAttribute(.attachment, value: newAttachment, range: range)
            }
        }
        
        return mutable
    }
}

