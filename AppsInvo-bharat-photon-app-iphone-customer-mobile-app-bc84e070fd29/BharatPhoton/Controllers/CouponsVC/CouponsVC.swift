//
//  CouponsVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/06/24.
//

import UIKit

protocol APICartDetailsRequestDelegate: AnyObject{
    func didUpdateCartDetailFetchData(couponCode: String)
}
class CouponsVC: UIViewController {
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var couponListTable: UITableView!
    @IBOutlet weak var popupView: UIView!
    private let maxPopupHeight: CGFloat = 600
    var couponListModal: CouponListModal?
    var delegate: APICartDetailsRequestDelegate?
    var cartId = ""
    var couponCode = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        setupSlidingPopup(for: popupView, maxHeight: maxPopupHeight)
        msgLbl.isHidden = true
        serviceCouponListAPI()
        couponListTable.delegate = self
        couponListTable.dataSource = self
        couponListTable.register(UINib(nibName: "CouponsCell", bundle: nil), forCellReuseIdentifier: "CouponsCell")
        couponListTable.rowHeight = UITableView.automaticDimension
        couponListTable.separatorStyle = .none
    }
    @IBAction func hidePopViewAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
}
extension CouponsVC: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return couponListModal?.data?.count ?? 0
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = couponListTable.dequeueReusableCell(withIdentifier: "CouponsCell", for: indexPath) as! CouponsCell
        cell.titleLbl.text = self.couponListModal?.data?[indexPath.row].title ?? ""
        let des = self.couponListModal?.data?[indexPath.row].description ?? ""
        cell.desLbl.attributedText = des.htmlToAttributedString()
        let discount = self.couponListModal?.data?[indexPath.row].discount ?? 0
        cell.discountLbl.text = "SAVE ₹ \(discount) ON THIS ORDER"
        cell.applyBtn.tag = indexPath.row
        cell.applyBtn.addTarget(self, action: #selector(applyBtnTapped(sender:)), for: .touchUpInside)
        let isCouponApplied = self.couponListModal?.data?[indexPath.row].isCouponApplied ?? false
        if isCouponApplied == true{
            cell.applyBtn.setTitle("Applied", for: .normal)
        }
        else{
            cell.applyBtn.setTitle("Apply", for: .normal)
        }
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return couponListTable.rowHeight
    }
    @objc func applyBtnTapped(sender: UIButton) {
        let row = sender.tag
        let id = self.couponListModal?.data?[row]._id ?? ""
        self.couponCode = self.couponListModal?.data?[row].couponCode ?? ""
        serviceApplyCouponCodeAPI(couponId: id)
    }
}
// MARK: CouponList API ==>
extension CouponsVC {
    func serviceCouponListAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "couponList?cartId=\(self.cartId)", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                do {
                    self.couponListModal = try JSONDecoder().decode(CouponListModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.couponListModal?.data?.count ?? 0) == 0 ? false : true
                    self.couponListTable.reloadData()
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
// MARK: ApplyCouponCode API ==>
extension CouponsVC {
    func serviceApplyCouponCodeAPI(couponId: String){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["couponId": couponId, "cartId": self.cartId] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "applyCoupon", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                //self.alert(message: JSONResponse["message"].stringValue)
                self.dismiss(animated: false, completion: nil)
                self.delegate?.didUpdateCartDetailFetchData(couponCode: self.couponCode)
            case 1:
                self.dismiss(animated: false, completion: nil)
                self.delegate?.didUpdateCartDetailFetchData(couponCode: self.couponCode)
               // self.alert(message: JSONResponse["message"].stringValue)
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.dismiss(animated: false, completion: nil)
            self.delegate?.didUpdateCartDetailFetchData(couponCode: self.couponCode)
          //  self.alert(message: JSONResponse["message"].stringValue, title: Error)
       }
    }
}
