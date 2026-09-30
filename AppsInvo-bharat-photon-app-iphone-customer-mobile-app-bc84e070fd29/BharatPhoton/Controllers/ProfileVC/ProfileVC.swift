//
//  ProfileVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 18/06/24.
//

import UIKit
import SDWebImage
 
class ProfileVC: UIViewController, UIScrollViewDelegate, APIRequestDelegate, LogOutPopupDelegate, DeleteAccountPopupDelegate, FetchGetProfileDelegate  {
    func didConfirmDeleteAccount() {
        let loginVC = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
        navigationController?.setViewControllers([loginVC], animated: false)
    }
    func didUpdateProfileFetchData() {
        serviceUserProfileAPI()
    }
    func didConfirmLogout() {
        let loginVC = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
        navigationController?.setViewControllers([loginVC], animated: false)
    }
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var changePasswordView: UIView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var nameLbl: UILabel!
    @IBOutlet weak var mobileNoLbl: UILabel!
    @IBOutlet weak var profileImg: UIImageView!
    var refreshControl = UIRefreshControl()
    var keyType = 1
    var userType = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceUserProfileAPI()
        setupRefreshControl()
        
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        updateCartBadge()
    }
    @IBAction func editBtnAction(_ sender: UIButton) { // EditProfileForIndividualVC
        if self.userType == 1{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "EditProfileForIndividualVC") as! EditProfileForIndividualVC
            vc.delegate = self
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else if self.userType == 2{
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "EditProfileForBusinessVC") as! EditProfileForBusinessVC
            vc.delegate = self
            self.navigationController?.pushViewController(vc, animated: true)
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
    private func setupRefreshControl() {
        // Configure the refresh control
        refreshControl.tintColor = UIColor.darkGray // Customize the spinner color
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh") // Set a title
        // Add target-action for pull-to-refresh
        refreshControl.addTarget(self, action: #selector(refresh(sender:)), for: .valueChanged)
        // Ensure the scroll view supports vertical bounce
        scrollView.isScrollEnabled = true
        scrollView.alwaysBounceVertical = true
        // Assign the refresh control to the scroll view
        scrollView.refreshControl = refreshControl
    }
    @objc func refresh(sender: AnyObject) {
        serviceUserProfileAPI()
        refreshControl.endRefreshing()
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
    @IBAction func myOrderAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "MyOrderVC") as! MyOrderVC
        vc.keyType = self.keyType
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func myAddressAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "MyAddressListVC") as! MyAddressListVC
        vc.isAddressSelect = "1"
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func mySubscriptionAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "MySubscriptionVC") as! MySubscriptionVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func myProductsAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "MyProductsVC") as! MyProductsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func myWishListAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "MyWishListVC") as! MyWishListVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func referToFriendAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ReferFriendsVC") as! ReferFriendsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func reportAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ReportsVC") as! ReportsVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func renewProductWarrantyAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "RenewWarrantyVC") as! RenewWarrantyVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func renewSubscriptionPlanAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "RenewSubscriptionVC") as! RenewSubscriptionVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func paymentHistoryAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "PaymentHistoryVC") as! PaymentHistoryVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func customerSupportAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CustomerSupportRegisterComplantVC") as! CustomerSupportRegisterComplantVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func aboutUsAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AboutUsVC") as! AboutUsVC
        vc.userType = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func termsAndConditionsAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "TermsAndConditionsVC") as! TermsAndConditionsVC
        vc.userType = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func privacyPolicyAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "PrivacyPolicyVC") as! PrivacyPolicyVC
        vc.userType = 1
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func changePasswordAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ChangePasswordVC") as! ChangePasswordVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func deleteAccountAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "DeleteAccountVC") as! DeleteAccountVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.delegate = self
        self.present(vc, animated: true)
    }
    @IBAction func logOutBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "LogOutPopupVC") as! LogOutPopupVC
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.delegate = self // Set the delegate to handle logout action
        self.present(vc, animated: true, completion: nil)
    }
}
// MARK: GetUserProfileAPI ==>
extension ProfileVC {
    func serviceUserProfileAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "userProfile", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let profileImage = dic["profileImage"]?.stringValue ?? ""
                let firstName = dic["firstName"]?.stringValue ?? ""
                let lastName = dic["lastName"]?.stringValue ?? ""
                let country_code = dic["country_code"]?.stringValue ?? ""
                let userType = dic["userType"]?.intValue ?? 0
                let phoneNo = dic["phoneNo"]?.intValue ?? 0
            //  let contactNo = dic["contactNo"]?.intValue ?? 0
                let companyName = dic["companyName"]?.stringValue ?? ""
               // let contactPerson = dic["contactPerson"]?.stringValue ?? ""
                let email = dic["email"]?.stringValue ?? ""
                let businessEmail = dic["businessEmail"]?.stringValue ?? ""
                let businessPhoneNo = dic["businessPhoneNo"]?.intValue ?? 0
                UserDefaults.standard.set(email, forKey: "email")
                UserDefaults.standard.set(profileImage, forKey: "profileImage")
                self.userType = userType
                if phoneNo == 0{
                    self.mobileNoLbl.text = email
                }
                else{
                    self.mobileNoLbl.text = "\(country_code) \(phoneNo)"
                }
                if userType == 1{
                    self.changePasswordView.isHidden = true
                  //self.mobileNoLbl.text = "\(country_code) \(phoneNo)"
                    self.nameLbl.text = "\(firstName) \(lastName)"
                    self.profileImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
                    self.profileImg.sd_setImage(with: URL(string: profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
                }
                else{
                    self.changePasswordView.isHidden = false
                    if businessPhoneNo == 0{
                        self.mobileNoLbl.text = businessEmail
                    }
                    else{
                        self.mobileNoLbl.text = "\(country_code) \(businessPhoneNo)"
                    }
                    //self.mobileNoLbl.text = "\(country_code) \(contactNo)"
                    self.nameLbl.text = companyName
                    self.profileImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
                    self.profileImg.sd_setImage(with: URL(string: profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
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
