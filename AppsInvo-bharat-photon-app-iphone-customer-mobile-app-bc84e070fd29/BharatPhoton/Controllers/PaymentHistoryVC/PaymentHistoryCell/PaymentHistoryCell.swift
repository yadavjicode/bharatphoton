//
//  PaymentHistoryCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 02/07/24.
//

import UIKit

class PaymentHistoryCell: UITableViewCell {
    @IBOutlet weak var uuidLbl: UILabel!
    @IBOutlet weak var logLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var paymentStatusLbl: UILabel!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var invoiceDownloadBtn: UIButton!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
