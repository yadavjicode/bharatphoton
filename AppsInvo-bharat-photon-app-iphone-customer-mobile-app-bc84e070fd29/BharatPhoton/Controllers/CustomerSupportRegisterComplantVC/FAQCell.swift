//
//  FAQCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 12/12/24.
//

import UIKit

class FAQCell: UITableViewCell {
    @IBOutlet weak var arrowBtn: UIButton!
    @IBOutlet weak var questionLbl: UILabel!
    @IBOutlet weak var answerLbl: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
       
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
}
