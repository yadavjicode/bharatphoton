//
//  SubscribeVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 16/07/24.
//

import UIKit

class SubscribeVC: UIViewController {
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var productDescriptionLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var subscriptionCollection: UICollectionView!
    var productSubscriptionModal: ProductSubscriptionModal?
    var productId = ""
    var pincode = 0
    var planType = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        updateCartBadge()
        updateNotificationBadge()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        serviceSubscriptionListingAPI()
        subscriptionCollection.delegate = self
        subscriptionCollection.dataSource = self
        subscriptionCollection.register(UINib(nibName: "RenewPlanCell", bundle: nil), forCellWithReuseIdentifier: "RenewPlanCell")
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
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
       }
    func updateCartBadge(){
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    @IBAction func notificationBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "NotificationsVC") as! NotificationsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
}
extension SubscribeVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return productSubscriptionModal?.data?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = subscriptionCollection.dequeueReusableCell(withReuseIdentifier: "RenewPlanCell", for: indexPath) as! RenewPlanCell
        let price = productSubscriptionModal?.data?[indexPath.row].price ?? 0
        cell.priceLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        let description_ = productSubscriptionModal?.data?[indexPath.row].productDescription ?? ""
        cell.descriptionLbl.attributedText = description_.htmlToAttributedString_(fontName: "System", fontSize: 17)
        cell.purchaseNowBtn.tag = indexPath.item
        cell.purchaseNowBtn.addTarget(self, action: #selector(purchaseNowBtnTapped(sender:)), for: .touchUpInside)
        let mode = productSubscriptionModal?.data?[indexPath.row].mode ?? ""
        if mode == "M"{
            cell.discountPercentageLbl.text = "Monthly Save"
        }else if mode == "Q"{
            cell.discountPercentageLbl.text = "Quarterly Save"
        }else if mode == "H"{
            cell.discountPercentageLbl.text = "Half Yearly Save"
        }else if mode == "Y"{
            cell.discountPercentageLbl.text = "Yearly Save"
        }
        if productSubscriptionModal?.data?[indexPath.row].planType ?? 0 == 0{
            cell.planTypeLbl.text = "LITE"
            cell.bgImg.image = UIImage(named: "plan bg1")
        }
        else if productSubscriptionModal?.data?[indexPath.row].planType ?? 0 == 1{
            cell.planTypeLbl.text = "PLUS"
            cell.bgImg.image = UIImage(named: "plan bg2")
        }
        else if productSubscriptionModal?.data?[indexPath.row].planType ?? 0 == 2{
            cell.planTypeLbl.text = "PREMIUM"
            cell.bgImg.image = UIImage(named: "plan bg3")
          
        }
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (self.subscriptionCollection.bounds.width/1.8), height: 350)
    }
    @objc func purchaseNowBtnTapped(sender: UIButton) {
        if UserDefaults.standard.integer(forKey: "guestUser") == 1{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
        let row = sender.tag
        let subscriptionId = productSubscriptionModal?.data?[row]._id ?? ""
        let productId = self.productId
        self.planType = productSubscriptionModal?.data?[row].planType ?? 0
        self.serviceAddProductSubsciptionAPI(subscriptionId: subscriptionId, productId: productId)
        }
    }
}
// MARK: AddProductSubsciption API ==>
extension SubscribeVC{
    func serviceAddProductSubsciptionAPI(subscriptionId: String, productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms: [String: Any] = ["subscriptionId": subscriptionId,"productId": productId, "quantity": 1]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addProductSubsciption", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let _id = dic["_id"]?.stringValue ?? ""
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "SubscribeDetailsVC") as! SubscribeDetailsVC
                vc._id = _id
                vc.planType = self.planType
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
// MARK: SubscriptionListing API ==>
extension SubscribeVC {
    func serviceSubscriptionListingAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "subscriptionListing?productId=\(self.productId)&pincode=\(self.pincode)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            print(JSONResponse)
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productSubscriptionModal = try JSONDecoder().decode(ProductSubscriptionModal.self, from: JSONResponse.rawData())
                    self.subscriptionCollection.reloadData()
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
        let img = self.productSubscriptionModal?.data?.first?.productImage?.first?.url ?? ""
        self.productImg.sd_setImage(with: URL(string: img), placeholderImage: UIImage(named: "placeHolder"))
        self.productNameLbl.text = self.productSubscriptionModal?.data?.first?.categoryName ?? ""
        self.productDescriptionLbl.text = self.productSubscriptionModal?.data?.first?.productName ?? ""
        self.modalNameLbl.text = "Model Name:\(productSubscriptionModal?.data?.first?.modelNo ?? "")"
//        let des = self.productSubscriptionModal?.data?.first?.productDescription ?? ""
//        self.productDescriptionLbl.attributedText = des.description.htmlToAttributedString_(fontName: "System", fontSize: 17)!
      //self.modalNameLbl.text = "Model Name:\(productSubscriptionModal?.data?.first?.modalNo ?? ""
    }
}

