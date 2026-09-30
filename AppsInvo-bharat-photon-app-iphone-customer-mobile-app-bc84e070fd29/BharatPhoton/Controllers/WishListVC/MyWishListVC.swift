//
//  MyWishListVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/09/24.
//

import UIKit
 

class MyWishListVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var wishlistTable: UITableView!
    var wishListModal: WishListModal?
    var isLoading = false
    var hasMoreData = true
    var currentPage = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        msgLbl.isHidden = true
        serviceWishlistListAPI(page: currentPage)
        serviceWishListCountAPI()
        wishlistTable.delegate = self
        wishlistTable.dataSource = self
        wishlistTable.register(UINib(nibName: "WishListCell", bundle: nil), forCellReuseIdentifier: "WishListCell")
        wishlistTable.separatorStyle = .none
        
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
extension MyWishListVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return wishListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = wishlistTable.dequeueReusableCell(withIdentifier: "WishListCell", for: indexPath) as! WishListCell
        cell.productNameLbl.text = self.wishListModal?.data?[indexPath.row].productName ?? ""
        let price = self.wishListModal?.data?[indexPath.row].price ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        let displayPrice = self.wishListModal?.data?[indexPath.row].priceToDisplay ?? 0
        cell.displayPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: displayPrice), number: .decimal))"
        cell.ratingLbl.text = "\(self.wishListModal?.data?[indexPath.row].rating ?? 0)"
        let image = self.wishListModal?.data?[indexPath.row].productImage?.first?.url ?? ""
        cell.productImg.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.wishListBtn.tag = indexPath.row
        cell.wishListBtn.addTarget(self, action: #selector(wishListBtnTapped(sender:)), for: .touchUpInside)
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 150
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductDetailsVC") as! ProductDetailsVC
        vc.productId = self.wishListModal?.data?[indexPath.row].productId ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @objc func wishListBtnTapped(sender: UIButton) {
        let row = sender.tag
        self.serviceDeleteWishListAPI(id: self.wishListModal?.data?[row]._id ?? "")
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let scrollViewHeight = scrollView.frame.size.height
        if offsetY > contentHeight - scrollViewHeight - 100, !isLoading, hasMoreData {
            currentPage += 1
            serviceWishlistListAPI(page: currentPage)
        }
    }
}
// MARK: WishlistList API ==>
extension MyWishListVC {
    func serviceWishlistListAPI(page: Int) {
        guard !isLoading, hasMoreData else { return }
        isLoading = true
        if page == 1 {
            LoaderManager.shared.showLoader(in: self.view)
        } else {
            LoaderManager.shared.hideLoader()
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = ServiceEndPoint.BhartPhotonUrl.baseURL + "wishlistListing?page=\(page)&limit=10"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: [:], token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    let newData = try JSONDecoder().decode(WishListModal.self, from: JSONResponse.rawData())
                    if page == 1 {
                        self.wishListModal = newData
                    } else {
                        self.wishListModal?.data?.append(contentsOf: newData.data ?? [])
                    }
                    self.hasMoreData = !(newData.data?.isEmpty ?? true)
                    self.msgLbl.isHidden = (self.wishListModal?.data?.count ?? 0) > 0
                    self.wishlistTable.reloadData()
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
    func reloadAllPages() {
        self.wishListModal?.data?.removeAll()
        self.currentPage = 1
        self.hasMoreData = true
        self.serviceWishlistListAPI(page: currentPage)
    }
}
extension MyWishListVC {
    func serviceDeleteWishListAPI(id: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .DELETE, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "deleteFromWishlist/\(id)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.reloadAllPages()
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
extension MyWishListVC {
    func serviceWishListCountAPI(){
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "wishlistedCount", params: parms, token: token) { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let count = JSONResponse["count"].intValue
                print("\(count)")
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}


