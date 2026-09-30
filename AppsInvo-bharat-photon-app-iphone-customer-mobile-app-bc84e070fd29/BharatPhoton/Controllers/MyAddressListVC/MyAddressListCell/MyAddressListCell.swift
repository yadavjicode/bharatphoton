//
//  MyAddressListCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 26/06/24.
//

import UIKit

class MyAddressListCell: UITableViewCell {
    
    @IBOutlet weak var selectedImg: UIImageView!
    @IBOutlet weak var selectedView: UIView!
    @IBOutlet weak var lbl: UILabel!
    @IBOutlet weak var addressLbl: UILabel!
    @IBOutlet weak var mobileNoLbl: UILabel!
    @IBOutlet weak var editDeletePopUpView: UIView!
    @IBOutlet weak var sideMenuBtn: UIButton!
    @IBOutlet weak var editBtn: UIButton!
    @IBOutlet weak var deleteBtn: UIButton!
    override func awakeFromNib() {
        super.awakeFromNib()
        editDeletePopUpView.isHidden = true
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
