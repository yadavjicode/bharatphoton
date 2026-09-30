//
//  ProductImageDetailsVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 07/02/25.
//

import UIKit

class ProductImageDetailsVC: UIViewController {
    @IBOutlet weak var productImgCollection: UICollectionView!
    var reviewImgArr: [String] = []
    var isReviews = false
    var productImages: [ProductDetailsImg]?
    var selectedIndex: Int?
    override func viewDidLoad() {
        super.viewDidLoad()
        print(reviewImgArr)
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
       // productImgCollection.isPagingEnabled = true
        productImgCollection.delegate = self
        productImgCollection.dataSource = self
        productImgCollection.register(UINib(nibName: "ProductImageDetailsCell", bundle: nil), forCellWithReuseIdentifier: "ProductImageDetailsCell")
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        // Scroll after the layout is set and data is loaded.
        if let index = selectedIndex, index < productImgCollection.numberOfItems(inSection: 0) {
            productImgCollection.scrollToItem(at: IndexPath(item: index, section: 0), at: .centeredHorizontally, animated: true)
        }
    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
}
extension ProductImageDetailsVC: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        if isReviews == true{
            return reviewImgArr.count
        }
        else{
            return productImages?.count ?? 0
        }
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = productImgCollection.dequeueReusableCell(withReuseIdentifier: "ProductImageDetailsCell", for: indexPath) as! ProductImageDetailsCell
        if isReviews == true{
            cell.productImg.sd_setImage(with: URL(string: reviewImgArr[indexPath.item]), placeholderImage: UIImage(named: "placeHolder"))
        }
        else{
            cell.productImg.sd_setImage(with: URL(string: self.productImages?[indexPath.item].url ?? ""), placeholderImage: UIImage(named: "profilePlaceholder"))
        }
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return  CGSize(width: (self.productImgCollection.frame.width), height: self.productImgCollection.frame.height)
    }
}

