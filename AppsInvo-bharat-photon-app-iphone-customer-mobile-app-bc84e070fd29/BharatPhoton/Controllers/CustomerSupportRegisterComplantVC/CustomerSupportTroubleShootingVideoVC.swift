//
//  CustomerSupportTroubleShootingVideoVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 15/07/24.
//

import UIKit
import youtube_ios_player_helper
import DropDown

class CustomerSupportTroubleShootingVideoVC: UIViewController, YTPlayerViewDelegate {
    @IBOutlet weak var horizontalScrollView: UIScrollView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var ytVideoListTable: UITableView!
    @IBOutlet weak var ytVideoListTableHeight: NSLayoutConstraint!
    @IBOutlet weak var userManualLbl: UILabel!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var categoryTF: UITextField!
    @IBOutlet weak var productTF: UITextField!
    @IBOutlet weak var userManualBtn: UIButton!
    @IBOutlet weak var troubleShootBtn: UIButton!
    @IBOutlet weak var installationVideoBtn: UIButton!
    var categoryListModal: CategoryModal?
    var productListingModal: ProductListingModal?
    var troubleShootVideoModal: TroubleShootVideoModal?
    var dropDown = DropDown()
    var categoryId = ""
    var productId = ""
    var type = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        let offset = CGPoint(x: 300, y: 0)
        horizontalScrollView.setContentOffset(offset, animated: true)
        updateCartBadge()
        updateNotificationBadge()
        serviceCategoryListAPI()
        self.ytVideoListTable.delegate = self
        self.ytVideoListTable.dataSource = self
        self.ytVideoListTable.register(UINib(nibName: "TroubleShootingVideoCell", bundle: nil), forCellReuseIdentifier: "TroubleShootingVideoCell")
        self.ytVideoListTable.register(UINib(nibName: "UserManualCell", bundle: nil), forCellReuseIdentifier: "UserManualCell")
        self.ytVideoListTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        self.ytVideoListTable.separatorStyle = .none
        self.type = 1
        self.userManualBtn.borderLineWidth = 1
        self.userManualBtn.borderColor = UIColor(hexString: "FF8D3A")
        self.userManualBtn.setTitleColor(UIColor(hexString: "FF8D3A"), for: .normal)
        self.troubleShootBtn.borderLineWidth = 1
        self.troubleShootBtn.borderColor = .black
        self.troubleShootBtn.setTitleColor( .black, for: .normal)
        self.installationVideoBtn.borderLineWidth = 1
        self.installationVideoBtn.borderColor = .black
        self.installationVideoBtn.setTitleColor( .black, for: .normal)
    }
    // MARK: Dynamic table ROW Height Delegate
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if let obj = object as? UITableView {
            if obj == self.ytVideoListTable && keyPath == "contentSize" {
                ytVideoListTableHeight.constant = ytVideoListTable.contentSize.height
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
        self.navigationController?.popToRootViewController(animated: true)
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
    @IBAction func complantHistoryBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportComplaintHistoryVC") as! CustomerSupportComplaintHistoryVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func registerComplantBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportRegisterComplantVC") as! CustomerSupportRegisterComplantVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func customerCareBtnTappedAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportCustomerCareVC") as! CustomerSupportCustomerCareVC
        self.navigationController?.pushViewController(vc, animated: false)
    }
    @IBAction func categoryAction(_ sender: UIButton) {
        guard let categories = categoryListModal?.data else { return }
        dropDown.dataSource = categories.map { $0.categoryName ?? "" }
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            let selectedCategory = categories[index]
            self?.categoryTF.text = selectedCategory.categoryName
            self?.categoryId = selectedCategory._id ?? ""
            self?.serviceProductListAPI(categoryId: selectedCategory._id ?? "")
        }
    }
    @IBAction func productAction(_ sender: UIButton) {
        guard let products = productListingModal?.data else { return }
        let productNames = products.map { $0.productName ?? "" }
        dropDown.dataSource = productNames
        dropDown.anchorView = sender
        dropDown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        dropDown.show()
        dropDown.selectionAction = { [weak self] (index: Int, item: String) in
            // Find product by name to safely retrieve the full object
            let selectedProduct = products[index]
            self?.productTF.text = selectedProduct.productName ?? ""
            self?.productId = selectedProduct._id ?? ""
            self?.serviceGetTroubleVideoAPI(productId: selectedProduct._id ?? "")
        }
    }
    @IBAction func userManualBtnAction(_ sender: UIButton) {
        if  categoryTF.text!.isEmpty{
            self.alert(message: "Please select category")
        }
        else if productTF.text!.isEmpty{
            self.alert(message: "Please select product")
        }
        else{
            self.type = 1
            self.userManualBtn.borderLineWidth = 1
            self.userManualBtn.borderColor = UIColor(hexString: "FF8D3A")
            self.userManualBtn.setTitleColor(UIColor(hexString: "FF8D3A"), for: .normal)
            self.troubleShootBtn.borderLineWidth = 1
            self.troubleShootBtn.borderColor = .black
            self.troubleShootBtn.setTitleColor( .black, for: .normal)
            self.installationVideoBtn.borderLineWidth = 1
            self.installationVideoBtn.borderColor = .black
            self.installationVideoBtn.setTitleColor( .black, for: .normal)
            self.serviceGetTroubleVideoAPI(productId: self.productId)
            //self.userManualLbl.isHidden = true
            let userManual = self.troubleShootVideoModal?.data?.userManual ?? ""
            if userManual == ""{
                alert(message: "User manual not found")
            }
            else{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "UserManualVC") as! UserManualVC
                vc.pdfUrl = self.troubleShootVideoModal?.data?.userManual ?? ""
                self.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
    @IBAction func troubleShootBtnAction(_ sender: UIButton) {
        if  categoryTF.text!.isEmpty{
            self.alert(message: "Please select category")
        }
        else if productTF.text!.isEmpty{
            self.alert(message: "Please select product")
        }
        else{
            self.type = 2
            self.troubleShootBtn.borderLineWidth = 1
            self.troubleShootBtn.borderColor = UIColor(hexString: "FF8D3A")
            self.troubleShootBtn.setTitleColor(UIColor(hexString: "FF8D3A"), for: .normal)
            self.userManualBtn.borderLineWidth = 1
            self.userManualBtn.borderColor = .black
            self.userManualBtn.setTitleColor( .black, for: .normal)
            self.installationVideoBtn.borderLineWidth = 1
            self.installationVideoBtn.borderColor = .black
            self.installationVideoBtn.setTitleColor( .black, for: .normal)
            self.serviceGetTroubleVideoAPI(productId: self.productId)
            self.userManualLbl.isHidden = true
        }
    }
    @IBAction func installationVideoBtnAction(_ sender: UIButton) {
        if  categoryTF.text!.isEmpty{
            self.alert(message: "Please select category")
        }
        else if productTF.text!.isEmpty{
            self.alert(message: "Please select product")
        }
        else{
            self.type = 3
            self.installationVideoBtn.borderLineWidth = 1
            self.installationVideoBtn.borderColor = UIColor(hexString: "FF8D3A")
            self.installationVideoBtn.setTitleColor(UIColor(hexString: "FF8D3A"), for: .normal)
            self.userManualBtn.borderLineWidth = 1
            self.userManualBtn.borderColor = .black
            self.userManualBtn.setTitleColor( .black, for: .normal)
            self.troubleShootBtn.borderLineWidth = 1
            self.troubleShootBtn.borderColor = .black
            self.troubleShootBtn.setTitleColor( .black, for: .normal)
            self.serviceGetTroubleVideoAPI(productId: self.productId)
            self.userManualLbl.isHidden = true
        }
    }
}
extension CustomerSupportTroubleShootingVideoVC: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if type == 2{
            return troubleShootVideoModal?.data?.troubleVideos?.count ?? 0
        }
        else if self.type == 3{
            return troubleShootVideoModal?.data?.installationVideo?.count ?? 0
        }
        else{
            return 0
        }
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = ytVideoListTable.dequeueReusableCell(withIdentifier: "TroubleShootingVideoCell", for: indexPath) as! TroubleShootingVideoCell
        if type == 2{
            let videoUrl = URL(string: troubleShootVideoModal?.data?.troubleVideos?[indexPath.row].url ?? "")
            let videoId = videoUrl?.queryParameters["v"]
            cell.ytPlayerView.load(withVideoId: videoId ?? "", playerVars: ["playsinline": 1])
            cell.ytPlayerView.delegate = self
        }
        else if type == 3{
            let videoUrl = URL(string: troubleShootVideoModal?.data?.installationVideo?[indexPath.row].url ?? "")
            let videoId = videoUrl?.queryParameters["v"]
            cell.ytPlayerView.load(withVideoId: videoId ?? "", playerVars: ["playsinline": 1])
            cell.ytPlayerView.delegate = self
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 250
    }
}
extension CustomerSupportTroubleShootingVideoVC {
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
extension CustomerSupportTroubleShootingVideoVC {
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
                    self.categoryListModal = try JSONDecoder().decode(CategoryModal.self, from: JSONResponse.rawData())
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
extension CustomerSupportTroubleShootingVideoVC {
    func serviceProductListAPI(categoryId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "productByCategoryId?categoryId=\(categoryId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.productListingModal = try JSONDecoder().decode(ProductListingModal.self, from: JSONResponse.rawData())
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
extension CustomerSupportTroubleShootingVideoVC {
    func serviceGetTroubleVideoAPI(productId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "getTroubleVideo?categoryId=\(self.categoryId)&productId=\(productId)", params: parms, token: "") { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.troubleShootVideoModal = try JSONDecoder().decode(TroubleShootVideoModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.troubleShootVideoModal?.data?.troubleVideos?.count ?? 0) == 0 ? false : true
                    self.msgLbl.isHidden = (self.troubleShootVideoModal?.data?.installationVideo?.count ?? 0) == 0 ? false : true
                    self.msgLbl.isHidden = (self.troubleShootVideoModal?.data?.userManual?.count ?? 0) == 0 ? false : true
                    self.userManualLbl.text = self.troubleShootVideoModal?.data?.userManual ?? ""
                    self.ytVideoListTable.reloadData()
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

