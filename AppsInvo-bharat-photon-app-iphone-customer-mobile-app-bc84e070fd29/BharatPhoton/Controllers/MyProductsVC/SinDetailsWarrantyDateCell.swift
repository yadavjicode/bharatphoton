//
//  SinDetailsWarrantyDateCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 19/02/25.
//

import UIKit

class SinDetailsWarrantyDateCell: UITableViewCell {
    
    @IBOutlet weak var sinNumberLbl: UILabel!
    @IBOutlet weak var warrantyStartDateLbl: UILabel!
    @IBOutlet weak var warrantyExpiryDateLbl: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
