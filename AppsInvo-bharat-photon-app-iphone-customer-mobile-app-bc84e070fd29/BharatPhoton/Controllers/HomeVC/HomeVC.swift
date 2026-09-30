//
//  HomeVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit
import SwiftyJSON
 

class HomeVC: UIViewController, UITextFieldDelegate, CartCountDelegate {
    func didUpdateCartCountApi() {
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    @IBOutlet weak var totalWaterSavedLbl: UILabel!
    @IBOutlet weak var totalCostSavedLbl: UILabel!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var searchProductTable: UITableView!
    @IBOutlet weak var searchProductTableHeight: NSLayoutConstraint!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var searchTF: UITextField!
    @IBOutlet weak var bannerCollection: UICollectionView!
    @IBOutlet weak var pageviewcontroller: UIPageControl!
    @IBOutlet weak var productsCollection: UICollectionView!
    @IBOutlet weak var secondProductsCollection: UICollectionView!
    var homeProductSearchModal: HomeProductSearchModal?
    var homeProductModal: HomeProductModal?
    var caroiselTimer = Timer()
    var counter = 0
    var fullAddress = ""
    var cartItemCount = 0
    var refreshControl = UIRefreshControl()
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        serviceUpdateDeviceTokenAPI()
        setupRefreshControl()
        updateCartBadge()
        updateNotificationBadge()
        serviceHomeAPI()
        searchProductTable.delegate = self
        searchProductTable.dataSource = self
        searchProductTable.register(UINib(nibName: "SearchProductCell", bundle: nil), forCellReuseIdentifier: "SearchProductCell")
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal  // Enable horizontal scrolling
        layout.minimumLineSpacing = 0         // No gaps between items
        bannerCollection.collectionViewLayout = layout
        bannerCollection.isPagingEnabled = true  // Scroll one-by-one
        bannerCollection.delegate = self
        bannerCollection.dataSource = self
        bannerCollection.register(UINib(nibName: "BannerCollectionCell", bundle: nil), forCellWithReuseIdentifier: "BannerCollectionCell")
        productsCollection.delegate = self
        productsCollection.dataSource = self
        productsCollection.register(UINib(nibName: "CategoriesCollectionCell", bundle: nil), forCellWithReuseIdentifier: "CategoriesCollectionCell")
        secondProductsCollection.delegate = self
        secondProductsCollection.dataSource = self
        secondProductsCollection.register(UINib(nibName: "CategoriesCollectionCell", bundle: nil), forCellWithReuseIdentifier: "CategoriesCollectionCell")
        searchTF.addTarget(self, action: #selector(searchTFAction), for: .editingDidBegin)
        searchTF.delegate = self
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateCartBadge()
        updateNotificationBadge()
    }
    @objc func searchTFAction() {
        print("Current search text: \(searchTF.text ?? "")")
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        searchTF.resignFirstResponder()
        print("Done button tapped. Current text: \(searchTF.text ?? "")")
        if searchTF.text == ""{
            print("Search text Empty")
            searchProductTable.reloadData()
            searchProductTableHeight.constant = 0
        }
        else{
//            self.serviceHomeSearchProductAPI(searchtxt: self.searchTF.text!)
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductVC") as! ProductVC
            vc.searchtxt = self.searchTF.text!
            vc.isHomeSearching = 1
            self.navigationController?.pushViewController(vc, animated: true)
        }
        return true
    }
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print("Search text field tapped!")
    }
    //   MARK: page view controller ==>
    @objc func changeCarousel() {
        let filteredBanners = homeProductModal?.data?.banner?.filter { banner in
            return banner.web == "0" && !(banner.appImage?.isEmpty ?? true)
        }
        if counter < filteredBanners?.count ?? 0 {
            let index = IndexPath.init(item: counter, section: 0)
            self.bannerCollection.scrollToItem(at: index, at: .centeredHorizontally, animated: true)
            pageviewcontroller.currentPage = counter
            counter += 1
        }else {
            counter = 0
            let index = IndexPath.init(item: counter, section: 0)
            self.bannerCollection.scrollToItem(at: index, at: .centeredHorizontally, animated: true)
            pageviewcontroller.currentPage = counter
        }
    }
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        for cell in bannerCollection.visibleCells {
            if let row = bannerCollection.indexPath(for: cell)?.item {
                pageviewcontroller.currentPage = row
                if counter < homeProductModal?.data?.banner?.count ?? 0
                {
                    counter += 1
                }
                else
                {
                    counter = 0
                }
            }
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
    private func setupRefreshControl() {
        refreshControl.tintColor = UIColor.darkGray // Customize the spinner color
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh") // Set a title
        refreshControl.addTarget(self, action: #selector(refresh(sender:)), for: .valueChanged)
        scrollView.isScrollEnabled = true
        scrollView.alwaysBounceVertical = true
        scrollView.refreshControl = refreshControl
    }
    @objc func refresh(sender: AnyObject) {
        self.serviceHomeAPI()
        refreshControl.endRefreshing()
    }
    @IBAction func filterAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "FilterVC") as! FilterVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        self.present(vc, animated: true)
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func viewMoreBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CategoriesVC") as! CategoriesVC
        vc.keyType = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    @IBAction func myProductsBtnAction(_ sender: UIButton) {
        if UserDefaults.standard.integer(forKey: "guestUser") == 1{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "MyProductsVC") as! MyProductsVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func mySubscriptionbtnAction(_ sender: UIButton) {
        if UserDefaults.standard.integer(forKey: "guestUser") == 1{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "MySubscriptionVC") as! MySubscriptionVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
}
extension HomeVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return homeProductSearchModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "SearchProductCell", for: indexPath) as! SearchProductCell
        cell.lbl.text = homeProductSearchModal?.data?[indexPath.row].productName ?? ""
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.searchProductTableHeight.constant = 0
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductVC") as! ProductVC
        vc.categoryId = homeProductSearchModal?.data?[indexPath.row].categoryId ?? ""
        vc.searchtxt = self.searchTF.text ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension HomeVC: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if collectionView == bannerCollection{
            let filteredBanners = homeProductModal?.data?.banner?.filter { banner in
                return banner.web == "0" && !(banner.appImage?.isEmpty ?? true)
            }
            return filteredBanners?.count ?? 0
        }
        else if collectionView == productsCollection{
            return homeProductModal?.data?.products?.prefix(3).count ?? 0
        }
        else{
            return homeProductModal?.data?.products?.suffix(2).count ?? 0
        }
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        if collectionView == bannerCollection{
            let cell = bannerCollection.dequeueReusableCell(withReuseIdentifier: "BannerCollectionCell", for: indexPath) as! BannerCollectionCell
            // Get the filtered banners
            let filteredBanners = homeProductModal?.data?.banner?.filter { banner in
                return banner.web == "0" && !(banner.appImage?.isEmpty ?? true)
            }
            // Use filtered data to set the image
            let banner = filteredBanners?[indexPath.item]
            let imageUrl = banner?.appImage ?? ""
            cell.bannerImg.sd_setImage(with: URL(string: imageUrl), placeholderImage: UIImage(named: "profilePlaceholder"))
            return cell
        }
        else{
            let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CategoriesCollectionCell", for: indexPath) as! CategoriesCollectionCell
            if collectionView == productsCollection{
                let categories = self.homeProductModal?.data?.categories?.prefix(3) ?? []
                // Display the corresponding category for the current index
                let category = categories[indexPath.item]
                cell.categoryNameLbl.text = category.categoryName ?? ""
                cell.categoryImg.sd_setImage(with: URL(string: category.image ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
                return cell
            }
            else{
                let categories = self.homeProductModal?.data?.categories?.suffix(2) ?? []
                // Display the corresponding category for the current index
                let category = categories[indexPath.item]
                cell.categoryNameLbl.text = category.categoryName ?? ""
                cell.categoryImg.sd_setImage(with: URL(string: category.image ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
                return cell
            }
        }
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        if collectionView == bannerCollection{
            return  CGSize(width: (self.bannerCollection.frame.width), height: self.bannerCollection.frame.height)
        }
        else if collectionView == productsCollection{
            return CGSize(width: (self.productsCollection.bounds.width/2.8)-32, height: 130)
        }
        else{
            return CGSize(width: (self.secondProductsCollection.bounds.width/1.95)-32, height: 130)
        }
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if collectionView == bannerCollection{
            
        }
        else if collectionView == productsCollection{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductVC") as! ProductVC
            vc.categoryId = self.homeProductModal?.data?.categories?[indexPath.item]._id ?? ""
            vc.selectedIndex = indexPath.item
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            let categories = self.homeProductModal?.data?.categories?.suffix(2) ?? []
            if indexPath.item == 0{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductVC") as! ProductVC
                vc.categoryId = categories[indexPath.item]._id ?? ""
                vc.selectedIndex = 3
                self.navigationController?.pushViewController(vc, animated: true)
            }
            else{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductVC") as! ProductVC
                vc.categoryId = categories[indexPath.item]._id ?? ""
                vc.selectedIndex = 4
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let pageIndex = round(scrollView.contentOffset.x / scrollView.frame.width)
        pageviewcontroller.currentPage = Int(pageIndex)
    }
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
           let pageWidth = scrollView.frame.width
           let targetPage = round(targetContentOffset.pointee.x / pageWidth)
           targetContentOffset.pointee.x = targetPage * pageWidth
       }
}
extension HomeVC {
    func serviceHomeAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "homepage", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.homeProductModal = try JSONDecoder().decode(HomeProductModal.self, from: JSONResponse.rawData())
                    self.pageviewcontroller.numberOfPages = self.getFilteredBanners().count
                    self.caroiselTimer = Timer.scheduledTimer(timeInterval: 2.5 , target: self, selector: #selector(self.changeCarousel), userInfo: nil, repeats: true)
                    self.bannerCollection.reloadData()
                    self.productsCollection.reloadData()
                    self.secondProductsCollection.reloadData()
                    self.setData()
                    self.updateNotificationBadge()
                    self.updateCartBadge()
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
        self.totalWaterSavedLbl.text = "\(homeProductModal?.data?.costSavedData?.totalWaterSaved ?? 0) Ltrs"
        self.totalCostSavedLbl.text = "₹ \(homeProductModal?.data?.costSavedData?.totalCostSaved ?? 0)"
    }
    func getFilteredBanners() -> [Banner] {
        // Filter the banners based on the condition web == "0" and appImage exists
        let filteredBanners = self.homeProductModal?.data?.banner?.filter { banner in
            return banner.web == "0" && !(banner.appImage?.isEmpty ?? true)
        }
        return filteredBanners ?? []
    }
}
extension HomeVC {
    func serviceUpdateDeviceTokenAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let deviceToken = UserDefaults.standard.string(forKey: "deviceToken") ?? ""
        let parms = ["deviceId": deviceId, "deviceType": "ios", "deviceToken": deviceToken] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .patchJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "updateDeviceToken", params: parms, token: token ) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print(JSONResponse)
            case 1:
                print(JSONResponse)
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
