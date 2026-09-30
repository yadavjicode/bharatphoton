//
//  ThanksPurchasePlaneUploadSitePhotoVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 04/07/24.
//

import UIKit
import SDWebImage
 
class ThanksPurchasePlaneUploadSitePhotoVC: UIViewController {
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var orderNoLbl: UILabel!
    @IBOutlet weak var uploadSiteImageCollection: UICollectionView!
    var siteImages = [[String: Any]]()
    var orderId = ""
    var uuid = ""
    var id = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        orderNoLbl.text = self.uuid
        startPulsingAnimation(on: img)
        self.uploadSiteImageCollection.delegate = self
        self.uploadSiteImageCollection.dataSource = self
        self.uploadSiteImageCollection.register(UINib(nibName: "UploadSiteImagesCell", bundle: nil), forCellWithReuseIdentifier: "UploadSiteImagesCell")
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
    func startPulsingAnimation(on image: UIImageView) {
        let pulseAnimation = CABasicAnimation(keyPath: "transform.scale")
        pulseAnimation.duration = 0.8
        pulseAnimation.fromValue = 1.5
        pulseAnimation.toValue = 1.1
        pulseAnimation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.easeInEaseOut)
        pulseAnimation.autoreverses = true
        pulseAnimation.repeatCount = .infinity
        image.layer.add(pulseAnimation, forKey: "pulsing")
    }
    @IBAction func submitBtnAction(_ sender: UIButton) {
        if siteImages.isEmpty {
            self.alert(message: "Please upload site photo")
        }
        else{
            serviceUploadSiteImagesAPI()
        }
    }
    @IBAction func uploadLaterBtnAction(_ sender: UIButton) {
        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "TabVC") as! TabVC
        self.navigationController?.pushViewController(vc, animated: true)
    }
    @IBAction func addMoreBtnClickAction(_ sender: UIButton) {
        CameraHandler.shared.showActionSheet(vc: self)
           CameraHandler.shared.imagePickedBlock = { (image, path, url) in
               self.startLoading()
               AWSS3Manager.shared.uploadImage(image: image, progress: .none, completion: { response, error in
                   DispatchQueue.main.async {
                       if let error = error {
                           print(error.localizedDescription)
                       } else if let imgUrl = response as? String {
                           print(imgUrl)
                           self.stopLoading()
                           self.siteImages.append(["url": imgUrl])
                           self.uploadSiteImageCollection.reloadData()
                       }
                   }
               })
           }
     }
}
extension ThanksPurchasePlaneUploadSitePhotoVC: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout{
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return siteImages.count
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = uploadSiteImageCollection.dequeueReusableCell(withReuseIdentifier: "UploadSiteImagesCell", for: indexPath) as! UploadSiteImagesCell
        let image =  self.siteImages[indexPath.item]["url"]  ?? ""
        cell.siteImg.sd_setImage(with: URL(string: image as! String), placeholderImage: UIImage(named: ""))
        cell.deleteBtn.tag = indexPath.item
        cell.deleteBtn.addTarget(self, action: #selector(deleteBtnTapped(sender:)), for: .touchUpInside)
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return  CGSize(width: uploadSiteImageCollection.frame.width, height: uploadSiteImageCollection.frame.height)
    }
    @objc func deleteBtnTapped(sender: UIButton) {
        let item = sender.tag
        self.siteImages.remove(at: item)
        self.uploadSiteImageCollection.reloadData()
    }
}
extension ThanksPurchasePlaneUploadSitePhotoVC {
    func serviceUploadSiteImagesAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        let parms = ["orderId": self.orderId, "image": self.siteImages, "id": self.id] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .postJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "addInstallationAddress", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let  vc = self.storyboard?.instantiateViewController(withIdentifier: "SitePhotoSubmittedVC") as! SitePhotoSubmittedVC
                vc.siteImages = self.siteImages
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
