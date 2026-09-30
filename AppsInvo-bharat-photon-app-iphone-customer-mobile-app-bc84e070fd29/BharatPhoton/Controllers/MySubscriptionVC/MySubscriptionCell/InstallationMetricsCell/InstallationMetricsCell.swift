//
//  InstallationMetricsCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 21/02/25.
//

import UIKit

class InstallationMetricsCell: UITableViewCell {
    @IBOutlet weak var registerComplantBtn: UIButton!
    @IBOutlet weak var renewPlanBtn: UIButton!
    @IBOutlet weak var renewPlanLbl: UILabel!
    @IBOutlet weak var subscriptionStartDateLbl: UILabel!
    @IBOutlet weak var subscriptionExpiryDateLbl: UILabel!
    @IBOutlet weak var sinNoLbl: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
 
    }
    
}
