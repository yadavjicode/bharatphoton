//
//  Untitled.swift
//  CustomLoader
//
//  Created by sangeetkamal on 25/03/25.
//

import UIKit


class CustomLoaderView: UIView {
    private let backgroundView = UIView()  // Full-screen background
    private let loaderContainer = UIView() // 80x80 static container
    private let loaderSize: CGFloat = 40
    private let circleLayer = CAShapeLayer()
    private let loaderView = UIView()      // Rotating loader inside
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupLoader()
    }
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupLoader()
    }
    private func setupLoader() {
        // **Full-Screen Background View**
        backgroundView.frame = UIScreen.main.bounds
        backgroundView.backgroundColor = UIColor.clear.withAlphaComponent(0.5)
        self.addSubview(backgroundView)
        // **Outer Static Loader Container (100x100)**
        loaderContainer.frame = CGRect(x: 0, y: 0, width: 80, height: 80)
        loaderContainer.center = backgroundView.center
        loaderContainer.backgroundColor = UIColor.white.withAlphaComponent(0.9)
        loaderContainer.layer.cornerRadius = 12
        self.addSubview(loaderContainer)
        // **Inner Rotating Loader (40x40)**
        loaderView.frame = CGRect(x: (loaderContainer.frame.width - loaderSize) / 2,y: (loaderContainer.frame.height - loaderSize) / 2, width: loaderSize, height: loaderSize)
        loaderView.backgroundColor = UIColor.clear
        loaderContainer.addSubview(loaderView) // Only this rotates
        // **Circular Path**
        let circularPath = UIBezierPath(arcCenter: CGPoint(x: loaderSize / 2, y: loaderSize / 2),radius: loaderSize / 2 - 4,startAngle: -CGFloat.pi / 2,endAngle: 3 * CGFloat.pi / 2,clockwise: true)
        circleLayer.path = circularPath.cgPath
        circleLayer.strokeColor = UIColor(hexString: "FF8D3A").cgColor
        circleLayer.fillColor = UIColor.clear.cgColor
        circleLayer.lineWidth = 3.5
        circleLayer.lineCap = .round
        circleLayer.strokeEnd = 0.7
        loaderView.layer.addSublayer(circleLayer) // Add only to loaderView
        startAnimating()
    }
    private func startAnimating() {
        let rotationAnimation = CABasicAnimation(keyPath: "transform.rotation")
        rotationAnimation.toValue = 2 * CGFloat.pi
        rotationAnimation.duration = 1
        rotationAnimation.repeatCount = .infinity
        loaderView.layer.add(rotationAnimation, forKey: "rotationAnimation") // Only rotates loaderView
    }
    func stopAnimating() {
        loaderView.layer.removeAllAnimations()
        self.removeFromSuperview()
    }
}
class LoaderManager {
    static let shared = LoaderManager()
    private var customLoader: CustomLoaderView?
    private init() {} // Prevent external instantiation
    func showLoader(in view: UIView) {
        DispatchQueue.main.async {
            self.customLoader?.removeFromSuperview() // Remove existing loader (if any)
            self.customLoader = CustomLoaderView(frame: UIScreen.main.bounds)
            if let loader = self.customLoader {
                view.addSubview(loader)
            }
        }
    }
    func hideLoader() {
        DispatchQueue.main.async {
            self.customLoader?.stopAnimating()
            self.customLoader = nil
        }
    }
}
