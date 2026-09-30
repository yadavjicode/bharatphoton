//
//  ReviewCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 28/06/24.
//

import UIKit
import SDWebImage

class ReviewCell: UITableViewCell {
    @IBOutlet weak var imagesCollection: UICollectionView!
    @IBOutlet weak var nameLbl: UILabel!
    @IBOutlet weak var profileImg: UIImageView!
    @IBOutlet weak var ratingView: CosmosView!
    @IBOutlet weak var reviewLbl: UILabel!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var viewHeight: NSLayoutConstraint!
    var reviewImgArr: [String] = []
    override func awakeFromNib() {
        super.awakeFromNib()
        imagesCollection.delegate = self
        imagesCollection.dataSource = self
        imagesCollection.register(UINib(nibName: "ReviewImagesCollectionCell", bundle: nil), forCellWithReuseIdentifier: "ReviewImagesCollectionCell")
        
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
}
extension ReviewCell: UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return reviewImgArr.count
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = imagesCollection.dequeueReusableCell(withReuseIdentifier: "ReviewImagesCollectionCell", for: indexPath) as! ReviewImagesCollectionCell
        cell.reviewImg.sd_imageIndicator = SDWebImageActivityIndicator.gray
        cell.reviewImg.sd_setImage(with: URL(string: reviewImgArr[indexPath.item]), placeholderImage: UIImage(named: "profilePlaceholder"))
        return cell
    }
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize {
        return  CGSize(width: imagesCollection.frame.width/3.2, height: 60)
    }
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        let item = indexPath.item
        if let parentVC = self.getParentViewController() as? UIViewController {
            if let vc = parentVC.storyboard?.instantiateViewController(withIdentifier: "ProductImageDetailsVC") as? ProductImageDetailsVC {
                vc.selectedIndex = item
                vc.reviewImgArr = reviewImgArr
                vc.isReviews = true
                parentVC.navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}
extension ReviewCell {
    func getParentViewController() -> UIViewController? {
        var parentResponder: UIResponder? = self
        while parentResponder != nil {
            parentResponder = parentResponder?.next
            if let viewController = parentResponder as? UIViewController {
                return viewController
            }
        }
        return nil
    }
}

