//
//  CartVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/06/24.
//

import UIKit

protocol CartCountDelegate: AnyObject {
    func didUpdateCartCountApi()
}
class CartVC: UIViewController, APICartDetailsRequestDelegate{
    func didUpdateCartDetailFetchData(couponCode: String) {
        serviceCartDetailsAPI()
        couponCodeLbl.text = couponCode
    }
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var couponCodeLbl: UILabel!
    @IBOutlet weak var couponAndOfferView: UIView!
    @IBOutlet weak var alertView: UIView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var totalPriceLbl: UILabel!
    @IBOutlet weak var discountView: UIView!
    @IBOutlet weak var discountPriceLbl: UILabel!
    @IBOutlet weak var taxLbl: UILabel!
    @IBOutlet weak var finalPriceLbl: UILabel!
    @IBOutlet weak var productListTable: UITableView!
    @IBOutlet weak var productListTableHeight: NSLayoutConstraint!
    var cartDetailsModal: CartDetailsModal?
    var isExpandedArray: [Bool] = []
    var delegate: CartCountDelegate?
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        scrollView.isHidden = true
        alertView.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        serviceCartDetailsAPI()
        productListTable.delegate = self
        productListTable.dataSource = self
        productListTable.register(UINib(nibName: "CartProductCell", bundle: nil), forCellReuseIdentifier: "CartProductCell")
        productListTable.separatorStyle = .none
        productListTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        
        if UserDefaults.standard.integer(forKey: "guestUser") == 1{
            couponAndOfferView.isHidden = false
        }
        else{
            couponAndOfferView.isHidden = true
        }
    }
    // MARK: back swipe gesture func ==>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        // Handle the swipe gesture here
        if gestureRecognizer.direction == .right {
            // Perform the back action (e.g., pop to the previous view controller)
            navigationController?.popViewController(animated: true)
        }
    }
    // MARK: Dynamic table ROW Height Delegate
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.productListTable && keyPath == "contentSize" {
                productListTableHeight.constant = productListTable.contentSize.height
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
    @IBAction func backBtnAction(_ sender: UIButton) {
        delegate?.didUpdateCartCountApi()
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    @IBAction func viewCouponAndOfferAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CouponsVC") as! CouponsVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.cartId = cartDetailsModal?.data?.cart?._id ?? ""
        vc.delegate = self
        self.present(vc, animated: true)
    }
    @IBAction func checkOutBtnAction(_ sender: UIButton) {
        if UserDefaults.standard.integer(forKey: "guestUser") == 1{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "CheckOutVC") as! CheckOutVC
            vc.cartId = cartDetailsModal?.data?.cart?._id ?? ""
            vc.addressId = cartDetailsModal?.data?.address?._id ?? ""
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func exploreProductBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(identifier: "TabVC") as? TabVC
        self.navigationController?.pushViewController(vc!, animated: false)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            vc?.selectedIndex = 0
        }
    }
}
extension CartVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cartDetailsModal?.data?.cart?.products?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = productListTable.dequeueReusableCell(withIdentifier: "CartProductCell", for: indexPath) as! CartProductCell
        cell.quantityLbl.text = "\(cartDetailsModal?.data?.cart?.products?[indexPath.row].quantity ?? 0)"
        let price = cartDetailsModal?.data?.cart?.products?[indexPath.row].price ?? 0
        let discontedPrice = cartDetailsModal?.data?.cart?.products?[indexPath.row].priceToDisplay ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        cell.discountedPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: discontedPrice), number: .decimal))"
        cell.productNameLbl.text = cartDetailsModal?.data?.cart?.products?[indexPath.row].productName ?? ""
        let description_ = cartDetailsModal?.data?.cart?.products?[indexPath.row].description ?? ""
        cell.descriptionLbl.attributedText = description_.htmlToAttributedString_(fontName: "System", fontSize: 17)
        let image = self.cartDetailsModal?.data?.cart?.products?[indexPath.row].image?.first?.url ?? ""
        cell.productImg.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        cell.plusBtn.tag = indexPath.item
        cell.plusBtn.addTarget(self, action: #selector(plusBtnTapped(sender:)), for: .touchUpInside)
        cell.minusBtn.tag = indexPath.item
        cell.minusBtn.addTarget(self, action: #selector(minusBtnTapped(sender:)), for: .touchUpInside)
        cell.deleteBtn.tag = indexPath.item
        cell.deleteBtn.addTarget(self, action: #selector(deleteBtnTapped(sender:)), for: .touchUpInside)
        cell.configureCell(isExpanded: isExpandedArray[indexPath.row])
        cell.viewMoreBtn.tag = indexPath.row
        cell.viewMoreBtn.addTarget(self, action: #selector(viewMoreTapped(sender:)), for: .touchUpInside)
        let rating = cartDetailsModal?.data?.cart?.products?[indexPath.row].rating ?? 0
        if rating == 0{
            cell.ratingView.isHidden = true
        }else{
            cell.ratingView.isHidden = false
            cell.ratingLbl.text = "\(rating)"
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return tableView.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    @objc func viewMoreTapped(sender: UIButton) {
        let row = sender.tag
        isExpandedArray[row] = !isExpandedArray[row]  // Toggle the expanded state
        productListTable.reloadRows(at: [IndexPath(row: row, section: 0)], with: .automatic)  // Reload the row with animation
    }
    // MARK: PlusBtnTapped ==>
    @objc func plusBtnTapped(sender: UIButton) {
        let indexPath = IndexPath(row: sender.tag, section: 0)
        guard let products = cartDetailsModal?.data?.cart?.products else { return }
        var currentProduct = products[indexPath.row]
        let currentQuantity = currentProduct.quantity ?? 0
        let totalQuantity = currentProduct.productquantity ?? 0 // Maximum available quantity for this product
        let isQuantityUnlimited = currentProduct.isQuantityUnlimited ?? 0
        if isQuantityUnlimited == 1 {
               print("Product has unlimited quantity. Hit Add to Cart API directly.")
               let id = currentProduct.productId ?? ""
               self.serviceAddToCartApi(productId: id, quantity: "1")
               return // Exit after hitting the API, no further checks are needed
           }
           if currentQuantity >= totalQuantity {
               alert(message: "Cannot add more. Maximum quantity for this product is \(totalQuantity).")
               return // Exit the function if quantity has reached the limit
           }
           currentProduct.quantity = currentQuantity + 1
           cartDetailsModal?.data?.cart?.products?[indexPath.row].quantity = currentProduct.quantity
           if let cell = productListTable.cellForRow(at: indexPath) as? CartProductCell {
               cell.quantityLbl.text = "\(currentProduct.quantity ?? 0)"
           }
           print("Hit add Cart API with quantity: \(currentProduct.quantity ?? 0)")
           let id = currentProduct.productId ?? ""
           self.serviceAddToCartApi(productId: id, quantity: "1")
       }
    // MARK: MinusBtnTapped ==>
    @objc func minusBtnTapped(sender: UIButton) {
        let indexPath = IndexPath(row: sender.tag, section: 0)
        guard let products = cartDetailsModal?.data?.cart?.products else { return }
        let currentQuantity = products[indexPath.row].quantity ?? 0
        // Ensure quantity does not go below 1
        if currentQuantity > 1 {
            cartDetailsModal?.data?.cart?.products?[indexPath.row].quantity = currentQuantity - 1
            let cell = productListTable.cellForRow(at: indexPath) as! CartProductCell
            cell.quantityLbl.text = "\(cartDetailsModal?.data?.cart?.products?[indexPath.row].quantity ?? 0)"
            print("Hit add Cart API with quantity: \(cartDetailsModal?.data?.cart?.products?[indexPath.row].quantity ?? 0)")
            let id = cartDetailsModal?.data?.cart?.products?[indexPath.row].productId ?? ""
            self.serviceAddToCartApi(productId: id, quantity: "-1")
        } else {
            print("Minimum quantity reached, cannot decrease further.")
        }
    }
    @objc func deleteBtnTapped(sender: UIButton) {
        let row = sender.tag
        let cartId = cartDetailsModal?.data?.cart?._id ?? ""
        let id = cartDetailsModal?.data?.cart?.products?[row]._id ?? ""
        self.serviceDeleteCartAPI(cartId: cartId, id: id)
    }
}
// MARK: AddToCart ==>
extension CartVC {
    func serviceAddToCartApi(productId: String, quantity: String){
        self.startLoading()
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let params = ["deviceId": deviceId, "productId": productId, "quantity": quantity] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addToCart", params: params, token: token) { JSONResponse in
            self.stopLoading()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print("itemAddInCart")
            case 1:
                print("itemAddInCart")
                self.updateCartBadge()
                self.serviceCartDetailsAPI()
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.stopLoading()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
// MARK: CartDetailsAPI ==>
extension CartVC {
    func serviceCartDetailsAPI(){
        self.startLoading()
        let deviceId = UserDefaults.standard.string(forKey: "DeviceId") ?? ""
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "cartDetails?deviceId=\(deviceId)", params: parms, token: token) { JSONResponse in
            self.scrollView.isHidden = false
            self.stopLoading()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.cartDetailsModal = try JSONDecoder().decode(CartDetailsModal.self, from: JSONResponse.rawData())
                    self.alertView.isHidden = (self.cartDetailsModal?.data?.cart?.products?.count ?? 0) == 0 ? false : true
                    if let productsCount = self.cartDetailsModal?.data?.cart?.products?.count {
                        self.isExpandedArray = Array(repeating: false, count: productsCount)
                    }
                    self.productListTable.reloadData()
                    self.updateCartBadge()
                    self.setdata()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.scrollView.isHidden = false
            self.stopLoading()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
    func setdata(){
        let discountAmount = self.cartDetailsModal?.data?.cart?.discountAmount ?? 0
        if discountAmount == 0{
            self.discountView.isHidden = true
        }
        else{
            self.discountView.isHidden = false
            self.discountPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: discountAmount), number: .decimal))"
        }
        let totalAmount = self.cartDetailsModal?.data?.cart?.totalAmount ?? 0
        self.totalPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: totalAmount), number: .decimal))"
        let tax = self.cartDetailsModal?.data?.cart?.taxAmount ?? 0
        self.taxLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: tax), number: .decimal))"
        let finalTotalAmount = self.cartDetailsModal?.data?.cart?.finalTotalAmount ?? 0.0
        self.finalPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: finalTotalAmount), number: .decimal))"
    }
}
extension CartVC {
    func serviceDeleteCartAPI(cartId: String, id: String) {
        let baseUrl = ServiceEndPoint.BhartPhotonUrl.baseURL
        let parameters = [
            "cartId": cartId,
            "id": id
        ]
        guard let postData = try? JSONSerialization.data(withJSONObject: parameters, options: []) else {
            print("Error serializing JSON")
            return
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        var request = URLRequest(url: URL(string: "\(baseUrl)removeFromCart")!, timeoutInterval: Double.infinity)
        request.addValue(token, forHTTPHeaderField: "authtoken")
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpMethod = "DELETE"
        request.httpBody = postData
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
                return
            }
            guard let data = data else {
                print("No data returned")
                return
            }
            do {
                if let jsonResponse = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                    // Handle the JSON response
                    print("Response JSON: \(jsonResponse)")
                    DispatchQueue.main.async {
                        if let code = jsonResponse["code"] as? Int {
                            switch code {
                            case 0:
                                print("Error: \(jsonResponse["message"] as? String ?? "Unknown error")")
                            case 1:
                                print("Success! Cart item removed.")
                                self.serviceCartDetailsAPI()
                            default:
                                print("Unidentified code: \(code)")
                            }
                        } else {
                            print("No 'code' key found or it's not an Int")
                        }
                    }
                }
            } catch {
                print("Error parsing response: \(error.localizedDescription)")
            }
        }
        task.resume()
    }
}
