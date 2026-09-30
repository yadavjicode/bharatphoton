//
//  MyAddressListVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 26/06/24.
//

import UIKit
 

class MyAddressListVC: UIViewController, AddressListApiCallDelegate {
    func didUpdateAddressAddAddressFetchData(address: String, zipCode: String, state: String, city: String, addressAdd: Bool) {
        serviceAddressListAPI()
    }
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var backView: UIView!
    @IBOutlet weak var popupView: UIView!
    private let maxPopupHeight: CGFloat = 500
    @IBOutlet weak var addressListTable: UITableView!
    var addressListModal: AddressListModal?
    var delegate: AddressListApiCallDelegate?
    var id = ""
    var isAddressSelect = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        msgLbl.isHidden = true
        backView.isHidden = true
        serviceAddressListAPI()
        addressListTable.delegate = self
        addressListTable.dataSource = self
        addressListTable.register(UINib(nibName: "MyAddressListCell", bundle: nil), forCellReuseIdentifier: "MyAddressListCell")
        self.addressListTable.separatorStyle = .none
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
    func updateCartBadge() {
        CartService.shared.fetchCartCount(for: cartBtn) { cartCount in
           
        }
    }
    func updateNotificationBadge(){
        NotificationCount.shared.fetchNotificationCount(for: notificationBtn) { notificationCount in
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        if isAddressSelect == ""{
            //alert(message: "Please Select Address")
            self.navigationController?.popViewController(animated: true)
        }
        else{
            self.navigationController?.popViewController(animated: true)
            //self.delegate?.didUpdateAddressAddAddressFetchData(address: "", zipCode: "", state: "", city: "", addressAdd: true)
        }
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
    @IBAction func deleteAddressPopupHide(_ sender: UIButton) {
        backView.isHidden = true
    }
    @IBAction func noGoBackBtnAction(_ sender: UIButton) {
        backView.isHidden = true
    }
    @IBAction func deleteAddressBtnAction(_ sender: UIButton) {
        self.serviceRemoveAddressAPI(id: self.id)
    }
    @IBAction func addAddressBtnTapped(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension MyAddressListVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return addressListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = addressListTable.dequeueReusableCell(withIdentifier: "MyAddressListCell", for: indexPath) as! MyAddressListCell
        cell.lbl.text = "\(addressListModal?.data?[indexPath.row].firstName ?? "") \(addressListModal?.data?[indexPath.row].lastName ?? "")"
        let countryCode = addressListModal?.data?[indexPath.row].country_code ?? ""
        let phoneNo = "\(addressListModal?.data?[indexPath.row].phoneNo ?? 0)"
        let address = addressListModal?.data?[indexPath.row].address ?? ""
        let city = addressListModal?.data?[indexPath.row].city ?? ""
        let state = addressListModal?.data?[indexPath.row].state ?? ""
        let zipCode = "\(addressListModal?.data?[indexPath.row].ZipCode ?? 0)"
        cell.mobileNoLbl.text = "\(countryCode) \(phoneNo)"
        cell.addressLbl.text = "\(address) \(city), \(state), \("Pincode") \(zipCode)"
        cell.selectionStyle = .none
        cell.sideMenuBtn.tag = indexPath.row
        cell.sideMenuBtn.addTarget(self, action: #selector(menuBtnTapped(sender:)), for: .touchUpInside)
        cell.deleteBtn.tag = indexPath.row
        cell.deleteBtn.addTarget(self, action: #selector(deleteAddressBtnTapped(sender:)), for: .touchUpInside)
        cell.editBtn.tag = indexPath.row
        cell.editBtn.addTarget(self, action: #selector(editAddressBtnTapped(sender:)), for: .touchUpInside)
        let selectedAddress = addressListModal?.data?[indexPath.row].selectedAddress ?? ""
        if selectedAddress == "1"{
            cell.selectedView.borderColor = UIColor(hexString: "FF8D3A")
            cell.selectedImg.image = UIImage(named: "radio selected")
        }
        else{
            cell.selectedView.borderColor = .clear
            cell.selectedImg.image = UIImage(named: "radio Unselected")
        }
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return addressListTable.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.serviceUpdateDeliveryAddressAPI(addressId: addressListModal?.data?[indexPath.row]._id ?? "")
    }
    // MARK: MenuBtnAction ==>
    @objc func menuBtnTapped(sender: UIButton) {
        let indexPath = IndexPath(row: sender.tag, section: 0)
        let cell = addressListTable.cellForRow(at: indexPath) as! MyAddressListCell
        cell.editDeletePopUpView.isHidden = false
    }
    //MARK: DeleteAddressBtnTapped Action ==>
    @objc func deleteAddressBtnTapped(sender: UIButton) {
        backView.isHidden = false
      //  setupSlidingPopupView(for: popupView, maxHeight: maxPopupHeight)
        let indexPath = IndexPath(row: sender.tag, section: 0)
        let cell = addressListTable.cellForRow(at: indexPath) as! MyAddressListCell
        cell.editDeletePopUpView.isHidden = true
        self.id = addressListModal?.data?[indexPath.row]._id ?? ""
    }
    //MARK: EditAddressBtnTapped Action ==>
    @objc func editAddressBtnTapped(sender: UIButton) {
        let indexPath = IndexPath(row: sender.tag, section: 0)
        let cell = addressListTable.cellForRow(at: indexPath) as! MyAddressListCell
        cell.editDeletePopUpView.isHidden = true
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "AddAddressVC") as! AddAddressVC
        vc.controllerName = "Edit Address"
        vc.id = addressListModal?.data?[indexPath.row]._id ?? ""
        vc.delegate = self
        self.navigationController?.pushViewController(vc, animated: false)
    }
}
// MARK: AddressList API ==>
extension MyAddressListVC {
    func serviceAddressListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addressList", params: parms, token:token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.addressListModal = try JSONDecoder().decode(AddressListModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.addressListModal?.data?.count ?? 0) == 0 ? false : true
                    self.backView.isHidden = true
                    self.addressListTable.reloadData()
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
// MARK: RemoveAddress API ==>
extension MyAddressListVC {
    func serviceRemoveAddressAPI(id: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .DELETE, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "removeAddress/\(id)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                self.serviceAddressListAPI()
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
// MARK: UpdateDeliveryAddress API ==>
extension MyAddressListVC {
    func serviceUpdateDeliveryAddressAPI(addressId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["addressId": addressId] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "updateDeliveryAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print(self.alert(message: JSONResponse["message"].stringValue))
            case 1:
                self.isAddressSelect = "1"
               // self.serviceAddressListAPI()
                self.delegate?.didUpdateAddressAddAddressFetchData(address: "", zipCode: "", state: "", city: "", addressAdd: true)
                self.navigationController?.popViewController(animated: false)
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
         }
    }
}
