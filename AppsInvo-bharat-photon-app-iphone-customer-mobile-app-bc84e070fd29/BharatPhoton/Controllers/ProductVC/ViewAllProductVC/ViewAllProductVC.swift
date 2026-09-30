//
//  ViewAllProductVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 26/12/24.
//

import UIKit

class ViewAllProductVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var productCollection: UICollectionView!
    var viewAllProductModal: ViewAllProductModal?
    var subcategoryId = ""
    var categoryId = ""
    var currentPage = 1
    var isLoading = false
    var hasMoreData = true
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceProductListAPI(page: currentPage)
        msgLbl.isHidden = true
        productCollection.delegate = self
        productCollection.dataSource = self
        productCollection.register(UINib(nibName: "ProductCollectionCell", bundle: nil), forCellWithReuseIdentifier: "ProductCollectionCell")
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
extension ViewAllProductVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return viewAllProductModal?.data?.productList?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = productCollection.dequeueReusableCell(withReuseIdentifier: "ProductCollectionCell", for: indexPath) as! ProductCollectionCell
        let priceToDisplay = self.viewAllProductModal?.data?.productList?[indexPath.item].priceToDisplay ?? 0
        cell.discountPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: priceToDisplay), number: .decimal))"
        cell.discountPercentageLbl.text = "\(self.viewAllProductModal?.data?.productList?[indexPath.item].discountPercentage ?? 0)% OFF"
        cell.productNameLbl.text = self.viewAllProductModal?.data?.productList?[indexPath.item].productName
        cell.catNameLbl.text = self.viewAllProductModal?.data?.productList?[indexPath.item].categoryName
        let price = self.viewAllProductModal?.data?.productList?[indexPath.item].price ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        let imageUrlString = self.viewAllProductModal?.data?.productList?[indexPath.item].image?.first?.url ?? ""
        cell.productImg.sd_setImage(with: URL(string: imageUrlString), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.wishListBtn.tag = indexPath.item // For product index
        cell.wishListBtn.addTarget(self, action: #selector(wishListBtnTapped(sender:)), for: .touchUpInside)
        cell.cartBtn.tag = indexPath.item
        cell.cartBtn.addTarget(self, action: #selector(addtoCartBtnTapped(sender:)), for: .touchUpInside)
        cell.viewDetailBtn.tag = indexPath.item
        cell.viewDetailBtn.addTarget(self, action: #selector(ViewDetailBtnAction(sender:)), for: .touchUpInside)
        let isWishlist = self.viewAllProductModal?.data?.productList?[indexPath.item].is_wishlist ?? 0
        cell.wishListBtn.isSelected = (isWishlist == 1)
        let cartStatus = self.viewAllProductModal?.data?.productList?[indexPath.item].cartStatus ?? 0
        cell.cartBtn.isSelected = (cartStatus == 1)
        let rating = self.viewAllProductModal?.data?.productList?[indexPath.item].rating ?? 0
        if rating == 0{
            cell.ratingView.isHidden = true
        }
        else{
            cell.ratingView.isHidden = false
            cell.ratingLbl.text = "\(rating)"
        }
        let quantity = self.viewAllProductModal?.data?.productList?[indexPath.item].quantity ?? 0
        if quantity == 0{
            cell.catNameLbl.isHidden = true
            cell.outOfStockView.isHidden = false
            cell.cartView.isHidden = true
        }
        else if quantity == -1{
            cell.catNameLbl.isHidden = false
            cell.outOfStockView.isHidden = true
            cell.cartView.isHidden = false
        }
        else{
            cell.catNameLbl.isHidden = false
            cell.outOfStockView.isHidden = true
            cell.cartView.isHidden = false
        }
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (self.productCollection.bounds.width/1.9)-32, height: (self.productCollection.bounds.width/2)+30)
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let frameHeight = scrollView.frame.size.height
        
        if offsetY > contentHeight - frameHeight - 100 {
            if !isLoading && hasMoreData {
                currentPage += 1
                serviceProductListAPI(page: currentPage)
            }
        }
    }
    // MARK: ViewDetailsAction ==>
    @objc func ViewDetailBtnAction(sender: UIButton) {
        let productIndex = sender.tag
        let productId = self.viewAllProductModal?.data?.productList?[productIndex]._id ?? ""
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductDetailsVC") as! ProductDetailsVC
        vc.productId = productId
        self.navigationController?.pushViewController(vc, animated: true)
    }
    // MARK: WishListAction ==>
    @objc func wishListBtnTapped(sender: UIButton) {
        let productIndex = sender.tag
        let productId = self.viewAllProductModal?.data?.productList?[productIndex]._id ?? ""
        self.serviceAddToWishListApi(productId: productId)
    }
    // MARK: AddToCART ==>
    @objc func addtoCartBtnTapped(sender: UIButton) {
        let productIndex = sender.tag
        let productImageURL = self.viewAllProductModal?.data?.productList?[productIndex].image?.first?.url ?? ""
        let productId = self.viewAllProductModal?.data?.productList?[productIndex]._id ?? ""
        let cartStatus = self.viewAllProductModal?.data?.productList?[productIndex].cartStatus ?? 0
        if cartStatus == 1{
            alert(message: "Product already add in cart")
        }
        else{
            let indexPath = IndexPath(item: productIndex, section: 0)
            if let cell = productCollection.cellForItem(at: indexPath) as? ProductCollectionCell {
                // Load the product image using SDWebImage
                if let imageURL = URL(string: productImageURL) {
                    cell.productImg.sd_setImage(with: imageURL, placeholderImage: UIImage(named: "placeholder"))
                }
                // Take a snapshot of the product image for the animation
                guard let productImage = cell.productImg.snapshot(),
                      let window = UIApplication.shared.keyWindow else {
                          return
                      }
                // Create an animation image view from the snapshot
                let animationImageView = UIImageView(image: productImage)
                animationImageView.frame = cell.productImg.convert(cell.productImg.bounds, to: window)
                animationImageView.layer.cornerRadius = cell.productImg.layer.cornerRadius
                animationImageView.clipsToBounds = true
                window.addSubview(animationImageView)
                
                // Calculate the target frame at the top-right of the screen (near the cart button)
                let targetFrame = CGRect(x: window.frame.width - 70,  // Top-right corner x position
                                         y: 50,                      // Top y position
                                         width: 20, height: 20)      // Target size
                // Perform the animation
                
                UIView.animate(withDuration: 0.8, delay: 0, options: .curveEaseInOut, animations: {
                    animationImageView.frame = targetFrame
                    animationImageView.alpha = 0.5
                }) { _ in
                    animationImageView.removeFromSuperview()
                    // Uncomment to update cart count after animation
                    // self.updateCartCount()
                }
            }
            serviceAddToCartApi(productId: productId, quantity: "1")
            print("Hit Add To Cart API with productId: \(productId)")
        }
    }
}
// MARK: ProductListAPI ==>
extension ViewAllProductVC {
    func serviceProductListAPI(page: Int) {
            guard !isLoading, hasMoreData else { return }
            isLoading = true
            if page == 1 {
                LoaderManager.shared.showLoader(in: self.view)
            }
            let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
            let token = UserDefaults.standard.string(forKey: "token") ?? ""
            let url = "\(ServiceEndPoint.BhartPhotonUrl.baseURL)listByCatSubcategory?categoryId=\(categoryId)&page=\(page)&limit=10&deviceId=\(deviceId)&subcategoryId=\(subcategoryId)"
            BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: url, params: [:], token: token) { JSONResponse in
                LoaderManager.shared.hideLoader()
                self.isLoading = false
                let code = JSONResponse["code"].intValue
                switch code {
                case 0:
                    self.alert(message: JSONResponse["message"].stringValue)
                case 1:
                    do {
                        let newData = try JSONDecoder().decode(ViewAllProductModal.self, from: JSONResponse.rawData())
                        if page == 1 {
                            self.viewAllProductModal = newData
                        } else {
                            self.viewAllProductModal?.data?.productList?.append(contentsOf: newData.data?.productList ?? [])
                        }
                        self.hasMoreData = !(newData.data?.productList?.isEmpty ?? true)
                        self.msgLbl.isHidden = (self.viewAllProductModal?.data?.productList?.count ?? 0) > 0
                        self.productCollection.reloadData()

                        // Automatically load next page if hasMoreData
                        if self.hasMoreData {
                            self.currentPage += 1
                            self.serviceProductListAPI(page: self.currentPage)
                        }

                    } catch {
                        print("Decoding error: \(error)")
                    }

                default:
                    print("Unhandled response code")
                }

            } failure: { error, JSONResponse in
                LoaderManager.shared.hideLoader()
                self.isLoading = false
                self.alert(message: JSONResponse["message"].stringValue, title: error)
            }
        }
        /// Call this to fully reload all data from page 1
        func reloadAllPages() {
            self.viewAllProductModal?.data?.productList?.removeAll()
            self.currentPage = 1
            self.hasMoreData = true
            self.serviceProductListAPI(page: currentPage)
        }
    }
// MARK: AddToWishList API ==>
extension ViewAllProductVC {
    func serviceAddToWishListApi(productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["productId": productId] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToWishlist", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.reloadAllPages() // Deslike
            case 1:
                self.reloadAllPages() // Like
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
// MARK: AddToCart ==>
extension ViewAllProductVC {
    func serviceAddToCartApi(productId: String, quantity: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let params = ["deviceId": deviceId, "productId": productId, "quantity": quantity] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToCart", params: params, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                print("itemAddInCart")
                self.updateCartBadge()
                self.serviceProductListAPI(page: self.currentPage)
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
