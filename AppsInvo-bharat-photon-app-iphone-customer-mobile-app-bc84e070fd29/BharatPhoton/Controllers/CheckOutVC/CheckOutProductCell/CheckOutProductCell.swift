//
//  CheckOutProductCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/06/24.
//

import UIKit

class CheckOutProductCell: UITableViewCell {
    @IBOutlet weak var installationLbl: UILabel!
    @IBOutlet weak var installationAmount: UILabel!
    @IBOutlet weak var deleteBtn: UIButton!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var productCategoryNameLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var qtyLbl: UILabel!
    @IBOutlet weak var crossBtn: UIButton!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
