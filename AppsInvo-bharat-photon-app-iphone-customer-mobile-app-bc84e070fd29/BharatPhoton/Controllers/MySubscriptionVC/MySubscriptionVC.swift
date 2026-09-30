//
//  MySubscriptionVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit
 
class MySubscriptionVC: UIViewController {
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var mySubscriptionTable: UITableView!
    var purchasedSubListingModal: PurchasedSubListingModal?
    var currentPage = 1
    var isLoading = false
    var hasMoreData = true
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        self.msgLbl.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        servicePurchasedSubListingAPI(page: currentPage)
        mySubscriptionTable.delegate = self
        mySubscriptionTable.dataSource = self
        mySubscriptionTable.register(UINib(nibName: "MySubscriptionCell", bundle: nil), forCellReuseIdentifier: "MySubscriptionCell")
        mySubscriptionTable.separatorStyle = .none
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
extension MySubscriptionVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return purchasedSubListingModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell{
        let cell = mySubscriptionTable.dequeueReusableCell(withIdentifier: "MySubscriptionCell", for: indexPath) as! MySubscriptionCell
        cell.uploadSitePhotoBtn.tag = indexPath.row
        cell.uploadSitePhotoBtn.addTarget(self, action: #selector(uploadSitePhotoBtnTapped(sender:)), for: .touchUpInside)
        let amount = purchasedSubListingModal?.data?[indexPath.row].price ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: amount), number: .decimal))/"
        let finalTotalAmount = purchasedSubListingModal?.data?[indexPath.row].finalTotalAmount ?? 0.0
        cell.totalPriceLbl.text = "Total Price : ₹ \(NumberFormatter.localizedString(from: NSNumber(value: finalTotalAmount), number: .decimal))"
        cell.orderIdLbl.text = "Order Id : \(purchasedSubListingModal?.data?[indexPath.row].uuid ?? "")"
        cell.titleLbl.text = purchasedSubListingModal?.data?[indexPath.row].categoryName ?? ""
        cell.descriptionLbl.text = purchasedSubListingModal?.data?[indexPath.row].productName ?? ""
        cell.productImg.sd_setImage(with: URL(string: purchasedSubListingModal?.data?[indexPath.row].productImage ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.modalNameLbl.text = purchasedSubListingModal?.data?[indexPath.row].modelNo ?? ""
        cell.qtyLbl.text = "QTY : \(purchasedSubListingModal?.data?[indexPath.row].quantity ?? 0)"
        cell.dateLbl.text = "Date : \(purchasedSubListingModal?.data?[indexPath.row].createdAt?.toDisplayDate() ?? "")"
        cell.paymentModaLbl.text = "Payment Mode : \(purchasedSubListingModal?.data?[indexPath.row].paymentMethod ?? "")"
        cell.totalWaterSavedLbl.text = "\(purchasedSubListingModal?.data?[indexPath.row].totalWaterSaved ?? 0) Ltrs"
        let totalCostSaved = purchasedSubListingModal?.data?[indexPath.row].totalCostSaved ?? 0
        cell.totalCostSavedLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: totalCostSaved), number: .decimal))"
        cell.id = purchasedSubListingModal?.data?[indexPath.row]._id ?? ""
        cell.productName = purchasedSubListingModal?.data?[indexPath.row].productName ?? ""
        cell.categoryName = purchasedSubListingModal?.data?[indexPath.row].categoryName ?? ""
        cell.categoryId = purchasedSubListingModal?.data?[indexPath.row].categoryId ?? ""
        cell.productId = purchasedSubListingModal?.data?[indexPath.row].productId ?? ""
        let installationMetrics = purchasedSubListingModal?.data?[indexPath.row].installationMetrics ?? []
        cell.installationMetricsArray = installationMetrics
        cell.installationMetricsTable.reloadData()
        let isSitePhotoUploaded = purchasedSubListingModal?.data?[indexPath.row].isSitePhotoUploaded ?? 0
        if isSitePhotoUploaded == 1{
            cell.uploadSitePhotoBtn.isHidden = true
            cell.uploadSitePhotoLbl.isHidden = true
            cell.img.isHidden = true
        }
        else{
            cell.uploadSitePhotoBtn.isHidden = false
            cell.uploadSitePhotoLbl.isHidden = false
            cell.img.isHidden = false
        }
        let isFieldAvailable = purchasedSubListingModal?.data?[indexPath.row].isFieldAvailable ?? 0
        let isUrinalSensorAvilable = purchasedSubListingModal?.data?[indexPath.row].isUrinalSensorAvilable ?? 0
        cell.totalCostSavedView.isHidden = !(isFieldAvailable == 1 || isUrinalSensorAvilable == 1)
        cell.totalWaterSavedView.isHidden = !(isFieldAvailable == 1 || isUrinalSensorAvilable == 1)
        let mode = purchasedSubListingModal?.data?[indexPath.row].mode ?? ""
        let planType = purchasedSubListingModal?.data?[indexPath.row].planType ?? 0
        if mode == "M"{
            cell.duretionLbl.text = "per month"
        }
        else if mode == "Q"{
            cell.duretionLbl.text = "Quarterly"
        }
        else if mode == "H"{
            cell.duretionLbl.text = "6 months"
        }
        else if mode == "Y"{
            cell.duretionLbl.text = "per year"
        }
        if planType == 0{
            cell.planLbl.text = "lite"
        }
        else if planType == 1{
            cell.planLbl.text = "plus"
        }
        else if planType == 2{
            cell.planLbl.text = "premium"
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return mySubscriptionTable.rowHeight
    }
    //MARK: UploadSitePhotoBtnTapped Action ==>
    @objc func uploadSitePhotoBtnTapped(sender: UIButton) {
        let row = sender.tag
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ThanksPurchasePlaneUploadSitePhotoVC") as! ThanksPurchasePlaneUploadSitePhotoVC
        vc.orderId = purchasedSubListingModal?.data?[row]._id ?? ""
        vc.uuid = purchasedSubListingModal?.data?[row].uuid ?? ""
        vc.id = purchasedSubListingModal?.data?[row].installationAddressId ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let tableViewHeight = scrollView.frame.size.height
        if offsetY > contentHeight - tableViewHeight - 100 {  // Load more when 100px from bottom
            if !isLoading && hasMoreData {
                currentPage += 1
                servicePurchasedSubListingAPI(page: currentPage)
            }
        }
    }
}
extension MySubscriptionVC {
    func servicePurchasedSubListingAPI(page: Int) {
        guard !isLoading, hasMoreData else { return }
        isLoading = true
        if page == 1 {
            LoaderManager.shared.showLoader(in: self.view)
        } else {
            LoaderManager.shared.hideLoader()
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = ServiceEndPoint.BhartPhotonUrl.baseURL + "purchasedSubListing?page=\(page)&limit=10&type=0"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: [:], token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    let newData = try JSONDecoder().decode(PurchasedSubListingModal.self, from: JSONResponse.rawData())
                    if page == 1 {
                        self.purchasedSubListingModal = newData
                    } else {
                        self.purchasedSubListingModal?.data?.append(contentsOf: newData.data ?? [])
                    }
                    self.hasMoreData = !(newData.data?.isEmpty ?? true)
                    self.msgLbl.isHidden = (self.purchasedSubListingModal?.data?.count ?? 0) > 0
                    self.mySubscriptionTable.reloadData()
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


