//
//  ProductVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit

class ProductVC: UIViewController {
    @IBOutlet weak var categoryNameLbl: UILabel!
    @IBOutlet weak var descriptionLbl: UILabel!
    @IBOutlet weak var viewMoreButton: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var productSubCatCollection: UICollectionView!
    @IBOutlet weak var productCollection: UICollectionView!
    @IBOutlet weak var productCollectionHeight: NSLayoutConstraint!
    @IBOutlet weak var searchTF: UITextField!
    var categoryListModel: CategoryModal?
    var productListModal: ProductListModal?
    var productSearchingModal: ProductSearchingModal?
    var homeProductSearchModal: HomeProductSearchModal?
    var categoryId = ""
    var selectedIndex: Int?
    var isExpanded = false // Track the label state
    var searchtxt = ""
    var isHomeSearching = 0
    var isSearching = false
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
            overrideUserInterfaceStyle = .light
        }
        self.viewMoreButton.isHidden = true
        self.msgLbl.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        serviceCategoryListAPI()
        serviceProductListAPI(categoryId: self.categoryId)
        productSubCatCollection.delegate = self
        productSubCatCollection.dataSource = self
        productSubCatCollection.register(UINib(nibName: "ProductSubCategoryCell", bundle: nil), forCellWithReuseIdentifier: "ProductSubCategoryCell")
        productCollection.delegate = self
        productCollection.dataSource = self
        productCollection.register(UINib(nibName: "ProductCollectionCell", bundle: nil), forCellWithReuseIdentifier: "ProductCollectionCell")
        productCollection.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        productCollection.register(HeaderView.self, forSupplementaryViewOfKind: UICollectionView.elementKindSectionHeader, withReuseIdentifier: "HeaderView")
        searchTF.addTarget(self, action: #selector(searchTFAction), for: .editingDidBegin)
        searchTF.delegate = self
        if isHomeSearching == 1{
            print("ShowHomeSearchingProduct")
            serviceHomeSearchingProductListAPI(searchtxt: searchtxt)
        }
        else{
            serviceCategoryListAPI()
            serviceProductListAPI(categoryId: self.categoryId)
        }
        
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        // Scroll after the layout is set and data is loaded.
        if let index = selectedIndex, index < productSubCatCollection.numberOfItems(inSection: 0) {
            productSubCatCollection.scrollToItem(at: IndexPath(item: index, section: 0), at: .centeredHorizontally, animated: true)
        }
    }
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UICollectionView {
            if obj == self.productCollection && keyPath == "contentSize" {
                productCollectionHeight.constant = productCollection.contentSize.height
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
    @IBAction func filterAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "FilterVC") as! FilterVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        self.present(vc, animated: true)
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
    @IBAction func viewMoreTapped(_ sender: UIButton) {
        isExpanded.toggle() // Toggle the state
        self.descriptionLbl.numberOfLines = isExpanded ? 0 : 3
        viewMoreButton.setTitle(isExpanded ? "View Less" : "View More", for: .normal)
        UIView.animate(withDuration: 0.3) {
            self.view.layoutIfNeeded()
        }
    }
}
extension ProductVC: UITextFieldDelegate {
    func textFieldDidBeginEditing(_ textField: UITextField) {
        print("Search text field tapped!")
    }
    @objc func searchTFAction() {
        let text = searchTF.text ?? ""
        print("Current search text: \(text)")
        
        if text.isEmpty {
            isSearching = false
            self.productCollection.reloadData()
        } else {
            isSearching = true
            self.serviceProductSearchingAPI(searchtxt: text)
        }
    }
    // Called when the user taps "Return" on the keyboard
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        searchTF.resignFirstResponder()
        print("Done button tapped. Current text: \(searchTF.text ?? "")")
        let text = searchTF.text ?? ""
        if !text.isEmpty {
            isSearching = true
            self.serviceProductSearchingAPI(searchtxt: text)
        } else {
            isSearching = false
            serviceProductListAPI(categoryId: self.categoryId)
            //self.productCollection.reloadData()
        }
        return true
    }
}
extension ProductVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {

    func numberOfSections(in collectionView: UICollectionView) -> Int {
        if collectionView == productSubCatCollection {
            return 1
        } else if isSearching || isHomeSearching == 1 {
            return 1
        } else {
            return productListModal?.data?.subcategoryListing?.count ?? 0
        }
    }

    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if collectionView == productSubCatCollection {
            return categoryListModel?.data?.count ?? 0
        } else if isSearching {
            return productSearchingModal?.data?.count ?? 0
        } else if isHomeSearching == 1 {
            return homeProductSearchModal?.data?.count ?? 0
        } else {
            return productListModal?.data?.subcategoryListing?[section].products?.count ?? 0
        }
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {

        if collectionView == productSubCatCollection {
            let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ProductSubCategoryCell", for: indexPath) as! ProductSubCategoryCell
            let data = categoryListModel?.data?[indexPath.item]
            cell.subCategoryNameLbl.text = data?.categoryName ?? ""
            cell.subCategoryImg.sd_setImage(with: URL(string: data?.image ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
            cell.selectedItemView.isHidden = (indexPath.item != selectedIndex)
            return cell
        }

        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "ProductCollectionCell", for: indexPath) as! ProductCollectionCell

        if isSearching {
            let product = productSearchingModal?.data?[indexPath.item]
            let priceToDisplay = product?.priceToDisplay ?? 0
            cell.discountPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: priceToDisplay), number: .decimal))"
            cell.discountPercentageLbl.text = "\(product?.discountPercentage ?? 0)% OFF"
            cell.productNameLbl.text = product?.productName
            cell.catNameLbl.text = product?.categoryName
            let price = product?.price ?? 0
            cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
            let imageUrlString = product?.image?.first?.url ?? ""
            cell.productImg.sd_setImage(with: URL(string: imageUrlString), placeholderImage: UIImage(named: "profilePlaceholder"))

            cell.wishListBtn.tag = indexPath.item
            cell.cartBtn.tag = indexPath.item
            cell.viewDetailBtn.tag = indexPath.item

            cell.wishListBtn.addTarget(self, action: #selector(wishListBtnTapped(sender:)), for: .touchUpInside)
            cell.cartBtn.addTarget(self, action: #selector(addtoCartBtnTapped(sender:)), for: .touchUpInside)
            cell.viewDetailBtn.addTarget(self, action: #selector(ViewDetailBtnAction(sender:)), for: .touchUpInside)

            cell.wishListBtn.isSelected = (product?.is_wishlist == 1)
            cell.cartBtn.isSelected = (product?.cartStatus == 1)
            let quantity = product?.quantity ?? 0
            cell.catNameLbl.isHidden = (quantity == 0)
            cell.outOfStockView.isHidden = (quantity != 0)
            cell.cartView.isHidden = (quantity == 0)

            let rating = product?.rating ?? 0
            if rating == 0 {
                cell.ratingView.isHidden = true
            } else {
                cell.ratingView.isHidden = false
                cell.ratingLbl.text = "\(rating)"
            }
        } else if isHomeSearching == 1 {
            let product = homeProductSearchModal?.data?[indexPath.item]
            let priceToDisplay = product?.priceToDisplay ?? 0
            cell.discountPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: priceToDisplay), number: .decimal))"
            cell.discountPercentageLbl.text = "\(product?.discountPercentage ?? 0)% OFF"
            cell.productNameLbl.text = product?.productName
            cell.catNameLbl.text = product?.categoryName
            let price = product?.price ?? 0
            cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
            let imageUrlString = product?.image?.first?.url ?? ""
            cell.productImg.sd_setImage(with: URL(string: imageUrlString), placeholderImage: UIImage(named: "profilePlaceholder"))

            cell.wishListBtn.tag = indexPath.item
            cell.cartBtn.tag = indexPath.item
            cell.viewDetailBtn.tag = indexPath.item

            cell.wishListBtn.addTarget(self, action: #selector(wishListBtnTapped(sender:)), for: .touchUpInside)
            cell.cartBtn.addTarget(self, action: #selector(addtoCartBtnTapped(sender:)), for: .touchUpInside)
            cell.viewDetailBtn.addTarget(self, action: #selector(ViewDetailBtnAction(sender:)), for: .touchUpInside)

            cell.wishListBtn.isSelected = (product?.is_wishlist == 1)
            cell.cartBtn.isSelected = (product?.cartStatus == 1)
            let quantity = product?.quantity ?? 0
            cell.catNameLbl.isHidden = (quantity == 0)
            cell.outOfStockView.isHidden = (quantity != 0)
            cell.cartView.isHidden = (quantity == 0)

            let rating = product?.rating ?? 0
            if rating == 0 {
                cell.ratingView.isHidden = true
            } else {
                cell.ratingView.isHidden = false
                cell.ratingLbl.text = "\(rating)"
            }
        } else {
            let product = productListModal?.data?.subcategoryListing?[indexPath.section].products?[indexPath.item]
            let priceToDisplay = product?.priceToDisplay ?? 0
            cell.discountPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: priceToDisplay), number: .decimal))"
            cell.discountPercentageLbl.text = "\(product?.discountPercentage ?? 0)% OFF"
            cell.productNameLbl.text = product?.productName
            cell.catNameLbl.text = product?.categoryName
            let price = product?.price ?? 0
            cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
            let imageUrlString = product?.image?.first?.url ?? ""
            cell.productImg.sd_setImage(with: URL(string: imageUrlString), placeholderImage: UIImage(named: "profilePlaceholder"))

            cell.wishListBtn.tag = indexPath.item
            cell.wishListBtn.superview?.tag = indexPath.section
            cell.cartBtn.tag = indexPath.item
            cell.cartBtn.superview?.tag = indexPath.section
            cell.viewDetailBtn.tag = indexPath.item
            cell.viewDetailBtn.superview?.tag = indexPath.section

            cell.wishListBtn.addTarget(self, action: #selector(wishListBtnTapped(sender:)), for: .touchUpInside)
            cell.cartBtn.addTarget(self, action: #selector(addtoCartBtnTapped(sender:)), for: .touchUpInside)
            cell.viewDetailBtn.addTarget(self, action: #selector(ViewDetailBtnAction(sender:)), for: .touchUpInside)

            cell.wishListBtn.isSelected = (product?.is_wishlist == 1)
            cell.cartBtn.isSelected = (product?.cartStatus == 1)
            let quantity = product?.quantity ?? 0
            cell.catNameLbl.isHidden = (quantity == 0)
            cell.outOfStockView.isHidden = (quantity != 0)
            cell.cartView.isHidden = (quantity == 0)

            let rating = product?.rating ?? 0
            if rating == 0 {
                cell.ratingView.isHidden = true
            } else {
                cell.ratingView.isHidden = false
                cell.ratingLbl.text = "\(rating)"
            }
        }

        return cell
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        if collectionView == productSubCatCollection {
            return CGSize(width: (self.productSubCatCollection.bounds.width / 3.45) - 32, height: 120)
        } else {
            return CGSize(width: (self.productCollection.bounds.width / 1.9) - 32, height: (self.productCollection.bounds.width / 2) + 30)
        }
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        if collectionView == productSubCatCollection {
            self.isHomeSearching = 0
            self.isSearching = false
            self.searchTF.text = ""
            self.categoryId = categoryListModel?.data?[indexPath.item]._id ?? ""
            self.serviceProductListAPI(categoryId: self.categoryId)
            selectedIndex = indexPath.item
            productSubCatCollection.reloadData()
        }
    }
    func collectionView(_ collectionView: UICollectionView, didDeselectItemAt indexPath: IndexPath) {
        productSubCatCollection.reloadData()
    }

    func collectionView(_ collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, at indexPath: IndexPath) -> UICollectionReusableView {
        if collectionView != productSubCatCollection && kind == UICollectionView.elementKindSectionHeader && !isSearching && isHomeSearching == 0 {
            let headerView = collectionView.dequeueReusableSupplementaryView(ofKind: kind, withReuseIdentifier: "HeaderView", for: indexPath) as! HeaderView
            let sectionTitle = productListModal?.data?.subcategoryListing?[indexPath.section].subcategoryName ?? ""
            headerView.titleLabel.text = sectionTitle
            headerView.viewAllButton.addTarget(self, action: #selector(viewAllTapped), for: .touchUpInside)
            headerView.viewAllButton.tag = indexPath.section
            return headerView
        }
        return UICollectionReusableView()
    }

    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, referenceSizeForHeaderInSection section: Int) -> CGSize {
        if collectionView != productSubCatCollection && !isSearching && isHomeSearching == 0 {
            return CGSize(width: collectionView.frame.width, height: 50)
        }
        return .zero
    }
    @objc func viewAllTapped(sender: UIButton) {
        let row = sender.tag
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ViewAllProductVC") as! ViewAllProductVC
        vc.subcategoryId = productListModal?.data?.subcategoryListing?[row]._id ?? ""
        vc.categoryId = categoryId
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @objc func ViewDetailBtnAction(sender: UIButton) {
        let productIndex = sender.tag
           let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductDetailsVC") as! ProductDetailsVC
           if isSearching {
               // Case: Product search via text
               vc.productId = productSearchingModal?.data?[productIndex]._id ?? ""
           }
           else if isHomeSearching == 1 {
               // Case: Home screen search
               vc.productId = homeProductSearchModal?.data?[productIndex]._id ?? ""
           }
           else {
               // Case: Default product list per subcategory
               if let subcategoryIndex = sender.superview?.tag {
                   vc.productId = productListModal?.data?.subcategoryListing?[subcategoryIndex].products?[productIndex]._id ?? ""
               }
           }
           self.navigationController?.pushViewController(vc, animated: true)
       }
    @objc func wishListBtnTapped(sender: UIButton) {
        let productIndex = sender.tag
        if isHomeSearching == 1{
            let productId = homeProductSearchModal?.data?[productIndex]._id ?? ""
            self.serviceAddToWishListApi(productId: productId)
        }
        if let subcategoryIndex = sender.superview?.tag {
            let productId = productListModal?.data?.subcategoryListing?[subcategoryIndex].products?[productIndex]._id ?? ""
            self.serviceAddToWishListApi(productId: productId)
        }
    }
    @objc func addtoCartBtnTapped(sender: UIButton) {
        let productIndex = sender.tag
        if isHomeSearching == 1 {
            let cartStatus = homeProductSearchModal?.data?[productIndex].cartStatus ?? 0
            if cartStatus == 1 {
                alert(message: "Product already added to cart")
            } else {
                let productId = homeProductSearchModal?.data?[productIndex]._id ?? ""
                serviceAddToCartApi(productId: productId, quantity: "1")
                print("Hit Add To Cart API with productId (searching): \(productId)")
            }
        } else if let subcategoryIndex = sender.superview?.tag {
            let cartStatus = productListModal?.data?.subcategoryListing?[subcategoryIndex].products?[productIndex].cartStatus ?? 0
            if cartStatus == 1 {
                alert(message: "Product already added to cart")
            } else {
                let productId = productListModal?.data?.subcategoryListing?[subcategoryIndex].products?[productIndex]._id ?? ""
                let indexPath = IndexPath(item: productIndex, section: subcategoryIndex)
                if let cell = productCollection.cellForItem(at: indexPath) as? ProductCollectionCell,
                   let window = UIApplication.shared.keyWindow,
                   let productImage = cell.productImg.snapshot() {
                    let animationImageView = UIImageView(image: productImage)
                    animationImageView.frame = cell.productImg.convert(cell.productImg.bounds, to: window)
                    animationImageView.layer.cornerRadius = cell.productImg.layer.cornerRadius
                    animationImageView.clipsToBounds = true
                    window.addSubview(animationImageView)
                    let targetFrame = CGRect(x: window.frame.width - 70, y: 50, width: 20, height: 20)
                    UIView.animate(withDuration: 0.8, delay: 0, options: .curveEaseInOut, animations: {
                        animationImageView.frame = targetFrame
                        animationImageView.alpha = 0.5
                    }) { _ in
                        animationImageView.removeFromSuperview()
                    }
                }
                serviceAddToCartApi(productId: productId, quantity: "1")
                print("Hit Add To Cart API with productId: \(productId)")
            }
        }
    }
}
extension UIView {
    func snapshot() -> UIImage? {
        UIGraphicsBeginImageContextWithOptions(self.bounds.size, false, 0)
        drawHierarchy(in: self.bounds, afterScreenUpdates: true)
        let snapshotImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return snapshotImage
    }
}
extension UILabel {
    var visibleLineCount: Int {
        guard let text = self.text, let font = self.font else { return 0 }
        
        let maxSize = CGSize(width: self.frame.width, height: CGFloat.greatestFiniteMagnitude)
        let textStorage = NSTextStorage(attributedString: self.attributedText ?? NSAttributedString(string: text))
        let textContainer = NSTextContainer(size: maxSize)
        textContainer.lineFragmentPadding = 0
        textContainer.lineBreakMode = self.lineBreakMode
        textContainer.maximumNumberOfLines = 0
        
        let layoutManager = NSLayoutManager()
        layoutManager.addTextContainer(textContainer)
        textStorage.addLayoutManager(layoutManager)
        layoutManager.glyphRange(for: textContainer)
        
        return layoutManager.numberOfLines()
    }
}
extension NSLayoutManager {
    func numberOfLines() -> Int {
        var numberOfLines = 0
        var index = 0
        var lineRange = NSRange(location: 0, length: 0)
        
        while index < self.numberOfGlyphs {
            self.lineFragmentRect(forGlyphAt: index, effectiveRange: &lineRange)
            index = NSMaxRange(lineRange)
            numberOfLines += 1
        }
        
        return numberOfLines
    }
}
// MARK: CategoryListAPI ==>
extension ProductVC {
    func serviceCategoryListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "categoryListing", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.categoryListModel = try JSONDecoder().decode(CategoryModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.categoryListModel?.data?.count ?? 0) == 0 ? false : true
                    self.productSubCatCollection.reloadData()
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
// MARK: ProductListAPI ==>
extension ProductVC {
    func serviceProductListAPI(categoryId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productListing?categoryId=\(categoryId)&deviceId=\(deviceId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            print(JSONResponse)
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productListModal = try JSONDecoder().decode(ProductListModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.productListModal?.data?.subcategoryListing?.count ?? 0) == 0 ? false : true
                    self.productCollection.reloadData()
                    self.setdata()
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
    func setdata(){
        if self.searchTF.text == ""{
            self.viewMoreButton.isHidden = false
            self.categoryNameLbl.isHidden = false
            self.descriptionLbl.isHidden = false
            self.categoryNameLbl.text = productListModal?.data?.categoryName ?? ""
            let des = productListModal?.data?.description ?? ""
            if let attributedString = des.htmlToAttributedString_(fontName: "System", fontSize: 17) {
                descriptionLbl.attributedText = attributedString
                descriptionLbl.numberOfLines = 3
                descriptionLbl.lineBreakMode = .byTruncatingTail
                descriptionLbl.layoutIfNeeded()
                
                let fullLineCount = descriptionLbl.visibleLineCount
                viewMoreButton.isHidden = fullLineCount <= 3
            }
        }
        else{
            self.categoryNameLbl.text = ""
            self.descriptionLbl.text = ""
        }
    }
}
// MARK: ProductSearching API ==>
extension ProductVC {
    func serviceProductSearchingAPI(searchtxt: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let text = searchtxt.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productListing?categoryId=\(self.categoryId)&search=\(text)&deviceId=\(deviceId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            print(JSONResponse)
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productSearchingModal = try JSONDecoder().decode(ProductSearchingModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.productSearchingModal?.data?.count ?? 0) == 0 ? false : true
                    self.viewMoreButton.isHidden = true
                    self.categoryNameLbl.isHidden = true
                    self.descriptionLbl.isHidden = true
                    self.descriptionLbl.text = ""
                    self.categoryNameLbl.text = ""
                    
                    self.productCollection.reloadData()
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
// MARK: HomeSearchingProductAPI ==>
extension ProductVC {
    func serviceHomeSearchingProductListAPI(searchtxt: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        let text = searchtxt.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productListing?search=\(text)&deviceId=\(deviceId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            print(JSONResponse)
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.homeProductSearchModal = try JSONDecoder().decode(HomeProductSearchModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.homeProductSearchModal?.data?.count ?? 0) == 0 ? false : true
                    if self.homeProductSearchModal?.data?.count ?? 0 == 0{
                        self.viewMoreButton.isHidden = true
                        self.categoryNameLbl.isHidden = true
                        self.descriptionLbl.isHidden = true
                        self.descriptionLbl.text = ""
                        self.categoryNameLbl.text = ""
                    }
                   
                    self.productCollection.reloadData()
                   
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
// MARK: AddToWishList API ==>
extension ProductVC {
    func serviceAddToWishListApi(productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["productId": productId] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToWishlist", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                // Deslike
                if self.isHomeSearching == 1{
                    self.serviceHomeSearchingProductListAPI(searchtxt: self.searchtxt)
                }
                else{
                    self.serviceProductListAPI(categoryId: self.categoryId)
                }
            case 1:
                if self.isHomeSearching == 1{
                    self.serviceHomeSearchingProductListAPI(searchtxt: self.searchtxt)
                }
                else{
                    self.serviceProductListAPI(categoryId: self.categoryId) // Like
                }
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            // self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
// MARK: AddToCart ==>
extension ProductVC {
    func serviceAddToCartApi(productId: String, quantity: String){
        LoaderManager.shared.showLoader(in: self.view)
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["deviceId": deviceId, "productId": productId, "quantity": quantity] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToCart", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                print("itemAddInCart")
                self.updateCartBadge()
                if self.isHomeSearching == 1{
                    self.serviceHomeSearchingProductListAPI(searchtxt: self.searchtxt)
                }else{
                    self.serviceProductListAPI(categoryId: self.categoryId)
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
