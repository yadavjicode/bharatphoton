//
//  UserManualVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 06/01/25.
//

import UIKit
import PDFKit

class UserManualVC: UIViewController {
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var pdfContainerView: UIView!
    var pdfView: PDFView!
    var pdfUrl = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        pdfContainerView.backgroundColor = UIColor.clear
        pdfView = PDFView(frame: CGRect(x: 0, y: 0, width: pdfContainerView.bounds.width, height: pdfContainerView.bounds.height))
        pdfView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        pdfContainerView.addSubview(pdfView)  // Add the PDFView inside the container view
        pdfView.autoScales = true
        pdfView.displayMode = .singlePage
        pdfView.displayDirection = .horizontal
        // Now load the PDF from a URL
        if let pdfURL = URL(string: self.pdfUrl) {
            loadPDF(from: pdfURL)
        }
    }
    func loadPDF(from url: URL) {
        if let document = PDFDocument(url: url) {
            pdfView.document = document  // Assign the PDFDocument to the PDFView
        } else {
            print("Failed to load PDF from the URL.")
        }
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func downloadUserManualBtnAction(_ sender: UIButton) {
        self.downloadInvoice(invoiceUrl: self.pdfUrl)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    func updateCartBadge() {
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
}
