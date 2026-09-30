//
//  PaymentHistoryVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 02/07/24.
//

import UIKit
 

class PaymentHistoryVC: UIViewController{
    @IBOutlet weak var historyView: UIView!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var paymentHistoryTable: UITableView!
    var paymentHistoryListModal: PaymentHistoryListModal?
    var currentPage = 1
    var isLoading = false
    var hasMoreData = true
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        msgLbl.isHidden = true
        historyView.isHidden = true
        statusBarColor()
        updateCartBadge()
        updateNotificationBadge()
        servicePaymentHistoryListAPI(page: currentPage)
        paymentHistoryTable.delegate = self
        paymentHistoryTable.dataSource = self
        paymentHistoryTable.register(UINib(nibName: "PaymentHistoryCell", bundle: nil), forCellReuseIdentifier: "PaymentHistoryCell")
        paymentHistoryTable.separatorStyle = .none
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    func updateCartBadge(){
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
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
extension PaymentHistoryVC: UITableViewDelegate, UITableViewDataSource, UIScrollViewDelegate{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return paymentHistoryListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PaymentHistoryCell", for: indexPath) as! PaymentHistoryCell
        cell.uuidLbl.text = paymentHistoryListModal?.data?[indexPath.row].uuid ?? ""
        let amount = paymentHistoryListModal?.data?[indexPath.row].finalTotalAmount ?? 0.0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: amount), number: .decimal))"
        cell.logLbl.text = paymentHistoryListModal?.data?[indexPath.row].log ?? ""
        cell.paymentStatusLbl.text = paymentHistoryListModal?.data?[indexPath.row].paymentStatus ?? ""
        cell.dateLbl.text = paymentHistoryListModal?.data?[indexPath.row].createdAt?.Date() ?? ""
        cell.invoiceDownloadBtn.tag = indexPath.row
        cell.invoiceDownloadBtn.addTarget(self, action: #selector(invoiceDownloadBtnAction(sender:)), for: .touchUpInside)
        let receiptUrl = paymentHistoryListModal?.data?[indexPath.row].receiptUrl ?? ""
        let invoiceUrl = paymentHistoryListModal?.data?[indexPath.row].invoiceUrl ?? ""
        if receiptUrl.isEmpty && invoiceUrl.isEmpty {
            cell.lbl.isHidden = false
            cell.invoiceDownloadBtn.isHidden = true
        } else {
            cell.lbl.isHidden = true
            cell.invoiceDownloadBtn.isHidden = false
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 40
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let tableViewHeight = scrollView.frame.size.height
        if offsetY > contentHeight - tableViewHeight - 100 {   // Load more when 100px from bottom
            if !isLoading && hasMoreData {
                currentPage += 1
                servicePaymentHistoryListAPI(page: currentPage)
            }
        }
    }
    @objc func invoiceDownloadBtnAction(sender: UIButton) {
        let row = sender.tag
        let invoiceUrl = paymentHistoryListModal?.data?[row].invoiceUrl ?? ""
        let receiptUrl = paymentHistoryListModal?.data?[row].receiptUrl ?? ""
        if !invoiceUrl.isEmpty {
            self.downloadInvoice(invoiceUrl: invoiceUrl)
        } else if !receiptUrl.isEmpty {
            self.downloadInvoice(invoiceUrl: receiptUrl)
        } else {
            print("No invoice or receipt URL available to download.")
        }
    }
}
// MARK: PaymentHistory API ==>
extension PaymentHistoryVC {
    func servicePaymentHistoryListAPI(page: Int){
        guard !isLoading, hasMoreData else { return }
        isLoading = true
        if page == 1{
            LoaderManager.shared.showLoader(in: self.view)
        }
        else{
            LoaderManager.shared.hideLoader()
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = ServiceEndPoint.BhartPhotonUrl.baseURL + "paymentHistory?page=\(page)&limit=10"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: [:], token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    let newData = try JSONDecoder().decode(PaymentHistoryListModal.self, from: JSONResponse.rawData())
                    if self.currentPage == 1 {
                        self.paymentHistoryListModal = newData
                    } else {
                        self.paymentHistoryListModal?.data?.append(contentsOf: newData.data ?? [])
                    }
                    self.hasMoreData = !(newData.data?.isEmpty ?? true)
                    self.msgLbl.isHidden = (self.paymentHistoryListModal?.data?.count ?? 0) > 0
                    self.historyView.isHidden = !self.msgLbl.isHidden
                    self.paymentHistoryTable.reloadData()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
