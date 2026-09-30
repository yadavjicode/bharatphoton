//
//  ReviewsListVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 28/06/24.
//

import UIKit
 
class ReviewsListVC: UIViewController {
    //@IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var reviewListTable: UITableView!
    var reviewAllListModal: ReviewAllListModal?
    var productId = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceReviewListAPI()
        reviewListTable.delegate = self
        reviewListTable.dataSource = self
        reviewListTable.register(UINib(nibName: "ReviewCell", bundle: nil), forCellReuseIdentifier: "ReviewCell")
//        self.reviewListTable.separatorStyle = .none
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
    func updateCartBadge(){
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
extension ReviewsListVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return reviewAllListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = reviewListTable.dequeueReusableCell(withIdentifier: "ReviewCell", for: indexPath) as! ReviewCell
        cell.dateLbl.text = reviewAllListModal?.data?[indexPath.row].createdAt?.date() ?? ""
        cell.reviewLbl.text = reviewAllListModal?.data?[indexPath.row].review ?? ""
        cell.ratingView.rating = Double(reviewAllListModal?.data?[indexPath.row].rating ?? 0)
        cell.ratingView.isUserInteractionEnabled = false
        let firstName = reviewAllListModal?.data?[indexPath.row].userFirstName ?? ""
        let lastName = reviewAllListModal?.data?[indexPath.row].userLastName ?? ""
        cell.nameLbl.text = "\(firstName) \(lastName)"
        cell.profileImg.sd_setImage(with: URL(string: reviewAllListModal?.data?[indexPath.row].userProfileImage ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        var images = reviewAllListModal?.data?[indexPath.row].image?.compactMap({ $0.url }) ?? []
        if let img = reviewAllListModal?.data?[indexPath.row].image, let reviewImageUrl = img as? String, !reviewImageUrl.isEmpty {
            images.insert(reviewImageUrl, at: 0)  // Insert mainProfile URL at the beginning
        }
        if images == []{
            cell.viewHeight.constant = 0
        }
        else {
            cell.viewHeight.constant = 60
        }
        cell.reviewImgArr = images  // Set the image URL array
        cell.imagesCollection.reloadData()  // Reload collection view data
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return reviewListTable.rowHeight
    }
}
// MARK: ReviewAllListAPI ==>
extension ReviewsListVC {
    func serviceReviewListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "reviewList?productId=\(self.productId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.reviewAllListModal = try JSONDecoder().decode(ReviewAllListModal.self, from: JSONResponse.rawData())
                    self.reviewListTable.reloadData()
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
}
