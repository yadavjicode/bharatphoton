//
//  RenewPlanCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 21/06/24.
//

import UIKit

class RenewPlanCell: UICollectionViewCell {
    @IBOutlet weak var bgImg: UIImageView!
    @IBOutlet weak var shapeImg: UIImageView!
    @IBOutlet weak var planTypeLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var purchaseNowBtn: UIButton!
    @IBOutlet weak var descriptionLbl: UITextView!
    @IBOutlet weak var discountPercentageLbl: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
    }

}
