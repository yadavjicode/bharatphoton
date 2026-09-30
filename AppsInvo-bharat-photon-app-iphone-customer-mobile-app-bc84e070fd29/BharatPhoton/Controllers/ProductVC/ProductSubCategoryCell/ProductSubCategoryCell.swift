//
//  ProductSubCategoryCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit

class ProductSubCategoryCell: UICollectionViewCell {
    @IBOutlet weak var selectedItemView: CustomDashedView!
    @IBOutlet weak var subCategoryImg: UIImageView!
    @IBOutlet weak var subCategoryNameLbl: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        selectedItemView.isHidden = true
        // Initialization code
    }

}
