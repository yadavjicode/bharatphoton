//
//  CategoriesVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit
import SDWebImage
 

class CategoriesVC: UIViewController {
    @IBOutlet weak var msgLbl: UILabel!
    @IBOutlet weak var backBtn: UIButton!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var notificationBtn: UIButton!
    @IBOutlet weak var categoriesCollection: UICollectionView!
    var categoryListModel: CategoryModal?
    var keyType = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        self.msgLbl.isHidden = true
        self.statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        updateCartBadge()
        updateNotificationBadge()
        serviceCategoryListAPI()
        categoriesCollection.delegate = self
        categoriesCollection.dataSource = self
        categoriesCollection.register(UINib(nibName: "CategoriesCollectionCell", bundle: nil), forCellWithReuseIdentifier: "CategoriesCollectionCell")
        if keyType == 1{
            backBtn.isHidden = false
        }
        else{
            backBtn.isHidden = true
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
}
extension CategoriesVC: UICollectionViewDelegateFlowLayout, UICollectionViewDelegate, UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return categoryListModel?.data?.count ?? 0
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = categoriesCollection.dequeueReusableCell(withReuseIdentifier: "CategoriesCollectionCell", for: indexPath) as! CategoriesCollectionCell
       
        cell.categoryNameLbl.text = self.categoryListModel?.data?[indexPath.item].categoryName ?? ""
        cell.categoryImg.sd_setImage(with: URL(string: self.categoryListModel?.data?[indexPath.item].image ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return CGSize(width: (self.categoriesCollection.bounds.width/2.8)-32, height: 130)
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "ProductVC") as! ProductVC
        vc.categoryId = self.categoryListModel?.data?[indexPath.item]._id ?? ""
        vc.selectedIndex = indexPath.item
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
extension CategoriesVC {
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
                    self.categoryListModel = try JSONDecoder().decode(CategoryModal.self, from: JSONResponse.rawData())
                    self.msgLbl.isHidden = (self.categoryListModel?.data?.count ?? 0) == 0 ? false : true
                    self.categoriesCollection.reloadData()
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
