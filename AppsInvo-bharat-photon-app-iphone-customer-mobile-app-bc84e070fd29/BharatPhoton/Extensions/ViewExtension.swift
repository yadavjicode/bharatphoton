//
//  ViewExtension.swift
//  Doctor
//
//  Created by Apple on 13/02/24.
//

import Foundation
import UIKit
let kSharedAppDelegate = UIApplication.shared.delegate as? AppDelegate
extension UIView {
    
    //Setting Corner Radius
    @IBInspectable
    var cornerRadius : CGFloat {
        
        get{
          return self.layer.cornerRadius
        }
        
        set(newValue) {
            self.layer.cornerRadius = newValue
        }
        
    }
    //MARK:- Setting bottom Line
    @IBInspectable
    var borderLineWidth : CGFloat {
        get {
            return self.layer.borderWidth
        }
        set(newValue) {
            self.layer.borderWidth = newValue
        }
    }
    //MARK:- Setting border color
    @IBInspectable
    var borderColor : UIColor {
        
        get {
            
            return UIColor(cgColor: self.layer.borderColor ?? UIColor.clear.cgColor)
        }
        set(newValue) {
            self.layer.borderColor = newValue.cgColor
        }
        
    }
    //MARK:- Shadow Offset
    @IBInspectable
    var offsetShadow : CGSize {
        
        get {
           return self.layer.shadowOffset
        }
        set(newValue) {
            self.layer.shadowOffset = newValue
        }
    }
    //MARK:- Shadow Opacity
    @IBInspectable
    var opacityShadow : Float {
        
        get{
            return self.layer.shadowOpacity
        }
        set(newValue) {
            self.layer.shadowOpacity = newValue
        }
    }
    //MARK:- Shadow Color
    @IBInspectable
    var colorShadow : UIColor? {
        
        get{
           return UIColor(cgColor: self.layer.shadowColor ?? UIColor.clear.cgColor)
        }
        set(newValue) {
            self.layer.shadowColor = newValue?.cgColor
        }
    }
    
    //MARK:- Shadow Radius
    @IBInspectable
    var radiusShadow : CGFloat {
        get {
             return self.layer.shadowRadius
        }
        set(newValue) {
            
           self.layer.shadowRadius = newValue
        }
    }
    
    //MARK:- Mask To Bounds
    
    @IBInspectable
    var maskToBounds : Bool {
        get {
            return self.layer.masksToBounds
        }
        set(newValue) {
            
            self.layer.masksToBounds = newValue
        }
    }
    
    //MARK:- Make View Round
    
    func makeRoundedCorner(){
        self.layer.masksToBounds = true
        self.layer.cornerRadius = self.bounds.width/2
    }
}
extension UIButton {
    
    @IBInspectable
    var cornerRadiusBtn : CGFloat {
        
        get{
          return self.layer.cornerRadius
        }
        
        set(newValue) {
            self.layer.cornerRadius = newValue
        }
    }
    //MARK:- Setting bottom Line
    @IBInspectable
    var borderLineWidthBtn : CGFloat {
        get {
            return self.layer.borderWidth
        }
        set(newValue) {
            self.layer.borderWidth = newValue
        }
    }
    //MARK:- Setting border color
    @IBInspectable
    var borderColorBtn : UIColor {
        get {
            
            return UIColor(cgColor: self.layer.borderColor ?? UIColor.clear.cgColor)
        }
        set(newValue) {
            self.layer.borderColor = newValue.cgColor
        }
        
    }
}
extension UITextField {
    
    @IBInspectable
    var cornerRadiusTF : CGFloat {
        
        get{
          return self.layer.cornerRadius
        }
        
        set(newValue) {
            self.layer.cornerRadius = newValue
        }

    }
    //MARK:- Setting bottom Line
    @IBInspectable
    var borderLineWidthTF : CGFloat {
        get {
            return self.layer.borderWidth
        }
        set(newValue) {
            self.layer.borderWidth = newValue
        }
    }
    //MARK:- Setting border color
    @IBInspectable
    var borderColorTF : UIColor {
        
        get {
            
            return UIColor(cgColor: self.layer.borderColor ?? UIColor.clear.cgColor)
        }
        set(newValue) {
            self.layer.borderColor = newValue.cgColor
        }
        
    }
}


