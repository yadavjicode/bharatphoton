//
//  SitePhotoSubmittedVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 25/06/24.
//

import UIKit

class SitePhotoSubmittedVC: UIViewController {

    @IBOutlet weak var img: UIImageView!
    
    var siteImages = [[String: Any]]()
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        if let firstImage = siteImages.first, let image = firstImage["url"] as? String {
            // Do something with the image
            print("First image URL: \(image)")
            self.img.sd_setImage(with: URL(string: image), placeholderImage: UIImage(named: "profilePlaceholder"))
        } else {
            print("No images found.")
        }

    }
    @IBAction func backBtnAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func backHomeAction(_ sender: UIButton) {
        let vc = self.storyboard?.instantiateViewController(identifier: "TabVC") as? TabVC
        self.navigationController?.pushViewController(vc!, animated: false)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + 0.2) {
            vc?.selectedIndex = 0
        }
    }
}
