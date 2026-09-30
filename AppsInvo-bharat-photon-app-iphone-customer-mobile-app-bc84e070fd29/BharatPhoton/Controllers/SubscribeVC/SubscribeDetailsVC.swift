//
//  SubscribeDetailsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 16/07/24.
//

import UIKit
 

class SubscribeDetailsVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var productListTable: UITableView!
    @IBOutlet weak var productListTableHeight: NSLayoutConstraint!
    @IBOutlet weak var planTypeLbl: UILabel!
    @IBOutlet weak var planPriceLbl: UILabel!
    @IBOutlet weak var taxPriceLbl: UILabel!
    @IBOutlet weak var totalPriceLbl: UILabel!
    @IBOutlet weak var qtyLbl: UILabel!
    @IBOutlet weak var quntityLbl: UILabel!
    var cartSubscriptionListModal: CartSubscriptionListModal?
    var subscriptionId = ""
    var productId = ""
    var _id = ""
    var planType = 0
    var type = 1
    var isRenewPlan = 0
    var selectedQuantity = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        print(isRenewPlan)
        print(selectedQuantity)
        updateCartBadge()
        updateNotificationBadge()
        serviceSubscriptionCartListAPI()
        productListTable.delegate = self
        productListTable.dataSource = self
        productListTable.register(UINib(nibName: "Cell", bundle: nil), forCellReuseIdentifier: "Cell")
        
        self.productListTable.rowHeight = UITableView.automaticDimension
        self.productListTable.separatorStyle = .none
        self.productListTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        // MARK: back swipe gesture ==>
        let swipeGestureRecognizer = UISwipeGestureRecognizer(target: self, action: #selector(handleSwipeGesture(_:)))
        swipeGestureRecognizer.direction = .right
        view.addGestureRecognizer(swipeGestureRecognizer)
        if planType == 0{
            planTypeLbl.text! = "Plan (Lite)"
        }
        else if planType == 1{
            planTypeLbl.text! = "Plan (Plus)"
        }
        else{
            planTypeLbl.text! = "Plan (Premium)"
        }
    }
    // MARK: back swipe gesture func ==>
    @objc func handleSwipeGesture(_ gestureRecognizer: UISwipeGestureRecognizer) {
        if gestureRecognizer.direction == .right {
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
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    @IBAction func continueBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "SubscribePaymentVC") as! SubscribePaymentVC
        vc.subscriptionCartId = self._id
        vc.type = self.type
        vc.isRenewPlan = self.isRenewPlan
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension SubscribeDetailsVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cartSubscriptionListModal?.data?.mode?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = productListTable.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! Cell
        cell.plusBtn.tag = indexPath.row
        cell.plusBtn.addTarget(self, action: #selector(plusBtnTapped(sender:)), for: .touchUpInside)
        cell.minusBtn.tag = indexPath.row
        cell.minusBtn.addTarget(self, action: #selector(minusBtnTapped(sender:)), for: .touchUpInside)
        cell.productNameLbl.text = cartSubscriptionListModal?.data?.productName ?? ""
        let totalPrice = cartSubscriptionListModal?.data?.totalPrice ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: totalPrice), number: .decimal))"
   //     cell.ratingLbl.text = "\(cartSubscriptionListModal?.data?.rating ?? 0)"
        cell.ratingValue.rating = Double(cartSubscriptionListModal?.data?.rating ?? 0)
        cell.ratingValue.isUserInteractionEnabled = false
        cell.quantityLbl.text = "\(cartSubscriptionListModal?.data?.quantity ?? 0)"
        let description_ = cartSubscriptionListModal?.data?.description ?? ""
        cell.descriptionLbl.attributedText = description_.htmlToAttributedString_(fontName: "System", fontSize: 17)
        let img = cartSubscriptionListModal?.data?.image?.first?.url ?? ""
        cell.productImg.sd_setImage(with: URL(string: img), placeholderImage: UIImage(named: "placeHolder"))
        if isRenewPlan == 1{
            cell.plusView.isHidden = true
            cell.minusView.isHidden = true
        }
        else{
            cell.plusView.isHidden = false
            cell.minusView.isHidden = false
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 170
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }
    // MARK: PlusBtnTapped ==>
    @objc func plusBtnTapped(sender: UIButton) {
        let id = cartSubscriptionListModal?.data?._id ?? ""
        self.serviceIncreaseQuantityAPI(id: id)
    }
    // MARK: MinusBtnTapped ==>
    @objc func minusBtnTapped(sender: UIButton) {
        let id = cartSubscriptionListModal?.data?._id ?? ""
        if cartSubscriptionListModal?.data?.quantity ?? 0 == 1{
        }
        else{
            self.serviceDecreaseQuantityAPI(id: id)
        }
       
    }
}
extension SubscribeDetailsVC {
    func serviceSubscriptionCartListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "subscriptionCartList?id=\(_id)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.cartSubscriptionListModal = try JSONDecoder().decode(CartSubscriptionListModal.self, from: JSONResponse.rawData())
                    //self.subTotalLbl.text = "\(self.cartSubscriptionListModal?.data?.first?.finalTotalPrice ?? 0)"
                    self.productListTable.reloadData()
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
        let taxAmount = cartSubscriptionListModal?.data?.taxAmount ?? 0
        self.taxPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: taxAmount), number: .decimal))"
        let price = cartSubscriptionListModal?.data?.price ?? 0
        self.planPriceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        self.qtyLbl.text = "Qty (\(cartSubscriptionListModal?.data?.quantity ?? 0))"
        self.quntityLbl.text = "\(cartSubscriptionListModal?.data?.quantity ?? 0)"
        self.totalPriceLbl.text = "₹ \(cartSubscriptionListModal?.data?.finalTotalAmount ?? 0)"
    }
}
extension SubscribeDetailsVC {
    func serviceIncreaseQuantityAPI(id: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["id": id, "quantity": 1] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "updateQuantity", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.serviceSubscriptionCartListAPI()
            case 1:
                self.serviceSubscriptionCartListAPI()
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
extension SubscribeDetailsVC {
    func serviceDecreaseQuantityAPI(id: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["id": id, "quantity":-1] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "updateQuantity", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                // self.alert(message: JSONResponse["message"].stringValue)
                self.serviceSubscriptionCartListAPI()
            case 1:
                self.serviceSubscriptionCartListAPI()
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

