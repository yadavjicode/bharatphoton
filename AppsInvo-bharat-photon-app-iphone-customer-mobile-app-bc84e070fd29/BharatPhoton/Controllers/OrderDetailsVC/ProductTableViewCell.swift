//
//  ProductTableViewCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 24/10/24.
//

import UIKit

class ProductTableViewCell: UITableViewCell {
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var descriptionLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var orderStatusView: UIView!
    @IBOutlet weak var orderStatusLbl: UILabel!
    @IBOutlet weak var trackOrderView: UIView!
    @IBOutlet weak var reOrderView: UIView!
    @IBOutlet weak var addReviewView: UIView!
    @IBOutlet weak var cancelOrderView: UIView!
    @IBOutlet weak var trackOrderBtn: UIButton!
    @IBOutlet weak var reOrderBrn: UIButton!
    @IBOutlet weak var addReviewBtn: UIButton!
    @IBOutlet weak var cancelOrderBtn: UIButton!
    @IBOutlet weak var qtyLbl: UILabel!
    @IBOutlet weak var installationPriceLbl: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
