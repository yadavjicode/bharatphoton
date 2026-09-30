//
//  CustomerSupportCustomerCareVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 04/07/24.
//

import UIKit

class CustomerSupportCustomerCareVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var faqTable: UITableView!
    @IBOutlet weak var faqTableHeight: NSLayoutConstraint!
    var faqListModal : FaqListModal?
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        servicefaqListAPI()
        faqTable.delegate = self
        faqTable.dataSource = self
        faqTable.register(UINib(nibName: "FAQCell", bundle: nil), forCellReuseIdentifier: "FAQCell")
        faqTable.separatorStyle = .none
        faqTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        
    }
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.faqTable && keyPath == "contentSize" {
                faqTableHeight.constant = faqTable.contentSize.height
            }
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
        self.navigationController?.popToRootViewController(animated: true)
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
    @IBAction func registerComplantBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportRegisterComplantVC") as! CustomerSupportRegisterComplantVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func complantHistoryBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportComplaintHistoryVC") as! CustomerSupportComplaintHistoryVC
        self.navigationController?.pushViewController(vc, animated: false)
        
    }
    @IBAction func troubleSootBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportTroubleShootingVideoVC") as! CustomerSupportTroubleShootingVideoVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
}
extension CustomerSupportCustomerCareVC: UITableViewDelegate, UITableViewDataSource{
    var expandedRow: Int? {
        get {
            return objc_getAssociatedObject(self, &AssociatedKeys.expandedRow) as? Int
        }
        set {
            objc_setAssociatedObject(self, &AssociatedKeys.expandedRow, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return faqListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "FAQCell", for: indexPath) as! FAQCell
        cell.questionLbl.text = faqListModal?.data?[indexPath.row].question ?? ""
        let answer = faqListModal?.data?[indexPath.row].answer ?? ""
        cell.answerLbl.attributedText = answer.description.htmlToAttributedString_(fontName: "System", fontSize: 17)!
        cell.arrowBtn.tag = indexPath.row
        cell.arrowBtn.addTarget(self, action: #selector(arrowBtnTapped(sender:)), for: .touchUpInside)
        let isExpanded = (expandedRow == indexPath.row)
        
        cell.arrowBtn.setImage(UIImage(named: isExpanded ? "DropDown" : "greater-than"), for: .normal)
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if expandedRow == indexPath.row {
            return UITableView.automaticDimension // This makes the row auto-expand based on its content
        } else {
            return 100 // The collapsed row height (adjust as needed)
        }
    }
    @objc func arrowBtnTapped(sender: UIButton) {
        let row = sender.tag
        if expandedRow == row {
            expandedRow = nil // Collapse if already expanded
        } else {
            expandedRow = row // Expand the selected row
            // self.faqTable.reloadData()
        }
        
        // Reload the table rows to reflect the height change
        faqTable.reloadRows(at: [IndexPath(row: row, section: 0)], with: .automatic)
    }
    
    // Associated key for storing isExpandedArray
    private struct AssociatedKeys {
        static var expandedRow = "expandedRow"
    }
}
// MARK: FAQ List API ==>
extension CustomerSupportCustomerCareVC {
    func servicefaqListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "faqList", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.faqListModal = try JSONDecoder().decode(FaqListModal.self, from: JSONResponse.rawData())
                    self.faqTable.reloadData()
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
}

