//
//  SpecificationsCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/09/24.
//

import UIKit

class SpecificationsCell: UITableViewCell {
    @IBOutlet weak var view: UIView!
    @IBOutlet weak var filedNameLbl: UILabel!
    @IBOutlet weak var valueLbl: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
