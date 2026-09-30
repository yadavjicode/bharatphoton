//
//  RenewSubscriptioPlanCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 19/02/25.
//

import UIKit

class RenewSubscriptioPlanCell: UITableViewCell {
    @IBOutlet weak var uuidLbl: UILabel!
    @IBOutlet weak var categoryNameLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var modelNoLbl: UILabel!
    @IBOutlet weak var sinNoLbl: UILabel!
    @IBOutlet weak var productImage: UIImageView!
    @IBOutlet weak var renewSubscriptionBtn: UIButton!
    @IBOutlet weak var renewSubscriptionLbl: UILabel!
    @IBOutlet weak var remainingSubscriptionDaysLbl: UILabel!
    @IBOutlet weak var selectUnselectBtn: UIButton!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
