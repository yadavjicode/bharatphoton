//
//  ProductDetailsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit
import SDWebImage
import SwiftyJSON
import youtube_ios_player_helper



class ProductDetailsVC: UIViewController, YTPlayerViewDelegate {
    @IBOutlet weak var downloadbrochureBtn: UIButton!
    @IBOutlet weak var downloadbrochureHeight: NSLayoutConstraint!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var purchaseNowBtn: UIButton!
    @IBOutlet weak var pincodeTF: UITextField!
    @IBOutlet weak var pinCodePopupView: UIView!
    @IBOutlet weak var viewAllReviewsView: UIView!
    @IBOutlet weak var stackView2: UIStackView!
    @IBOutlet weak var stackView: UIStackView!
    @IBOutlet weak var outOfStockView: UIView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var addToCartBtn: UIButton!
    @IBOutlet weak var viewCartBtn: UIButton!
    @IBOutlet weak var rentBtn: UIButton!
    @IBOutlet weak var quantityLbl: UILabel!
    @IBOutlet weak var minusView: UIView!
    @IBOutlet weak var quantityView: UIView!
    @IBOutlet weak var plusView: UIView!
    @IBOutlet weak var cartView: UIView!
    @IBOutlet weak var likeBtnselectUnselect: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var tableViewHeight: NSLayoutConstraint!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var productDesLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var discountPriceLbl: UILabel!
    @IBOutlet weak var productDetailCollection: UICollectionView!
    @IBOutlet weak var reviewsTable: UITableView!
    @IBOutlet weak var reviewsTableHeight: NSLayoutConstraint!
    @IBOutlet weak var youtubeView: UIView!
    @IBOutlet weak var view1: UIView!
    @IBOutlet weak var view2: UIView!
    @IBOutlet weak var installationVideoViewHeight: NSLayoutConstraint!
    @IBOutlet weak var installationVideoDropDownSelectUnselect: UIButton!
    @IBOutlet weak var demoVideoViewHeight: NSLayoutConstraint!
    @IBOutlet weak var demoVideoDropDownSelectUnselect: UIButton!
    @IBOutlet weak var installationVideoYtPlayer: YTPlayerView!
    @IBOutlet weak var demoVideoYtPlayer: YTPlayerView!
    @IBOutlet weak var specificationsLbl: UILabel!
    @IBOutlet weak var specificationsLine: UILabel!
    @IBOutlet weak var featuresLbl: UILabel!
    @IBOutlet weak var featuresLine: UILabel!
    @IBOutlet weak var youtubeLbl: UILabel!
    @IBOutlet weak var youtubeLine: UILabel!
    @IBOutlet weak var pageControl: UIPageControl!
    
    var productDetailsModal: ProductDetailsModal?
    var counter = 0
    var productId = ""
    var type = 0
    var demoUrl = ""
    var installationUrl = ""
    var borchureUrl = ""
    var quantity = 1
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        scrollView.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        serviceProductDetailsAPI()
        pincodeTF.delegate = self
        pinCodePopupView.isHidden = true
        viewCartBtn.isHidden = true
        viewAllReviewsView.isHidden = true
        outOfStockView.isHidden = true
        stackView.isHidden = true
        cartBtn.isHidden = false
        viewCartBtn.isHidden = true
        rentBtn.isHidden = false
        let layout = UICollectionViewFlowLayout()
        layout.scrollDirection = .horizontal  // Enable horizontal scrolling
        layout.minimumLineSpacing = 0         // No gaps between items
        productDetailCollection.collectionViewLayout = layout
        productDetailCollection.isPagingEnabled = true  // Scroll one-by-one
        productDetailCollection.delegate = self
        productDetailCollection.dataSource = self
        productDetailCollection.register(UINib(nibName: "ProductDetailCell", bundle: nil), forCellWithReuseIdentifier: "ProductDetailCell")
        reviewsTable.delegate = self
        reviewsTable.dataSource = self
        reviewsTable.register(UINib(nibName: "ReviewCell", bundle: nil), forCellReuseIdentifier: "ReviewCell")
        // reviewsTable.separatorStyle = .none
        reviewsTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        tableView.delegate = self
        tableView.dataSource = self
        tableView.register(UINib(nibName: "SpecificationsCell", bundle: nil), forCellReuseIdentifier: "SpecificationsCell")
        tableView.register(UINib(nibName: "FeaturesCell", bundle: nil), forCellReuseIdentifier: "FeaturesCell")
        // tableView.register(UINib(nibName: "YoutubeCell", bundle: nil), forCellReuseIdentifier: "YoutubeCell")
        tableView.separatorStyle = .none
        tableView.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        view1.isHidden = true
        view2.isHidden = true
        installationVideoViewHeight.constant = 80
        demoVideoViewHeight.constant = 80
        demoVideoYtPlayer.delegate = self
        installationVideoYtPlayer.delegate = self
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        //        self.quantityLbl.text = "1"
        //        self.quantity = 1
    }
    // MARK: back swipe gesture func ==>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        if gestureRecognizer.direction == .right {
            navigationController?.popViewController(animated: true)
        }
    }
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if keyPath == "contentSize" {
                if obj == self.reviewsTable {
                    reviewsTableHeight.constant = reviewsTable.contentSize.height
                } else if obj == self.tableView {
                    tableViewHeight.constant = tableView.contentSize.height
                }
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
    @IBAction func downloadBrochureAction(_ sender: UIButton) {
        let borchureUrl = self.borchureUrl
        self.downloadBrochure(brochureUrl: borchureUrl)
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
    @IBAction func shareBtnAction(_ sender: UIButton) {
        
    }
    @IBAction func  enquireNowBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "EnquireNowVC") as! EnquireNowVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.categoryId = productDetailsModal?.data?.productDetails?.categoryId ?? ""
        vc.categoryName = productDetailsModal?.data?.productDetails?.categoryName ?? ""
        self.present(vc, animated: true)
    }
    @IBAction func likeButtonAction(_ sender: UIButton) {
        if sender.isSelected == false
        {
            likeBtnselectUnselect.isSelected = true
            serviceAddToWishListApi(productId: self.productId)
        }
        else if sender.isSelected == true
        {
            likeBtnselectUnselect.isSelected = false
            serviceAddToWishListApi(productId: self.productId)
        }
    }
    @IBAction func minusBtnAction(_ sender: UIButton) {
        self.serviceRemoveToCartAPI(productId: self.productId)
    }
    @IBAction func plusBtnAction(_ sender: UIButton) {
        let isQuantityUnlimited = productDetailsModal?.data?.productDetails?.isQuantityUnlimited ?? 0
        if isQuantityUnlimited == 1{
            self.serviceAddToCartAPI(productId: self.productId)
        }
        else{
            let qty = productDetailsModal?.data?.productDetails?.quantity ?? 0
            if self.quantity < qty {
                self.quantity += 1
                self.quantityLbl.text = "\(self.productDetailsModal?.data?.productDetails?.cartQuantity ?? 0)"
                print("Hit Add To Cart Api")
                print("===> \(self.quantity)")
                self.serviceAddToCartAPI(productId: self.productId)
            } else {
                self.alert(message: "Maximum quantity reached for this product.")
            }
        }
    }
    @IBAction func addToCartBtnAction(_ sender: UIButton) {
        print("===>\(self.quantity)")
        self.serviceAddToCartAPI(productId: self.productId)
    }
    @IBAction func viewAllReviewsAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ReviewsListVC") as! ReviewsListVC
        vc.productId = self.productId
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func buyNowBtnAction(_ sender: UIButton) {
        let cartStatus = self.productDetailsModal?.data?.productDetails?.cartStatus ?? 0
        if cartStatus == 0{
            self.serviceAddToCartAPI(productId: self.productId)
        }
        else{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func  hidePincodePopupAction(_ sender: UIButton) {
        pinCodePopupView.isHidden = true
    }
    @IBAction func rentBtnAction(_ sender: UIButton) {
        pinCodePopupView.isHidden = false
        
    }
    @IBAction func checkBtnAction(_ sender: UIButton) {
        if pincodeTF.text!.isEmpty{
            self.alert(message: "Please enter pincode")
        }
        else{
            serviceSubscriptionInstallationAPI()
        }
    }
    @IBAction func specificationsAction(_ sender: UIButton) {
        self.type = 0
        self.specificationsLbl.textColor = UIColor(hexString: "FF8D3A")
        self.specificationsLine.backgroundColor = UIColor(hexString: "FF8D3A")
        self.featuresLbl.textColor = .black
        self.youtubeLbl.textColor = .black
        self.featuresLine.backgroundColor = .lightGray
        self.youtubeLine.backgroundColor = .lightGray
        tableView.isHidden = false
        view1.isHidden = true
        view2.isHidden = true
        serviceProductDetailsAPI()
    }
    @IBAction func featuresAction(_ sender: UIButton) {
        self.type = 1
        self.featuresLbl.textColor = UIColor(hexString: "FF8D3A")
        self.featuresLine.backgroundColor = UIColor(hexString: "FF8D3A")
        self.specificationsLbl.textColor = .black
        self.youtubeLbl.textColor = .black
        self.specificationsLine.backgroundColor = .lightGray
        self.youtubeLine.backgroundColor = .lightGray
        tableView.isHidden = false
        view1.isHidden = true
        view2.isHidden = true
        serviceProductDetailsAPI()
    }
    @IBAction func youtubeAction(_ sender: UIButton) {
        self.type = 2
        self.youtubeLbl.textColor = UIColor(hexString: "FF8D3A")
        self.youtubeLine.backgroundColor = UIColor(hexString: "FF8D3A")
        self.featuresLbl.textColor = .black
        self.specificationsLbl.textColor = .black
        self.featuresLine.backgroundColor = .lightGray
        self.specificationsLine.backgroundColor = .lightGray
        tableView.isHidden = true
        tableViewHeight.constant = 0
        view1.isHidden = false
        view2.isHidden = false
        self.viewAllReviewsView.isHidden = true
        serviceProductDetailsAPI()
    }
    @IBAction func dropDownBtnInstallationVideoAction(_ sender: UIButton) {
        if sender.isSelected == false{
            self.installationVideoDropDownSelectUnselect.isSelected = true
            installationVideoViewHeight.constant = 250
            let demoUrl = URL(string: self.demoUrl)
            let demoVideoId = demoUrl?.queryParameters["v"]
            installationVideoYtPlayer.load(withVideoId: demoVideoId ?? "", playerVars: ["playsinline": 1])
        }
        else if sender.isSelected == true{
            self.installationVideoDropDownSelectUnselect.isSelected = false
            installationVideoViewHeight.constant = 80
            installationVideoYtPlayer.stopVideo()
        }
    }
    @IBAction func dropDownBtnDemoVideoAction(_ sender: UIButton) {
        if sender.isSelected == false{
            self.demoVideoDropDownSelectUnselect.isSelected = true
            demoVideoViewHeight.constant = 250
            let installationUrl = URL(string: self.installationUrl)
            let installationVideoId = installationUrl?.queryParameters["v"]
            demoVideoYtPlayer.load(withVideoId: installationVideoId ?? "", playerVars: ["playsinline": 2])
        }
        else if sender.isSelected == true{
            self.demoVideoDropDownSelectUnselect.isSelected = false
            demoVideoViewHeight.constant = 80
            demoVideoYtPlayer.stopVideo()
        }
    }
}
extension ProductDetailsVC: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentString: NSString = textField.text! as NSString
        let newString_: NSString =
        currentString.replacingCharacters(in: range, with: string) as NSString
        let maxLength = 6
        let allowedCharacters = "1234567890"
        let allowedCharacterSet = CharacterSet(charactersIn: allowedCharacters)
        let typedCharacterSet = CharacterSet(charactersIn: string)
        let alphabet = allowedCharacterSet.isSuperset(of: typedCharacterSet)
        return alphabet && newString_.length <= maxLength
    }
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        self.pincodeTF.resignFirstResponder()
        return true
    }
}
extension ProductDetailsVC: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return productDetailsModal?.data?.productDetails?.image?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = productDetailCollection.dequeueReusableCell(withReuseIdentifier: "ProductDetailCell", for: indexPath) as! ProductDetailCell
        cell.productImg.sd_setImage(with: URL(string: self.productDetailsModal?.data?.productDetails?.image?[indexPath.item].url ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.detailBtn.tag = indexPath.item
        cell.detailBtn.addTarget(self, action: #selector(detailsBtnTapped(sender:)), for: .touchUpInside)
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return  CGSize(width: (self.productDetailCollection.frame.width), height: self.productDetailCollection.frame.height)
    }
    @objc func detailsBtnTapped(sender: UIButton) {
        let item = sender.tag
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductImageDetailsVC") as! ProductImageDetailsVC
        vc.selectedIndex = item
        vc.productImages = productDetailsModal?.data?.productDetails?.image
        self.navigationController?.pushViewController(vc, animated: true)
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let pageIndex = round(scrollView.contentOffset.x / scrollView.frame.width)
        pageControl.currentPage = Int(pageIndex)
    }
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>) {
        let pageWidth = scrollView.frame.width
        let targetPage = round(targetContentOffset.pointee.x / pageWidth)
        targetContentOffset.pointee.x = targetPage * pageWidth
    }
}
extension ProductDetailsVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == reviewsTable{
            if self.type == 2{
                self.viewAllReviewsView.isHidden = true
                return 0
            }
            else{
                return min(productDetailsModal?.data?.reviews?.count ?? 0, 2)
            }
        }
        else{
            if self.type == 0{
                return productDetailsModal?.data?.productDetails?.specification?.count ?? 0
            }
            else if self.type == 1{
                return productDetailsModal?.data?.productDetails?.features?.count ?? 0
            }
            else if self.type == 2{
                return 0
            }
        }
        return 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if tableView == reviewsTable{
            let cell = reviewsTable.dequeueReusableCell(withIdentifier: "ReviewCell", for: indexPath) as! ReviewCell
            cell.dateLbl.text = productDetailsModal?.data?.reviews?[indexPath.row].createdAt?.date() ?? ""
            cell.reviewLbl.text = productDetailsModal?.data?.reviews?[indexPath.row].review ?? ""
            cell.ratingView.rating = Double(productDetailsModal?.data?.reviews?[indexPath.row].rating ?? 0.0)
            cell.ratingView.isUserInteractionEnabled = false
            let firstName = productDetailsModal?.data?.reviews?[indexPath.row].userProfile?.firstName ?? ""
            let lastName = productDetailsModal?.data?.reviews?[indexPath.row].userProfile?.lastName ?? ""
            cell.nameLbl.text = "\(firstName) \(lastName)"
            cell.profileImg.sd_setImage(with: URL(string: productDetailsModal?.data?.reviews?[indexPath.row].userProfile?.profileImage ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
            var images = productDetailsModal?.data?.reviews?[indexPath.row].image?.compactMap({ $0.url }) ?? []
            if let img = productDetailsModal?.data?.reviews?[indexPath.row].image, let reviewImageUrl = img as? String, !reviewImageUrl.isEmpty {
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
        else{
            if self.type == 0{
                let cell = tableView.dequeueReusableCell(withIdentifier: "SpecificationsCell", for: indexPath) as! SpecificationsCell
                cell.filedNameLbl.text = productDetailsModal?.data?.productDetails?.specification?[indexPath.row].filedName ?? ""
                cell.valueLbl.text = productDetailsModal?.data?.productDetails?.specification?[indexPath.row].value ?? ""
                if indexPath.row % 2 == 0 {
                    cell.view.backgroundColor = .white
                }
                else{
                    cell.view.backgroundColor = .systemGray6
                }
                cell.selectionStyle = .none
                return cell
            }
            else if self.type == 1{
                let cell = tableView.dequeueReusableCell(withIdentifier: "FeaturesCell", for: indexPath) as! FeaturesCell
                cell.lbl.text = productDetailsModal?.data?.productDetails?.features?[indexPath.row].label ?? ""
                cell.descriptionLbl.text = productDetailsModal?.data?.productDetails?.features?[indexPath.row].description ?? ""
                return cell
            }
            return UITableViewCell()
        }
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return reviewsTable.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
}
extension ProductDetailsVC {
    func playerViewDidBecomeReady(_ playerView: YTPlayerView) {
        playerView.playVideo()
    }
    func playerView(_ playerView: YTPlayerView, didChangeTo state: YTPlayerState) {
        // Handle player state changes
    }
    func playerView(_ playerView: YTPlayerView, receivedError error: YTPlayerError) {
        // Handle player errors
    }
}
extension URL{
    var queryParameters: [String: String] {
        var params = [String: String]()
        if let components = URLComponents(url: self, resolvingAgainstBaseURL: true),
           let queryItems = components.queryItems {
            for item in queryItems {
                params[item.name] = item.value
            }
        }
        return params
    }
}
// MARK: downloadBrochure Extension ==>
extension UIViewController{
    func downloadBrochure(brochureUrl: String) {
        self.startLoading() // Start loading indicator
        guard let url = URL(string: brochureUrl) else {
            print("Invalid URL")
            self.stopLoading()
            self.alert(message: "Brochure file not found.", title: "Error")
            return
        }
        let task = URLSession.shared.downloadTask(with: url) { (location, response, error) in
            guard let location = location, error == nil else {
                DispatchQueue.main.async {
                    self.stopLoading()
                    print("Download error: \(String(describing: error))")
                    self.alert(message: "Failed to download the brochure.", title: "Error")
                }
                return
            }
            // Get the destination path to save the file
            let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            let destinationURL = documentsDirectory?.appendingPathComponent(url.lastPathComponent)
            do {
                if let destinationURL = destinationURL {
                    // Move the downloaded file to the destination URL
                    try FileManager.default.moveItem(at: location, to: destinationURL)
                    print("File downloaded to: \(destinationURL.path)")
                    
                    DispatchQueue.main.async {
                        self.stopLoading()
                        self.alert(message: "Brochure downloaded successfully.", title: "Success")
                        // Optionally, you can present a preview of the file here
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    self.stopLoading()
                    print("Error saving file: \(error)")
                    self.alert(message: "The brochure has already been downloaded.", title: "File Exists")
                }
            }
        }
        task.resume() // Start the download task
    }
}
// MARK: ProductDetailsAPI ===>
extension ProductDetailsVC{
    func serviceProductDetailsAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productDetails?id=\(self.productId)&deviceId=\(deviceId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productDetailsModal = try JSONDecoder().decode(ProductDetailsModal.self, from: JSONResponse.rawData())
                    self.pageControl.currentPage = 0
                    self.pageControl.numberOfPages = self.productDetailsModal?.data?.productDetails?.image?.count ?? 0
                    let reviews = self.productDetailsModal?.data?.reviews ?? []
                    self.viewAllReviewsView.isHidden = reviews.isEmpty
                    self.productDetailCollection.reloadData()
                    self.tableView.reloadData()
                    self.reviewsTable.reloadData()
                    self.setData()
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
        scrollView.isHidden = false
        self.productNameLbl.text = self.productDetailsModal?.data?.productDetails?.productName ?? ""
        let price = self.productDetailsModal?.data?.productDetails?.price ?? 0
        self.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        let priceToDisplay = self.productDetailsModal?.data?.productDetails?.priceToDisplay ?? 0
        self.discountPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: priceToDisplay), number: .decimal))"
        let des = self.productDetailsModal?.data?.productDetails?.description
        self.productDesLbl.attributedText = des?.description.htmlToAttributedString_(fontName: "System", fontSize: 17)!
        self.demoUrl = self.productDetailsModal?.data?.productDetails?.youtube?.first?.demoVideo ?? ""
        self.installationUrl = self.productDetailsModal?.data?.productDetails?.youtube?.first?.installationVideo ?? ""
        let uploadBrocheure = self.productDetailsModal?.data?.productDetails?.uploadBrocheure ?? ""
        self.borchureUrl = uploadBrocheure
        if uploadBrocheure == ""{
            downloadbrochureBtn.isHidden = true
            downloadbrochureHeight.constant = 0
        }
        else{
            downloadbrochureBtn.isHidden = false
            downloadbrochureHeight.constant = 60
        }
        let cartQuantity = self.productDetailsModal?.data?.productDetails?.cartQuantity ?? 0
        if cartQuantity == 0{
            cartView.isHidden = false
            quantityView.isHidden = true
            plusView.isHidden = true
            minusView.isHidden = true
            // purchaseNowBtn.isHidden = true
        }
        else{
            cartView.isHidden = true
            quantityView.isHidden = false
            plusView.isHidden = false
            minusView.isHidden = false
            // purchaseNowBtn.isHidden = false
            self.quantityLbl.text = "\(self.productDetailsModal?.data?.productDetails?.cartQuantity ?? 0)"
        }
        let quntity = self.productDetailsModal?.data?.productDetails?.quantity ?? 0
        if quntity == 0{
            self.outOfStockView.isHidden = false
            self.stackView.isHidden = true
            self.stackView2.isHidden = true
        }
        else if quntity == -1{
            self.outOfStockView.isHidden = true
            self.stackView.isHidden = false
            self.stackView2.isHidden = false
        }
        else{
            self.outOfStockView.isHidden = true
            self.stackView.isHidden = false
            self.stackView2.isHidden = false
        }
        if self.productDetailsModal?.data?.subscriptionPrice?.currencyType ?? "" == "INR"{
            let subPricel = self.productDetailsModal?.data?.subscriptionPrice?.price ?? 0
            self.rentBtn.isHidden = false
            rentBtn.setTitle("Rent@ \(subPricel)", for: .normal)
        }
        else{
            self.rentBtn.isHidden = true
        }
        if self.productDetailsModal?.data?.productDetails?.cartStatus ?? 0 == 1{
            //self.viewCartBtn.isHidden = false
            // self.addToCartBtn.isHidden = true
        }
        else{
            //   self.addToCartBtn.isHidden = false
            // self.viewCartBtn.isHidden = true
        }
        if self.productDetailsModal?.data?.productDetails?.is_wishlist ?? 0 == 1{
            self.likeBtnselectUnselect.isSelected = true
        }
        else{
            self.likeBtnselectUnselect.isSelected = false
        }
    }
}
// MARK: AddToCart ==>
extension ProductDetailsVC {
    func serviceAddToCartAPI(productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let params = ["deviceId": deviceId, "productId": productId, "quantity": "1"] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToCart", params: params, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                print("itemAddInCart")
                self.serviceProductDetailsAPI()
                self.updateCartBadge()
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
extension ProductDetailsVC {
    func serviceRemoveToCartAPI(productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let params = ["deviceId": deviceId, "productId": productId, "quantity": "-1"] as [String: Any]
        print(params)
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToCart", params: params, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                print("itemAddInCart")
                self.serviceProductDetailsAPI()
                self.updateCartBadge()
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
// MARK: AddToWishListAPI ==>
extension ProductDetailsVC {
    func serviceAddToWishListApi(productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["productId": productId] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToWishlist", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print("Deslike") // Deslike
            case 1:
                print("Like") // Like
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
extension ProductDetailsVC {
    func serviceSubscriptionInstallationAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let pincode = Int(self.pincodeTF.text!) ?? 0
        let parms = ["productId": self.productId, "pincode": pincode] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "subscriptionInstallation", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                //  self.alert(message: JSONResponse["message"].stringValue)
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "InstallationNotAvailableAlertVC") as! InstallationNotAvailableAlertVC
                vc.modalPresentationStyle = .overCurrentContext
                vc.modalTransitionStyle = .crossDissolve
                self.present(vc, animated: true)
            case 1:
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "SubscribeVC") as! SubscribeVC
                vc.productId = self.productId
                vc.pincode = pincode
                self.navigationController?.pushViewController(vc, animated: true)
                
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

