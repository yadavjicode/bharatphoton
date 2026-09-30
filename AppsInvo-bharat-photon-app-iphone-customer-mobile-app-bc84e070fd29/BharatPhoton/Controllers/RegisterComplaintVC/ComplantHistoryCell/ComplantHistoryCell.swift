//
//  ComplantHistoryCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 02/07/24.
//

import UIKit

class ComplantHistoryCell: UITableViewCell {
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var productCategoryNameLbl: UILabel!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var sinLbl: UILabel!
    @IBOutlet weak var complantIssueLbl: UILabel!
    @IBOutlet weak var complantStatusLbl: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
