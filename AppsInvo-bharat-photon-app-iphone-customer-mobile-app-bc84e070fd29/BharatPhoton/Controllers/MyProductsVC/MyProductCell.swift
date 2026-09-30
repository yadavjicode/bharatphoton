//
//  MyProductCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/07/24.
//

import UIKit

class MyProductCell: UITableViewCell {
    @IBOutlet weak var arrowBtn: UIButton!
    @IBOutlet weak var stack2: UIStackView!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var productNameLbl: UILabel!
    @IBOutlet weak var costSavedLbl: UILabel!
    @IBOutlet weak var waterSavedLbl: UILabel!
    @IBOutlet weak var modalNoLbl: UILabel!
    @IBOutlet weak var orderIdLbl: UILabel!
    @IBOutlet weak var waterSavedCostSavedView: UIView!
    @IBOutlet weak var warrantyPlanExpViw: UIView!
    @IBOutlet weak var warrantyPlanExpLbl: UILabel!
    @IBOutlet weak var modalNameView: UIView!
    override func awakeFromNib() {
        super.awakeFromNib()
      
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
