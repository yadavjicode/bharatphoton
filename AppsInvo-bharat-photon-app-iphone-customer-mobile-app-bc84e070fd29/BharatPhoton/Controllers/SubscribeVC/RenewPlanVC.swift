//
//  RenewPlanVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 21/06/24.
//

import UIKit
 

class RenewPlanVC: UIViewController {
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var planTypeLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var daysRemainingLbl: UILabel!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var productCatLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var planModeLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var subscriptionCollection: UICollectionView!
    var renewPlanModal: RenewPlanModal?
    var _id = ""
    var sin = ""
    var planType = 0
    var productId = ""
    var isRenewPlan = 0
    var selectedQuantity = 0
    var sinNoArray: [String] = []
    var finNoArray: [String] = []
    override func viewDidLoad() {
        super.viewDidLoad()
        print("SIN NO \(self.sinNoArray)")
        print("QTY \(self.selectedQuantity)")
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
       
        updateNotificationBadge()
        serviceRenewPlanAPI()
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
    @IBAction func productClickAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "SubscribeDetailsVC") as! SubscribeDetailsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension RenewPlanVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return renewPlanModal?.data?.avilablePlan?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = subscriptionCollection.dequeueReusableCell(withReuseIdentifier: "RenewPlanCell", for: indexPath) as! RenewPlanCell
        cell.priceLbl.text = "\(renewPlanModal?.data?.avilablePlan?[indexPath.row].price ?? 0)"
        let description_ = renewPlanModal?.data?.avilablePlan?[indexPath.row].description ?? ""
        cell.descriptionLbl.attributedText = description_.htmlToAttributedString_(fontName: "System", fontSize: 17)
        cell.purchaseNowBtn.setTitle("Upgrade", for: .normal)
        cell.purchaseNowBtn.tag = indexPath.item
        cell.purchaseNowBtn.addTarget(self, action: #selector(purchaseNowBtnTapped(sender:)), for: .touchUpInside)
        let mode = renewPlanModal?.data?.avilablePlan?[indexPath.row].mode ?? ""
        if mode == "M"{
            cell.discountPercentageLbl.text = "Monthly Save"
        }else if mode == "Q"{
            cell.discountPercentageLbl.text = "Quarterly Save"
        }else if mode == "H"{
            cell.discountPercentageLbl.text = "Half Yearly Save"
        }else if mode == "Y"{
            cell.discountPercentageLbl.text = "Yearly Save"
        }
        if renewPlanModal?.data?.avilablePlan?[indexPath.row].planType ?? 0 == 0{
            cell.planTypeLbl.text = "LITE"
            cell.bgImg.image = UIImage(named: "plan bg1")
        }
        else if renewPlanModal?.data?.avilablePlan?[indexPath.row].planType ?? 0 == 1{
            cell.planTypeLbl.text = "PLUS"
            cell.bgImg.image = UIImage(named: "plan bg2")
        }
        else if renewPlanModal?.data?.avilablePlan?[indexPath.row].planType ?? 0 == 2{
            cell.planTypeLbl.text = "PREMIUM"
            cell.bgImg.image = UIImage(named: "plan bg3")
        }
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (self.subscriptionCollection.bounds.width/1.8), height: 350)
    }
    @objc func purchaseNowBtnTapped(sender: UIButton) {
        let row = sender.tag
        let subscriptionId = renewPlanModal?.data?.avilablePlan?[row]._id ?? ""
        let productId = self.productId
       // let finNo = renewPlanModal?.data?.installationDetails?[row].finNo ?? ""
        self.planType = renewPlanModal?.data?.avilablePlan?[row].planType ?? 0
        if let installationDetails = renewPlanModal?.data?.installationDetails {
                finNoArray.removeAll() // Clear old values before appending new ones
                for detail in installationDetails.prefix(2) {
                    finNoArray.append(detail.finNo ?? "")
                    print(finNoArray)
                }
          }
        self.serviceAddProductSubsciptionAPI(subscriptionId: subscriptionId, productId: productId)
    }
}
// MARK: RenewPlan API ==>
extension RenewPlanVC {
    func serviceRenewPlanAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let sin = sinNoArray.joined(separator: ",")
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "renewPlan?id=\(self._id)&sin=\(sin)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.renewPlanModal = try JSONDecoder().decode(RenewPlanModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.renewPlanModal?.data?.avilablePlan?.count ?? 0) == 0 ? false : true
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
        let planType = renewPlanModal?.data?.currentPlan?.planType ?? 0
        if planType == 0{
            self.planTypeLbl.text = "LITE"
        }
        else if planType == 1{
            self.planTypeLbl.text = "PLUS"
        }
        else if planType == 2{
            self.planTypeLbl.text = "PREMIUM"
        }
        let mode = renewPlanModal?.data?.currentPlan?.mode ?? ""
        if mode == "M"{
            self.planModeLbl.text = "Monthly Save"
        }else if mode == "Q"{
            self.planModeLbl.text = "Quarterly Save"
        }else if mode == "H"{
            self.planModeLbl.text = "Half Yearly Save"
        }else if mode == "Y"{
            self.planModeLbl.text = "Yearly Save"
        }
        
        self.priceLbl.text = "₹\(renewPlanModal?.data?.currentPlan?.price ?? 0)"
        let daysRemaining = renewPlanModal?.data?.currentPlan?.daysRemaining ?? 0
        if daysRemaining <= 0{
            self.daysRemainingLbl.text = "Your plan has expired"
            self.daysRemainingLbl.textColor = .red
        }
        else{
            self.daysRemainingLbl.textColor = .black
            self.daysRemainingLbl.text = "Your plan will expire in \(renewPlanModal?.data?.currentPlan?.daysRemaining ?? 0)/Days"
        }
        self.productCatLbl.text = renewPlanModal?.data?.currentPlan?.categoryName ?? ""
        self.productNameLbl.text = renewPlanModal?.data?.currentPlan?.productName ?? ""
        self.modalNameLbl.text = "Model Name:\(renewPlanModal?.data?.currentPlan?.modelNo ?? "")"
        self.productImg.sd_setImage(with: URL(string: renewPlanModal?.data?.currentPlan?.productImage ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
    }
}
// MARK: AddProductSubsciption API ==>
extension RenewPlanVC{
    func serviceAddProductSubsciptionAPI(subscriptionId: String, productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms: [String: Any] = ["finNo": self.finNoArray, "subscriptionId": subscriptionId,"productId": productId, "quantity": self.selectedQuantity]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addProductSubsciption", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
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
                vc.type = 2
                vc.isRenewPlan = self.isRenewPlan
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
