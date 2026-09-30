//
//  CartProductCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/06/24.
//

import UIKit

class CartProductCell: UITableViewCell {
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var descriptionLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var minusView: UIView!
    @IBOutlet weak var plusView: UIView!
    @IBOutlet weak var quantityView: UIView!
    @IBOutlet weak var quantityLbl: UILabel!
    @IBOutlet weak var minusBtn: UIButton!
    @IBOutlet weak var plusBtn: UIButton!
    @IBOutlet weak var deleteBtn: UIButton!
    @IBOutlet weak var viewMoreBtn: UIButton!
    @IBOutlet weak var discountedPriceLbl: UILabel!
    @IBOutlet weak var ratingView: UIView!
    @IBOutlet weak var ratingLbl: UILabel!
    var isExpanded: Bool = false
    override func awakeFromNib() {
        super.awakeFromNib()
        descriptionLbl.numberOfLines = 1 // Set default number of lines to 1
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
    func configureCell(isExpanded: Bool) {
        descriptionLbl.numberOfLines = isExpanded ? 0 : 1 // 0 means no limit to lines (fully expanded)
        let buttonTitle = isExpanded ? "View Less" : "View More"
        viewMoreBtn.setTitle(buttonTitle, for: .normal)
    }
}

