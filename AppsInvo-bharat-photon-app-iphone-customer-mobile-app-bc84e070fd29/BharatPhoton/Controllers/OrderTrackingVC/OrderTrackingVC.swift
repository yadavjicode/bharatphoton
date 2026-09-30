//
//  OrderTrackingVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit

class OrderTrackingVC: UIViewController {
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var orderReceivedImg: UIImageView!
    @IBOutlet weak var orderProcessedImg: UIImageView!
    @IBOutlet weak var orderDispatchedImg: UIImageView!
    @IBOutlet weak var orderDeliveredImg: UIImageView!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var orderIdLbl: UILabel!
    @IBOutlet weak var amountLbl: UILabel!
    @IBOutlet weak var statusLbl: UILabel!
    @IBOutlet weak var statusView: UIView!
    @IBOutlet weak var addressLbl: UILabel!
    @IBOutlet weak var pendingProductDateLbl: UILabel!
    @IBOutlet weak var processedProductDateLbl: UILabel!
    @IBOutlet weak var dispatchedProductDateLbl: UILabel!
    @IBOutlet weak var deliveredProductDateLbl: UILabel!
    @IBOutlet weak var estimatedDeliveryTimeLbl: UILabel!
    var trackOrderDetailsModal: TrackOrderDetailsModal?
    var id = ""
    var refreshControl = UIRefreshControl()
    override func viewDidLoad() {
        super.viewDidLoad()
        self.statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        scrollView.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        setupRefreshControl()
        serviceTrackOderAPI()
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
    @IBAction func cartBtnAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "CartVC") as! CartVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func callBtnAction(_ sender: UIButton) {
        makeCall()
    }
    private func setupRefreshControl() {
        refreshControl.tintColor = UIColor.darkGray // Customize the spinner color
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh") // Set a title
        refreshControl.addTarget(self, action: #selector(refresh(sender:)), for: .valueChanged)
        scrollView.isScrollEnabled = true
        scrollView.alwaysBounceVertical = true
        // Assign the refresh control to the scroll view
        scrollView.refreshControl = refreshControl
    }
    @objc func refresh(sender: AnyObject) {
        serviceTrackOderAPI()
        refreshControl.endRefreshing()
    }
    func startPulsingAnimation(on image: UIImageView) {
        let pulse = CABasicAnimation(keyPath: "transform.scale")
        pulse.fromValue = 1.0
        pulse.toValue = 1.2
        pulse.duration = 1.0
        pulse.repeatCount = Float.infinity
        pulse.timingFunction = CAMediaTimingFunction(name: .easeInEaseOut)
        image.layer.add(pulse, forKey: "pulsing")
    }
    func stopPulsingAnimation(on image: UIImageView) {
        image.layer.removeAnimation(forKey: "pulsing")
    }
}
extension OrderTrackingVC {
    func serviceTrackOderAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "orderDetails?id=\(self.id)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.trackOrderDetailsModal = try JSONDecoder().decode(TrackOrderDetailsModal.self, from: JSONResponse.rawData())
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
        let price = self.trackOrderDetailsModal?.data?.product?.price ?? 0
        self.amountLbl.text = "₹ \(NumberFormatter.localizedString(from: NSNumber(value: price), number: .decimal))"
        let dateString =  trackOrderDetailsModal?.data?.createdAt ?? ""
        dateLbl.text = dateString.dateAndDay()
        self.orderIdLbl.text = trackOrderDetailsModal?.data?.uuid ?? ""
        let city = self.trackOrderDetailsModal?.data?.address?.city ?? ""
        let state = self.trackOrderDetailsModal?.data?.address?.state ?? ""
        let zipCode = "\(self.trackOrderDetailsModal?.data?.address?.ZipCode ?? 0)"
        let address = self.trackOrderDetailsModal?.data?.address?.address ?? ""
        self.addressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
        let finalOrderStatus = self.trackOrderDetailsModal?.data?.product?.orderStatus ?? 0
        if finalOrderStatus == 0{
            startPulsingAnimation(on: orderReceivedImg)
            orderReceivedImg.image = UIImage(named: "check green")
            orderProcessedImg.image = UIImage(named: "Hide green")
            orderDispatchedImg.image = UIImage(named: "Hide green")
            orderDeliveredImg.image = UIImage(named: "Hide green")
            self.statusView.backgroundColor = UIColor(hexString: "ffe0ee")
            self.statusLbl.textColor = UIColor(hexString: "cf005a")
            self.statusLbl.text = "Pending"
        }
        else if finalOrderStatus == 1{
            orderReceivedImg.layer.removeAnimation(forKey: "pulsing")
            startPulsingAnimation(on: orderProcessedImg)
            orderReceivedImg.image = UIImage(named: "check green")
            orderProcessedImg.image = UIImage(named: "check green")
            orderDispatchedImg.image = UIImage(named: "Hide green")
            orderDeliveredImg.image = UIImage(named: "Hide green")
            self.statusView.backgroundColor = UIColor(hexString: "FFE8D4")
            self.statusLbl.textColor = UIColor(hexString: "FF8D3A")
            self.statusLbl.text = "Processed"
        }
        else if finalOrderStatus == 2{
            orderReceivedImg.layer.removeAnimation(forKey: "pulsing")
            orderProcessedImg.layer.removeAnimation(forKey: "pulsing")
            startPulsingAnimation(on: orderDispatchedImg)
            orderReceivedImg.image = UIImage(named: "check green")
            orderProcessedImg.image = UIImage(named: "check green")
            orderDispatchedImg.image = UIImage(named: "check green")
            orderDeliveredImg.image = UIImage(named: "Hide green")
            self.statusView.backgroundColor = UIColor(hexString: "D4E4FB")
            self.statusLbl.textColor = UIColor(hexString: "1B6AC6")
            self.statusLbl.text = "Dispatched"
        }
        else if finalOrderStatus == 3{
            orderReceivedImg.layer.removeAnimation(forKey: "pulsing")
            orderProcessedImg.layer.removeAnimation(forKey: "pulsing")
            orderDispatchedImg.layer.removeAnimation(forKey: "pulsing")
            startPulsingAnimation(on: orderDeliveredImg)
            orderReceivedImg.image = UIImage(named: "check green")
            orderProcessedImg.image = UIImage(named: "check green")
            orderDispatchedImg.image = UIImage(named: "check green")
            orderDeliveredImg.image = UIImage(named: "check green")
            self.statusView.backgroundColor = UIColor(hexString: "CEF2DF")
            self.statusLbl.textColor = UIColor(hexString: "00B130")
            self.statusLbl.text = "Delivered"
        }
        else if finalOrderStatus == 5{
            orderReceivedImg.layer.removeAnimation(forKey: "pulsing")
            orderProcessedImg.layer.removeAnimation(forKey: "pulsing")
            orderDispatchedImg.layer.removeAnimation(forKey: "pulsing")
            orderDeliveredImg.layer.removeAnimation(forKey: "pulsing")
            orderReceivedImg.image = UIImage(named: "Hide green")
            orderProcessedImg.image = UIImage(named: "Hide green")
            orderDispatchedImg.image = UIImage(named: "Hide green")
            orderDeliveredImg.image = UIImage(named: "Hide green")
            self.statusView.backgroundColor = UIColor(hexString: "ffbfbf")
            self.statusLbl.textColor = UIColor(hexString: "ff2c2c")
            self.statusLbl.text = "Canceled"
        }
        else if finalOrderStatus == 7{
            orderReceivedImg.layer.removeAnimation(forKey: "pulsing")
            orderProcessedImg.layer.removeAnimation(forKey: "pulsing")
            orderDispatchedImg.layer.removeAnimation(forKey: "pulsing")
            orderDeliveredImg.layer.removeAnimation(forKey: "pulsing")
            orderReceivedImg.image = UIImage(named: "Hide green")
            orderProcessedImg.image = UIImage(named: "Hide green")
            orderDispatchedImg.image = UIImage(named: "Hide green")
            orderDeliveredImg.image = UIImage(named: "Hide green")
            self.statusView.backgroundColor = UIColor(hexString: "ffbfbf")
            self.statusLbl.textColor = UIColor(hexString: "ff2c2c")
            self.statusLbl.text = "Canceled"
        }
        let pendingTimeString = trackOrderDetailsModal?.data?.product?.pendingProductDate ?? ""
        let processedTimeString = trackOrderDetailsModal?.data?.product?.processedProductDate ?? ""
        let dispatchedTimeString = trackOrderDetailsModal?.data?.product?.dispatchedProductDate ?? ""
        let deliveredTimeString = trackOrderDetailsModal?.data?.product?.deliveredProductDate ?? ""
        self.pendingProductDateLbl.text = pendingTimeString.toFormattedTimeString()
        self.processedProductDateLbl.text = processedTimeString.toFormattedTimeString()
        self.dispatchedProductDateLbl.text = dispatchedTimeString.toFormattedTimeString()
        self.deliveredProductDateLbl.text = deliveredTimeString.toFormattedTimeString()
   }
}

 
