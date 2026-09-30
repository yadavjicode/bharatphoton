//
//  ReNewPlanCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit

class RenewWarrantyCell: UITableViewCell {
    @IBOutlet weak var oderIdLbl: UILabel!
    @IBOutlet weak var remainingWarrantyDaysLbl: UILabel!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var categoryNameLbl: UILabel!
    @IBOutlet weak var modalNoLbl: UILabel!
    @IBOutlet weak var renewWarrantyBtn: UIButton!
    @IBOutlet weak var renewWarrantyLbl: UILabel!
    @IBOutlet weak var sinNoLbl: UILabel!
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
