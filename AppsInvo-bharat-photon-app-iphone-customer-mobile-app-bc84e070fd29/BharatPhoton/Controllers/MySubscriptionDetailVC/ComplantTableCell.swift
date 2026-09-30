//
//  ComplantTableCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 16/01/25.
//

import UIKit

class ComplantTableCell: UITableViewCell {
    @IBOutlet weak var complantNoLbl: UILabel!
    @IBOutlet weak var complantTypeLbl: UILabel!
    @IBOutlet weak var complantStatusLbl: UILabel!
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var attendedByLbl: UILabel!
    @IBOutlet weak var paymentModeLbl: UILabel!
    @IBOutlet weak var resolveDateLbl: UILabel!
    @IBOutlet weak var resolveActionTokenLbl: UILabel!
    @IBOutlet weak var dayLbl: UILabel!
    @IBOutlet weak var representativeNameLbl: UILabel!
    @IBOutlet weak var remarkLbl: UILabel!
    @IBOutlet weak var warrantyExpLbl: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
