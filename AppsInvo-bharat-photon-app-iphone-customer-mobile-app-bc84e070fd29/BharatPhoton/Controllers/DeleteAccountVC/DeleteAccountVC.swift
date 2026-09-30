//
//  DeleteAccountVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit
 
protocol DeleteAccountPopupDelegate: AnyObject {
    func didConfirmDeleteAccount()
}
 
class DeleteAccountVC: UIViewController {
    @IBOutlet weak var popupView: UIView!
    private let maxPopupHeight: CGFloat = 500
    weak var delegate: DeleteAccountPopupDelegate?
   
    override func viewDidLoad() {
        super.viewDidLoad()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
        setupSlidingPopup(for: popupView, maxHeight: maxPopupHeight)
    }
    @IBAction func hidePopViewAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
       
    }
    @IBAction func noGoBackAction(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    @IBAction func deleteBtnActon(_ sender: UIButton) {
        serviceDeleteAccountAPI()
    }
}
extension UIViewController {
    
    func setupSlidingPopup(for popupView: UIView, maxHeight: CGFloat) {
        guard let popupHeightConstraint = popupView.constraints.first(where: { $0.firstAttribute == .height }) else {
            fatalError("Height constraint for popupView is missing.")
        }
        
        // Initialize with max height
        popupHeightConstraint.constant = maxHeight
        
        // Add gesture recognizer
        let panGesture = UIPanGestureRecognizer(target: self, action: #selector(handlePopupPan(_:)))
        popupView.addGestureRecognizer(panGesture)
        
        // Store properties
        popupView.tag = 1001 // Unique identifier for popupView
        objc_setAssociatedObject(self, &AssociatedKeys.popupHeightConstraint, popupHeightConstraint, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        objc_setAssociatedObject(self, &AssociatedKeys.maxPopupHeight, maxHeight, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
    }
    
    @objc private func handlePopupPan(_ gesture: UIPanGestureRecognizer) {
        guard
            let popupView = gesture.view,
            let popupHeightConstraint = objc_getAssociatedObject(self, &AssociatedKeys.popupHeightConstraint) as? NSLayoutConstraint,
            let maxPopupHeight = objc_getAssociatedObject(self, &AssociatedKeys.maxPopupHeight) as? CGFloat
        else { return }
        
        let translation = gesture.translation(in: view)
        let velocity = gesture.velocity(in: view)
        
        switch gesture.state {
        case .changed:
            // Adjust height as user drags
            let newHeight = max(0, min(maxPopupHeight, popupHeightConstraint.constant - translation.y))
            popupHeightConstraint.constant = newHeight
            gesture.setTranslation(.zero, in: view)
            
        case .ended:
            // Slide to target height based on velocity
            let targetHeight: CGFloat = velocity.y > 0 ? 0 : maxPopupHeight
            animatePopup(popupView, heightConstraint: popupHeightConstraint, toHeight: targetHeight)
            
        default:
            break
        }
    }
    private func animatePopup(_ popupView: UIView, heightConstraint: NSLayoutConstraint, toHeight height: CGFloat) {
        UIView.animate(withDuration: 0.3, delay: 0, options: .curveEaseOut, animations: {
            heightConstraint.constant = height
            self.view.layoutIfNeeded()
        }) { _ in
            if height == 0 {
                self.showPopupMenu()
            }
        }
    }
    private func showPopupMenu() {
        print("Popup collapsed. Handle additional actions here.")
        self.dismiss(animated: false, completion: nil)
        
        
    }
}
// MARK: - Associated Keys
private struct AssociatedKeys {
    static var popupHeightConstraint = "popupHeightConstraint"
    static var maxPopupHeight = "maxPopupHeight"
}
extension DeleteAccountVC {
    func serviceDeleteAccountAPI(){
        LoaderManager.shared.showLoader(in: self.view)
        let parms = ["isDeleted": true] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .putJSON, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "deleteAccount", params: parms, token: token) { JSONResponse in
            LoaderManager.shared.hideLoader()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
               // self.alert(message: JSONResponse["message"].stringValue)
                self.delegate?.didConfirmDeleteAccount()
                self.dismiss(animated: false, completion: nil)
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            LoaderManager.shared.hideLoader()
            self.alert(message: JSONResponse["message"].stringValue, title: Error)
         }
    }
}
