//
//  ProductCollectionCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit

class ProductCollectionCell: UICollectionViewCell {
    @IBOutlet weak var viewDetailBtn: UIButton!
    @IBOutlet weak var discountPriceLbl: UILabel!
    @IBOutlet weak var discountPercentageLbl: UILabel!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var wishListBtn: UIButton!
    @IBOutlet weak var catNameLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var cartBtn: UIButton!
    @IBOutlet weak var plusBtn: UIButton!
    @IBOutlet weak var minusBtn: UIButton!
    @IBOutlet weak var quantityLbl: UILabel!
    @IBOutlet weak var minusView: UIView!
    @IBOutlet weak var quantityView: UIView!
    @IBOutlet weak var plusView: UIView!
    @IBOutlet weak var cartView: UIView!
    @IBOutlet weak var outOfStockView: UIView!
    @IBOutlet weak var ratingView: UIView!
    @IBOutlet weak var ratingLbl: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        minusView.isHidden = true
        plusView.isHidden = true
        quantityView.isHidden = true
        outOfStockView.isHidden = true
   }
    
}
