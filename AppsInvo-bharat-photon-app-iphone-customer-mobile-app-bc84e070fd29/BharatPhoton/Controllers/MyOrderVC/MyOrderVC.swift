//
//  MyOrderVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 25/06/24.
//

import UIKit

class MyOrderVC: UIViewController {
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var backBtn: UIButton!
    @IBOutlet weak var logoImg: UIImageView!
    @IBOutlet weak var myOrderTable: UITableView!
    @IBOutlet weak var allBtn: UIButton!
    @IBOutlet weak var pendingBtn: UIButton!
    @IBOutlet weak var processedBtn: UIButton!
    @IBOutlet weak var dispatchedBtn: UIButton!
    @IBOutlet weak var deliveredBtn: UIButton!
    var orderListModal: OrderListModal?
    var statusType = 4
    var keyType = 0
    var currentPage = 1
    var isLoading = false
    var hasMoreData = true
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        self.msgLbl.isHidden = true
        serviceOrderListingAPI(page: currentPage)
        updateCartBadge()
        updateNotificationBadge()
        myOrderTable.delegate = self
        myOrderTable.dataSource = self
        myOrderTable.register(UINib(nibName: "MyOrderCell", bundle: nil), forCellReuseIdentifier: "MyOrderCell")
        self.myOrderTable.separatorStyle = .none
        myOrderTable.sectionHeaderHeight = UITableView.automaticDimension
        if keyType == 1{
            backBtn.isHidden = false
            // MARK: back swipe gesture ==>
            let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
            swipeGestureRecognizer.direction = .right
            view.addGestureRecognizer(swipeGestureRecognizer)
        }
        else{
            backBtn.isHidden = true
        }
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateCartBadge()
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
    @IBAction func allBtnAction(_ sender: UIButton) {
        updateFilter(status: 4, selectedButton: allBtn)
    }
    @IBAction func pendingBtnAction(_ sender: UIButton) {
        updateFilter(status: 0, selectedButton: pendingBtn)
    }
    @IBAction func processedBtnAction(_ sender: UIButton) {
        updateFilter(status: 1, selectedButton: processedBtn)
    }
    @IBAction func dispatchedBtnAction(_ sender: UIButton) {
        updateFilter(status: 2, selectedButton: dispatchedBtn)
    }
    @IBAction func deliveredBtnAction(_ sender: UIButton) {
        updateFilter(status: 3, selectedButton: deliveredBtn)
    }
    private func updateFilter(status: Int, selectedButton: UIButton) {
        self.statusType = status
        self.currentPage = 1
        self.hasMoreData = true
        self.orderListModal = nil
        self.myOrderTable.reloadData()
        let buttons = [allBtn, pendingBtn, processedBtn, dispatchedBtn, deliveredBtn]
        for button in buttons {
            button?.backgroundColor = (button == selectedButton) ? UIColor(hexString: "FF8D3A") : .white
        }
        self.myOrderTable.setContentOffset(.zero, animated: true)
        serviceOrderListingAPI(page: currentPage)
    }
}
extension MyOrderVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return orderListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = myOrderTable.dequeueReusableCell(withIdentifier: "MyOrderCell", for: indexPath) as! MyOrderCell
        cell.productImg.sd_setImage(with: URL(string: self.orderListModal?.data?[indexPath.row].products?.first?.image ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.paymentModeLbl.text = self.orderListModal?.data?[indexPath.row].paymentMethod ?? ""
        let dateAndTime = self.orderListModal?.data?[indexPath.row].createdAt ?? ""
        cell.dateLbl.text = dateAndTime.dateAndTime()
        cell.orderIdLbl.text = self.orderListModal?.data?[indexPath.row].uuid ?? ""
        let subTotal = self.orderListModal?.data?[indexPath.row].subTotal ?? 0
        cell.totalAmountLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: subTotal), number: .decimal))"
        cell.uploadSitePhotoBtn.tag = indexPath.row
        cell.uploadSitePhotoBtn.addTarget(self, action: #selector(uploadSitePhotoBtnAction(sender:)), for: .touchUpInside)
        let itemCount = self.orderListModal?.data?[indexPath.row].itemCount ?? 0
        if itemCount > 1 {
            let items = "+\(itemCount - 1) More"
            cell.productNameLbl.text = "\(self.orderListModal?.data?[indexPath.row].products?.first?.product_name ?? "") \(items)"
            cell.itemsLbl.text = "\(itemCount) Items,"
        } else {
            cell.itemsLbl.text = "\(itemCount) Item,"
            cell.productNameLbl.text = self.orderListModal?.data?[indexPath.row].products?.first?.product_name ?? ""
        }
        let paymentMode = self.orderListModal?.data?[indexPath.row].paymentMethod ?? ""
        if paymentMode == "cod"{
            cell.paymentModeLbl.text = "COD"
        }
        else if paymentMode == "netbanking"{
            cell.paymentModeLbl.text = "NETBANKING"
        }
        else if paymentMode == "upi"{
            cell.paymentModeLbl.text = "UPI"
        }
        else{
            cell.paymentModeLbl.text = paymentMode
        }
            if statusType == 0{
            let isSitePhotoUploaded = self.orderListModal?.data?[indexPath.row].isSitePhotoUploaded ?? 0
            if isSitePhotoUploaded == 1{
                cell.uploadSitePhotoBtn.isHidden = true
            }
            else{
                cell.uploadSitePhotoBtn.isHidden = false
            }
        }
        else{
            cell.uploadSitePhotoBtn.isHidden = true
        }
       
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return myOrderTable.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "OrderDetailsVC") as! OrderDetailsVC
        vc.productId = self.orderListModal?.data?[indexPath.row].products?.first?.productId ?? ""
        vc.orderId = self.orderListModal?.data?[indexPath.row]._id ?? ""
        vc.id = self.orderListModal?.data?[indexPath.row].products?.first?._id ?? ""
        // let orderStatus = self.orderListModal?.data?[indexPath.section].products?[indexPath.row].orderStatus ?? 0
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @objc func uploadSitePhotoBtnAction(sender: UIButton) {
        let index = sender.tag
        if let sectionIndex = sender.superview?.tag { // Assuming superview.tag is set to section index
            let orderId = self.orderListModal?.data?[sectionIndex]._id ?? ""
            let uuid = self.orderListModal?.data?[sectionIndex].uuid ?? ""
            let id = self.orderListModal?.data?[sectionIndex].products?[index].installationAddressId ?? ""
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "ThanksPurchasePlaneUploadSitePhotoVC") as! ThanksPurchasePlaneUploadSitePhotoVC
            vc.orderId = orderId
            vc.uuid = uuid
            vc.id = id
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
            let offsetY = scrollView.contentOffset.y
            let contentHeight = scrollView.contentSize.height
            let tableViewHeight = scrollView.frame.size.height
            if offsetY > contentHeight - tableViewHeight - 100 {
                if hasMoreData && !isLoading {
                    currentPage += 1
                    serviceOrderListingAPI(page: currentPage)
                }
            }
    }
}
// MARK: OrderList API ==>
extension MyOrderVC {
    func serviceOrderListingAPI(page: Int) {
        guard !isLoading, hasMoreData else { return }
        isLoading = true
        if page == 1 {
            LoaderManager.shared.showLoader(in: self.view)
        } else {
            LoaderManager.shared.hideLoader()
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = ServiceEndPoint.BhartPhotonUrl.baseURL + "orderListing?orderStatus=\(self.statusType)&page=\(page)&limit=10"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: [:], token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            let code = JSONResponse["code"].intValue
            print(JSONResponse)
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    let newData = try JSONDecoder().decode(OrderListModal.self, from: JSONResponse.rawData())
                    if page == 1 {
                        self.orderListModal = newData
                    } else {
                        self.orderListModal?.data?.append(contentsOf: newData.data ?? [])
                    }
                    self.hasMoreData = !(newData.data?.isEmpty ?? true)
                    self.msgLbl.isHidden = (self.orderListModal?.data?.count ?? 0) > 0
                    self.myOrderTable.reloadData()
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

