//
//  ProductImageDetailsCell.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 10/02/25.
//

import UIKit

class ProductImageDetailsCell: UICollectionViewCell, UIScrollViewDelegate {
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var scrollView: UIScrollView!
    override func awakeFromNib() {
        super.awakeFromNib()
        scrollView.minimumZoomScale = 1.0
        scrollView.maximumZoomScale = 3.0
        scrollView.delegate = self
        let doubleTap = UITapGestureRecognizer(target: self, action: #selector(handleDoubleTap(_:)))
        doubleTap.numberOfTapsRequired = 2
        scrollView.addGestureRecognizer(doubleTap)
    }
    func viewForZooming(in scrollView: UIScrollView) -> UIView? {
        return productImg
    }
    @objc func handleDoubleTap(_ gesture: UITapGestureRecognizer) {
        if scrollView.zoomScale == 1.0 {
            scrollView.setZoomScale(2.5, animated: true)  // Zoom in
        } else {
            scrollView.setZoomScale(1.0, animated: true)  // Reset to normal
        }
    }
}
