//
//  WarrantyPlanCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 09/12/24.
//

import UIKit

class WarrantyPlanCell: UITableViewCell {
    @IBOutlet weak var selectedImg: UIImageView!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
