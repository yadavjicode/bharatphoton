//
//  NotificationsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit


class NotificationsVC: UIViewController {
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var notificationTable: UITableView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    var notificationListModal: NotificationListModal?
    var currentPage: Int = 1
    var isLoadingData: Bool = false
    var isLastPage: Bool = false
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }
        msgLbl.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        notificationTable.delegate = self
        notificationTable.dataSource = self
        notificationTable.register(UINib(nibName: "NotificationsCell", bundle: nil), forCellReuseIdentifier: "NotificationsCell")
        notificationTable.separatorStyle = .none
        serviceNotificationListingAPI(page: self.currentPage)
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: #selector(refreshNotifications), for: .valueChanged)
        notificationTable.refreshControl = refreshControl
        setupTableViewFooter()
        
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    @objc func refreshNotifications() {
        updateNotificationBadge()
        currentPage = 1
        isLastPage = false
        serviceNotificationListingAPI(page: currentPage) { [weak self] in
            self?.notificationTable.refreshControl?.endRefreshing()
        }
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
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
}
extension NotificationsVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return notificationListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = notificationTable.dequeueReusableCell(withIdentifier: "NotificationsCell", for: indexPath) as! NotificationsCell
        cell.titleLbl.text = notificationListModal?.data?[indexPath.row].title ?? ""
        cell.bodyLbl.text = notificationListModal?.data?[indexPath.row].body ?? ""
        let image = self.notificationListModal?.data?[indexPath.row].profileImage ?? ""
        cell.profileImg.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "Notification Profile"))
        let dateAndTime = self.notificationListModal?.data?[indexPath.row].createdAt ?? ""
        cell.dateLbl.text = dateAndTime.date()
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return notificationTable.rowHeight
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let tableViewHeight = scrollView.frame.size.height
        
        if offsetY > contentHeight - tableViewHeight - 100 { // Load more when nearing the bottom
            guard !isLoadingData else { return }
            currentPage += 1
            serviceNotificationListingAPI(page: currentPage)
        }
    }
    func setupTableViewFooter() { // FF8D3A
        let footerView = UIView(frame: CGRect(x: 0, y: 0, width: notificationTable.frame.size.width, height: 50))
        let activityIndicator = UIActivityIndicatorView(style: .medium)
        activityIndicator.center = footerView.center
        activityIndicator.color = .gray
        activityIndicator.startAnimating()
        footerView.addSubview(activityIndicator)
        notificationTable.tableFooterView = footerView
        notificationTable.tableFooterView?.isHidden = true
    }
}
extension NotificationsVC {
    func serviceNotificationListingAPI(page: Int, completion: (() -> Void)? = nil) {
        guard !isLoadingData, !isLastPage else {
            completion?()
            return
        }
        isLoadingData = true
        if page == 1 {
            LoaderManager.shared.showLoader(in: self.view)
        } else {
            notificationTable.tableFooterView?.isHidden = false
        }
        let params = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = "\(ServiceEndPoint.BhartPhotonUrl.baseURL)notificationListing?read=&page=\(page)&limit=10"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: params, token: token) { JSONResponse in
            self.isLoadingData = false
            if page == 1 {
                LoaderManager.shared.hideLoader()
            } else {
                self.notificationTable.tableFooterView?.isHidden = true
            }
            completion?()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.updateNotificationBadge()
                do {
                    let newNotifications = try JSONDecoder().decode(NotificationListModal.self, from: JSONResponse.rawData())
                    if let newData = newNotifications.data, !newData.isEmpty {
                        if page == 1 {
                            // Replace old data on refresh
                            self.notificationListModal = newNotifications
                        } else {
                            // Append for load more
                            var updatedData = self.notificationListModal?.data ?? []
                            updatedData.append(contentsOf: newData)
                            self.notificationListModal?.data = updatedData
                        }
                        self.notificationTable.reloadData()
                    } else {
                        self.isLastPage = true
                        if page == 1 {
                            self.notificationListModal?.data = []
                            self.notificationTable.reloadData()
                        }
                    }
                    // ✅ Show/hide msgLbl
                    let isEmpty = self.notificationListModal?.data?.isEmpty ?? true
                    self.msgLbl.isHidden = !isEmpty
                    
                } catch {
                    print("Error decoding JSON: \(error)")
                    self.msgLbl.isHidden = false
                }
            default:
                print("Unidentified code found")
            }
        } failure: { error, JSONResponse in
            self.isLoadingData = false
            if page == 1 {
                LoaderManager.shared.hideLoader()
            } else {
                self.notificationTable.tableFooterView?.isHidden = true
            }
            completion?()
            self.alert(message: JSONResponse["message"].stringValue, title: "")
        }
    }
}
