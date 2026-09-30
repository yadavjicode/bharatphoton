//
//  CouponsCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 20/06/24.
//

import UIKit

class CouponsCell: UITableViewCell {
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var desLbl: UILabel!
    @IBOutlet weak var discountLbl: UILabel!
    @IBOutlet weak var applyBtn: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
