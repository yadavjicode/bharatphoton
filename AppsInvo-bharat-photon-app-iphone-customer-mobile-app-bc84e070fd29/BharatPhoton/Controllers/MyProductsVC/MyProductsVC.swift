//
//  MyProductsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 27/06/24.
//

import UIKit
import SDWebImage


class MyProductsVC: UIViewController {
    @IBOutlet weak var totalWaterSavedView: UIView!
    @IBOutlet weak var totalCostSavedView: UIView!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var qtyLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var popUpView: UIView!
    @IBOutlet weak var myProductTable: UITableView!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var waterSavedLbl: UILabel!
    @IBOutlet weak var costSavedLbl: UILabel!
    @IBOutlet weak var sinDetailsWarrantyDateTable: UITableView!
    @IBOutlet weak var sinDetailsWarrantyDateTableHeight: NSLayoutConstraint!
    var myProductModal: MyProductModal?
    var myProductDetailsModal: MyProductDetailsModal?
    var currentPage = 1
    var isLoading = false
    var hasMoreData = true
    var selectedFinNo: [String] = []
    var selectedQuantity = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        sinDetailsWarrantyDateTableHeight.constant = 0
        self.totalWaterSavedView.isHidden = true
        self.totalCostSavedView.isHidden = true
        updateCartBadge()
        updateNotificationBadge()
        msgLbl.isHidden = true
        serviceInstalledProductListingAPI(page: currentPage)
        popUpView.isHidden = true
        myProductTable.delegate = self
        myProductTable.dataSource = self
        myProductTable.register(UINib(nibName: "MyProductCell", bundle: nil), forCellReuseIdentifier: "MyProductCell")
        myProductTable.separatorStyle = .none
        sinDetailsWarrantyDateTable.delegate = self
        sinDetailsWarrantyDateTable.dataSource = self
        sinDetailsWarrantyDateTable.register(UINib(nibName: "SinDetailsWarrantyDateCell", bundle: nil), forCellReuseIdentifier: "SinDetailsWarrantyDateCell")
        sinDetailsWarrantyDateTable.separatorStyle = .none
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
    @IBAction func popupHideAction(_ sender: UIButton) {
        popUpView.isHidden = true
    }
    @IBAction func renewWarrantyBtnAction(_ sender: UIButton) {
        let warrentyStartDate = self.myProductDetailsModal?.data?.order?.products?.warrentyStartDate ?? ""
        let remainingWarrantyDays = self.myProductDetailsModal?.data?.order?.products?.remainingWarrantyDays ?? 0
        if warrentyStartDate == ""{
            alert(message: "Previous plan has not started yet")
        }
        else if remainingWarrantyDays <= 0{
            popUpView.isHidden = true
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "WarrantyPlanVC") as! WarrantyPlanVC
            vc.productId = self.myProductDetailsModal?.data?.order?.products?.productId ?? ""
            vc.orderId = self.myProductDetailsModal?.data?.order?._id ?? ""
            vc.selectedFinNo = self.selectedFinNo
            vc.selectedQuantity = self.selectedQuantity
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else{
            alert(message: "You can renew plan after \(remainingWarrantyDays) days")
        }
    }
    @IBAction func registerComplantBtnAction(_ sender: UIButton) {
        popUpView.isHidden = true
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "RegisterComplaintVC") as! RegisterComplaintVC
        vc.productName = self.myProductDetailsModal?.data?.order?.products?.product_name ?? ""
        vc.productId = self.myProductDetailsModal?.data?.order?.products?.productId ?? ""
        vc.categoryName = self.myProductDetailsModal?.data?.order?.products?.categoryName ?? ""
        vc.categoryId = self.myProductDetailsModal?.data?.order?.products?.categoryId ?? ""
        vc.sin = self.myProductDetailsModal?.data?.order?.installationInfo?.first?.sin ?? ""
        vc.myProductDetailsModal = myProductDetailsModal
        vc.type = 1
        vc.orderId = self.myProductDetailsModal?.data?.order?._id ?? ""
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func downloadReportAction(_ sender: UIButton) {
        let invoiceUrl = self.myProductDetailsModal?.data?.order?.invoiceUrl ?? ""
        self.downloadInvoice(invoiceUrl: invoiceUrl)
    }
    @IBAction func downloadInvoiceAction(_ sender: UIButton) {
        let invoiceUrl = self.myProductDetailsModal?.data?.order?.invoiceUrl ?? ""
        self.downloadInvoice(invoiceUrl: invoiceUrl)
    }
}
extension MyProductsVC: UITableViewDelegate, UITableViewDataSource{
    var expandedIndexPath: IndexPath? {
        get {
            return objc_getAssociatedObject(self, &AssociatedKeys.expandedIndexPath) as? IndexPath
        }
        set {
            objc_setAssociatedObject(self, &AssociatedKeys.expandedIndexPath, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    func numberOfSections(in tableView: UITableView) -> Int {
        if tableView == myProductTable{
            return myProductModal?.data?.count ?? 0
        }
        else if tableView == sinDetailsWarrantyDateTable{
            return 1
        }
        return 0
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if tableView == myProductTable {
            return myProductModal?.data?[section].products?.count ?? 0
        }
        else if tableView == sinDetailsWarrantyDateTable {
            let rowCount = myProductDetailsModal?.data?.order?.installationInfo?.count ?? 0
            print("Returning Rows for Warranty Table: \(rowCount), Called at: \(Date())")
            return rowCount
        }
        return 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if tableView == myProductTable{
            let cell = myProductTable.dequeueReusableCell(withIdentifier: "MyProductCell", for: indexPath) as! MyProductCell
            cell.orderIdLbl.text = myProductModal?.data?[indexPath.section].products?[indexPath.row].uuid ?? ""
            cell.productImg.sd_setImage(with: URL(string: myProductModal?.data?[indexPath.section].products?[indexPath.row].image ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
            cell.productNameLbl.text = myProductModal?.data?[indexPath.section].products?[indexPath.row].product_name ?? ""
            cell.modalNoLbl.text = myProductModal?.data?[indexPath.section].products?[indexPath.row].modelNo ?? ""
            cell.costSavedLbl.text = "₹ \(myProductModal?.data?[indexPath.section].products?[indexPath.row].costSaved ?? 0)"
            cell.waterSavedLbl.text = "\(myProductModal?.data?[indexPath.section].products?[indexPath.row].waterSaved ?? 0) Ltrs"
            cell.arrowBtn.tag = indexPath.row
            cell.arrowBtn.superview?.tag = indexPath.section
            cell.arrowBtn.addTarget(self, action: #selector(arrowBtnTapped(sender:)), for: .touchUpInside)
            // Update arrow image based on state
            cell.modalNameView.isHidden = false
            let remainingWarrantyDays = Int(floor(myProductModal?.data?[indexPath.section].products?[indexPath.row].remainingWarrantyDays ?? 0.0))
            if remainingWarrantyDays == 0{
                cell.warrantyPlanExpViw.isHidden = true
            }
            else if remainingWarrantyDays <= 0{
                cell.warrantyPlanExpViw.isHidden = false
                cell.warrantyPlanExpLbl.text = "Your Plan Has Expired"
                cell.warrantyPlanExpLbl.textColor = .red
            }
            else{
                cell.warrantyPlanExpViw.isHidden = false
                cell.warrantyPlanExpLbl.textColor = .black
                cell.warrantyPlanExpLbl.text = "Your warranty will expire in : \(remainingWarrantyDays) Days"
            }
            let isFieldAvailable = myProductModal?.data?[indexPath.section].products?[indexPath.row].isFieldAvailable ?? 0
            let isUrinalSensorAvilable = myProductModal?.data?[indexPath.section].products?[indexPath.row].isUrinalSensorAvilable ?? 0
            cell.waterSavedCostSavedView.isHidden = !(isFieldAvailable == 1 || isUrinalSensorAvilable == 1)
            let isExpanded = (expandedIndexPath == indexPath)
            cell.stack2.isHidden = !isExpanded
            cell.arrowBtn.setImage(UIImage(named: isExpanded ? "DropDown" : "greater-than"), for: .normal)
            cell.selectionStyle = .none
            return cell
        }
        else if tableView == sinDetailsWarrantyDateTable {
            print("Cell for row at: \(indexPath.row)") // Debugging
            let cell = tableView.dequeueReusableCell(withIdentifier: "SinDetailsWarrantyDateCell", for: indexPath) as! SinDetailsWarrantyDateCell
            if let installationInfo = myProductDetailsModal?.data?.order?.installationInfo {
                let info = installationInfo[indexPath.row]
                cell.sinNumberLbl.text = "SIN : \(info.sin ?? "")"
                cell.warrantyStartDateLbl.text = info.warrentyStartDate?.toDisplayDate() ?? ""
                cell.warrantyExpiryDateLbl.text = info.warrentyEndDate?.toDisplayDate() ?? ""
            }
            cell.selectionStyle = .none
            return cell
        }
        return UITableViewCell()
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        if tableView == myProductTable{
            if expandedIndexPath == indexPath {
                return UITableView.automaticDimension // Expanded row height
            } else {
                return 160 // Collapsed row height (adjust as needed)
            }
        }
        else{
            return 130
        }
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if tableView == myProductTable{
            popUpView.isHidden = false
            let orderId = myProductModal?.data?[indexPath.section]._id ?? ""
            let id = myProductModal?.data?[indexPath.section].products?[indexPath.row].productId ?? ""
            self.waterSavedLbl.text = "\(myProductModal?.data?[indexPath.section].products?[indexPath.row].waterSaved ?? 0) Ltrs"
            self.costSavedLbl.text = "₹ \(myProductModal?.data?[indexPath.section].products?[indexPath.row].costSaved ?? 0)"
            serviceInstalledProductDetailsAPI(orderId: orderId, id: id)
        }
        else{
            popUpView.isHidden = true
            let vc = self.storyboard?.instantiateViewController(withIdentifier: "SinDetailsVC") as! SinDetailsVC
            vc.orderId = myProductDetailsModal?.data?.order?.installationInfo?[indexPath.row].orderId ?? ""
            vc.productId = myProductDetailsModal?.data?.order?.installationInfo?[indexPath.row].productId ?? ""
            vc.installationId = myProductDetailsModal?.data?.order?.installationInfo?[indexPath.row]._id ?? ""
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        let offsetY = scrollView.contentOffset.y
        let contentHeight = scrollView.contentSize.height
        let tableViewHeight = scrollView.frame.size.height
        if offsetY > contentHeight - tableViewHeight - 100 {  // Load more when 100px from bottom
            if !isLoading && hasMoreData {
                currentPage += 1
                serviceInstalledProductListingAPI(page: currentPage)
            }
        }
    }
    @objc func arrowBtnTapped(sender: UIButton) {
        let row = sender.tag
        if let sectionIndex = sender.superview?.tag {
            let indexPath = IndexPath(row: row, section: sectionIndex)
            
            if expandedIndexPath == indexPath {
                expandedIndexPath = nil // Collapse if already expanded
            } else {
                expandedIndexPath = indexPath // Expand the selected row
                myProductTable.reloadData()
            }
            // Reload the specific row to reflect the height change
            myProductTable.reloadRows(at: [indexPath], with: .automatic)
        }
    }
    // Associated key for storing isExpandedArray
    private struct AssociatedKeys {
        static var expandedIndexPath = "expandedIndexPath"
    }
}
extension MyProductsVC {
    func serviceInstalledProductListingAPI(page: Int) {
        guard !isLoading, hasMoreData else { return }
        isLoading = true
        if page == 1 {
            LoaderManager.shared.showLoader(in: self.view)
        } else {
            LoaderManager.shared.hideLoader()
        }
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let urlString = ServiceEndPoint.BhartPhotonUrl.baseURL + "installedProductListing?page=\(page)&limit=10"
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: urlString, params: [:], token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    let newData = try JSONDecoder().decode(MyProductModal.self, from: JSONResponse.rawData())

                    if page == 1 {
                        self.myProductModal = newData
                    } else {
                        self.myProductModal?.data?.append(contentsOf: newData.data ?? [])
                    }
                    self.hasMoreData = !(newData.data?.isEmpty ?? true)
                    self.msgLbl.isHidden = (self.myProductModal?.data?.count ?? 0) > 0
                    self.myProductTable.reloadData()
                } catch {
                    print("Error decoding JSON: \(error)")
                }
            default:
                print("Unidentified code found")
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.isLoading = false
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
        }
    }
}
extension MyProductsVC {
    func serviceInstalledProductDetailsAPI(orderId: String, id: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "installedProductListing?page=1&limit=10&id=\(id)&orderId=\(orderId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            print(JSONResponse)
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.myProductDetailsModal = try JSONDecoder().decode(MyProductDetailsModal.self, from: JSONResponse.rawData())
                    if self.myProductDetailsModal?.data?.order?.installationInfo?.count == 0{
                        self.sinDetailsWarrantyDateTableHeight.constant = 0
                    }
                    else{
                        self.sinDetailsWarrantyDateTableHeight.constant = 150
                    }
                    self.sinDetailsWarrantyDateTable.reloadData()
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
        let isFieldAvailable = self.myProductDetailsModal?.data?.order?.products?.isFieldAvailable ?? 0
        let isUrinalSensorAvilable = self.myProductDetailsModal?.data?.order?.products?.isUrinalSensorAvilable ?? 0
        let shouldShowViews = (isFieldAvailable == 1 || isUrinalSensorAvilable == 1)
        self.totalWaterSavedView.isHidden = !shouldShowViews
        self.totalCostSavedView.isHidden = !shouldShowViews
        if let installationDetails = myProductDetailsModal?.data?.order?.products?.installationDetails{
                selectedFinNo.removeAll()
                for detail in installationDetails.prefix(2) {
                    selectedFinNo.append(detail.finNo ?? "")
                    print("FIN NO ==> \(selectedFinNo)")
                }
         }
        self.selectedQuantity = selectedFinNo.count
        let imageUrl = self.myProductDetailsModal?.data?.order?.products?.image ?? ""
        self.productImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
        self.productImg.sd_setImage(with: URL(string: imageUrl), placeholderImage: UIImage(named: "profilePlaceholder"))
        self.productNameLbl.text = "\(self.myProductDetailsModal?.data?.order?.products?.product_name ?? "")"
        self.modalNameLbl.text = "Model Name: \(self.myProductDetailsModal?.data?.order?.products?.modelNo ?? "")"
        self.qtyLbl.text = "QTY: \(self.myProductDetailsModal?.data?.order?.products?.quantity ?? 0)"
        
    }
}
