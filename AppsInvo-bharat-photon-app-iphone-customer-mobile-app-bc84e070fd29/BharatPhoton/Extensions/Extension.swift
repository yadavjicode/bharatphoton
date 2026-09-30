//
//  Extension.swift
//  Doctor
//
//  Created by Apple on 13/02/24.
//

import Foundation
import UIKit
fileprivate var aview: UIView?
fileprivate var animationView: UIView?
let loader = UIActivityIndicatorView(style: .medium)
extension UIView {
    func addDashedLine(strokeColor: UIColor, lineWidth: CGFloat) {
        
        backgroundColor = .clear
        let shapeLayer = CAShapeLayer()
        shapeLayer.name = "DashedTopLine"
        shapeLayer.bounds = bounds
        shapeLayer.position = CGPoint(x: frame.width / 2, y: frame.height / 2)
        shapeLayer.fillColor = UIColor.clear.cgColor
        shapeLayer.strokeColor = strokeColor.cgColor
        shapeLayer.lineWidth = lineWidth
        shapeLayer.lineJoin = CAShapeLayerLineJoin.round
        shapeLayer.lineDashPattern = [1, 1]
        
        let path = CGMutablePath()
        path.move(to: CGPoint.zero)
        path.addLine(to: CGPoint(x: 0, y: frame.height))
        shapeLayer.path = path
        
        layer.addSublayer(shapeLayer)
    }
}
// Segment Control
extension UISegmentedControl{
    func removeBorder(){
        let backgroundImage = UIImage.getColoredRectImageWith(color: UIColor.clear.cgColor, andSize: self.bounds.size)
        self.setBackgroundImage(backgroundImage, for: .normal, barMetrics: .default)
        self.setBackgroundImage(backgroundImage, for: .selected, barMetrics: .default)
        
        _ = UIImage.getColoredRectImageWith(color: UIColor.white.cgColor, andSize: CGSize(width: 1.0, height: self.bounds.size.height))
        
    }
    func addUnderlineForSelectedSegment(){
        removeBorder()
        let underlineWidth: CGFloat = self.bounds.size.width / CGFloat(self.numberOfSegments)
        let underlineHeight: CGFloat = 3.0
        let underlineXPosition = CGFloat(selectedSegmentIndex * Int(underlineWidth))
        let underLineYPosition = self.bounds.size.height - 5.0
        let underlineFrame = CGRect(x: underlineXPosition, y: underLineYPosition, width: underlineWidth, height: underlineHeight)
        let underline = UIView(frame: underlineFrame)
        underline.backgroundColor = UIColor(red: 2/255, green: 114/255, blue: 68/255, alpha: 1.0)
        underline.tag = 1
        self.addSubview(underline)
    }
    func changeUnderlinePosition(){
        guard let underline = self.viewWithTag(1) else {return}
        let underlineFinalXPosition = (self.bounds.width / CGFloat(self.numberOfSegments)) * CGFloat(selectedSegmentIndex)
        UIView.animate(withDuration: 0.1, animations: {
            underline.frame.origin.x = underlineFinalXPosition
        })
    }
}
extension UIImage{
    class func getColoredRectImageWith(color: CGColor, andSize size: CGSize) -> UIImage{
        UIGraphicsBeginImageContextWithOptions(size, false, 0.0)
        let graphicsContext = UIGraphicsGetCurrentContext()
        graphicsContext?.setFillColor(color)
        let rectangle = CGRect(x: 0.0, y: 0.0, width: size.width, height: size.height)
        graphicsContext?.fill(rectangle)
        let rectangleImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return rectangleImage!
    }
}
extension UITextField {
    @IBInspectable var placeholderColor: UIColor {
        get {
            return attributedPlaceholder?.attribute(.foregroundColor, at: 0, effectiveRange: nil) as? UIColor ?? .clear
        }
        set {
            guard let attributedPlaceholder = attributedPlaceholder else { return }
            let attributes: [NSAttributedString.Key: UIColor] = [.foregroundColor: newValue]
            self.attributedPlaceholder = NSAttributedString(string: attributedPlaceholder.string, attributes: attributes)
        }
    }
}
@available(iOS 13.0, *)
extension UIViewController {
    var appDelegate: AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }
    static var activityIndicatorTag = 12345
    // MARK: startLoading
    func startLoading() {
        aview = UIView(frame: self.view.bounds)
        aview!.backgroundColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 0.25)
        loader.center = aview!.center
        loader.color = .gray
        loader.startAnimating()
        aview!.addSubview(loader)
        UIView.transition(with: self.view, duration: 0.25, options: [.transitionCrossDissolve], animations: {
            self.view.addSubview(aview!)
        }, completion: nil)
    }
    
    // MARK:stopLoading
    func stopLoading() {
        UIView.transition(with: self.view, duration: 0.25, options: [.transitionCrossDissolve], animations: {
            aview?.removeFromSuperview()
        }, completion: nil)
        loader.stopAnimating()
        aview = nil
    }
}
extension UITextField {
    
    func setInputViewDatePicker(target: Any, selector: Selector) {
        // Create a UIDatePicker object and assign to inputView
        let screenWidth = UIScreen.main.bounds.width
        let datePicker = UIDatePicker(frame: CGRect(x: 0, y: 0, width: screenWidth, height: 216))//1
        datePicker.datePickerMode = .date //2
        datePicker.maximumDate = Calendar.current.date(byAdding: .nanosecond, value: 0, to: Date())
        // iOS 14 and above
        if #available(iOS 14, *) {// Added condition for iOS 14
            datePicker.preferredDatePickerStyle = .wheels
            datePicker.sizeToFit()
        }
        self.inputView = datePicker //3
        
        // Create a toolbar and assign it to inputAccessoryView
        let toolBar = UIToolbar(frame: CGRect(x: 0.0, y: 0.0, width: screenWidth, height: 44.0)) //4
        let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil) //5
        let cancel = UIBarButtonItem(title: "Cancel", style: .plain, target: nil, action: #selector(tapCancel)) // 6
        let barButton = UIBarButtonItem(title: "Done", style: .plain, target: target, action: selector) //7
        toolBar.setItems([cancel, flexible, barButton], animated: false) //8
        self.inputAccessoryView = toolBar //9
    }
    func setInputViewTimePicker(target: Any, selector: Selector) {
        // Create a UIDatePicker object and assign to inputView
        let screenWidth = UIScreen.main.bounds.width
        let datePicker = UIDatePicker(frame: CGRect(x: 0, y: 0, width: screenWidth, height: 216))//1
        datePicker.datePickerMode = .time //2
        // iOS 14 and above
        if #available(iOS 14, *) {// Added condition for iOS 14
            datePicker.preferredDatePickerStyle = .wheels
            datePicker.sizeToFit()
        }
        self.inputView = datePicker //3
        
        // Create a toolbar and assign it to inputAccessoryView
        let toolBar = UIToolbar(frame: CGRect(x: 0.0, y: 0.0, width: screenWidth, height: 44.0)) //4
        let flexible = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil) //5
        let cancel = UIBarButtonItem(title: "Cancel", style: .plain, target: nil, action: #selector(tapCancel)) // 6
        let barButton = UIBarButtonItem(title: "Done", style: .plain, target: target, action: selector) //7
        toolBar.setItems([cancel, flexible, barButton], animated: false) //8
        self.inputAccessoryView = toolBar //9
    }
    @objc func tapCancel() {
        self.resignFirstResponder()
    }
}
extension UIViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}
extension UIView {
    /// Round
    func rounded() {
        circularEdges()
        customizeBorderColor(color: UIColor.clear)
    }
    /// Round edge
    func roundedEdges() {
        layer.cornerRadius = 5
        clipsToBounds = true
    }
    /// Circle edge
    func circularEdges() {
        layer.cornerRadius = self.frame.height / 2
        clipsToBounds = true
    }
    /// Custom border color
    func customizeBorderColor(color: UIColor) {
        layer.borderWidth = 2
        layer.borderColor = color.cgColor
    }
    /// Custom border color radius
    func customizeBorderColorRadius(color: UIColor, radius: CGFloat, borderWidth: CGFloat = 1) {
        layer.cornerRadius = radius
        layer.borderWidth = borderWidth
        layer.borderColor = color.cgColor
    }
    /// Round corners
    func roundCorners(corners: UIRectCorner, radius: CGFloat) {
        let path = UIBezierPath(roundedRect: bounds, byRoundingCorners: corners, cornerRadii: CGSize(width: radius, height: radius))
        let mask = CAShapeLayer()
        mask.path = path.cgPath
        layer.mask = mask
    }
    /// Drop a shadow
    func dropShadow(shadowOffset: CGSize = CGSize(width: 0, height: 1.0),
                    shadowRadius: CGFloat = 1.0,
                    shadowOpacity: Float = 0.5) {
        layer.shadowColor = UIColor.gray.cgColor
        layer.shadowOffset = shadowOffset
        layer.shadowRadius = shadowRadius
        layer.shadowOpacity = shadowOpacity
        layer.masksToBounds = false
    }
    /// Dashed border
    func addDashedBorder() {
        let color = UIColor.white
        
        let shapeLayer: CAShapeLayer = CAShapeLayer()
        let frameSize = self.frame.size
        let shapeRect = CGRect(x: 0, y: 0, width: frameSize.width, height: frameSize.height)
        
        shapeLayer.bounds = shapeRect
        shapeLayer.position = CGPoint(x: frameSize.width/2, y: frameSize.height/2)
        shapeLayer.fillColor = UIColor.clear.cgColor
        shapeLayer.strokeColor = color.cgColor
        shapeLayer.lineWidth = 1
        shapeLayer.lineJoin = CAShapeLayerLineJoin.round
        shapeLayer.lineDashPattern = [6,3]
        shapeLayer.path = UIBezierPath(roundedRect: shapeRect, cornerRadius: 5).cgPath
        
        self.layer.masksToBounds = true
        self.layer.addSublayer(shapeLayer)
    }
    func dashVerticalLine(){
        let lineLayer = CAShapeLayer()
        let frameSize = self.frame.size
        lineLayer.strokeColor = UIColor.gray.cgColor
        lineLayer.lineWidth = 2
        lineLayer.lineDashPattern = [4,4]
        let path = CGMutablePath()
        path.addLines(between: [CGPoint(x: 0, y: 0),
                                CGPoint(x: 0, y: frameSize.height)])
        lineLayer.path = path
        self.layer.addSublayer(lineLayer)
    }
    /// Single dashed line
    func singleDashedLine(start point: CGPoint = CGPoint(x: 0, y: 0),
                          width value: CGFloat,
                          end pointY: CGPoint = CGPoint(x: 0, y: 0),
                          color: UIColor = .white) {
        let shapeLayer = CAShapeLayer()
        shapeLayer.strokeColor = color.cgColor
        shapeLayer.lineWidth = 1
        /// 5 is the length of dash, 3 is length of the gap.
        shapeLayer.lineDashPattern = [5, 3]
        
        let path = CGMutablePath()
        let widthPoint = CGPoint(x: value, y: pointY.y)
        
        path.addLines(between: [point, widthPoint])
        shapeLayer.path = path
        self.layer.addSublayer(shapeLayer)
    }
    /// Shake animation
    func shake(delegate: CAAnimationDelegate) {
        let animationKeyPath = "transform.translation.x"
        let shakeAnimation = "shake"
        let duration = 0.6
        let animation = CAKeyframeAnimation(keyPath: animationKeyPath)
        animation.timingFunction = CAMediaTimingFunction(name: CAMediaTimingFunctionName.linear)
        animation.duration = duration
        animation.values = [-20.0, 20.0, -20.0, 20.0, -10.0, 10.0, -5.0, 5.0, 0.0]
        animation.delegate = delegate
        layer.add(animation, forKey: shakeAnimation)
    }
    @discardableResult
    func fromNib<T : UIView>() -> T? {
        guard let contentView = Bundle.main.loadNibNamed(String(describing: type(of: self)), owner: self, options: nil)?.first as? T else {
            return nil
        }
        self.addSubview(contentView)
        contentView.translatesAutoresizingMaskIntoConstraints = false
        contentView.layoutAttachAll()
        return contentView
    }
    /// attaches all sides of the receiver to its parent view
    func layoutAttachAll(margin : CGFloat = 0.0) {
        let view = superview
        layoutAttachTop(to: view, margin: margin)
        layoutAttachBottom(to: view, margin: margin)
        layoutAttachLeading(to: view, margin: margin)
        layoutAttachTrailing(to: view, margin: margin)
    }
    /// attaches the top of the current view to the given view's top if it's a superview of the current view, or to it's bottom if it's not (assuming this is then a sibling view).
    /// if view is not provided, the current view's super view is used
    @discardableResult
    func layoutAttachTop(to: UIView? = nil, margin : CGFloat = 0.0) -> NSLayoutConstraint {
        
        let view: UIView? = to ?? superview
        let isSuperview = view == superview
        let constraint = NSLayoutConstraint(item: self, attribute: .top, relatedBy: .equal, toItem: view, attribute: isSuperview ? .top : .bottom, multiplier: 1.0, constant: margin)
        superview?.addConstraint(constraint)
        return constraint
    }
    /// attaches the bottom of the current view to the given view
    @discardableResult
    func layoutAttachBottom(to: UIView? = nil, margin : CGFloat = 0.0, priority: UILayoutPriority? = nil) -> NSLayoutConstraint {
        
        let view: UIView? = to ?? superview
        let isSuperview = (view == superview) || false
        let constraint = NSLayoutConstraint(item: self, attribute: .bottom, relatedBy: .equal, toItem: view, attribute: isSuperview ? .bottom : .top, multiplier: 1.0, constant: -margin)
        if let priority = priority {
            constraint.priority = priority
        }
        superview?.addConstraint(constraint)
        
        return constraint
    }
    /// attaches the leading edge of the current view to the given view
    @discardableResult
    func layoutAttachLeading(to: UIView? = nil, margin : CGFloat = 0.0) -> NSLayoutConstraint {
        
        let view: UIView? = to ?? superview
        let isSuperview = (view == superview) || false
        let constraint = NSLayoutConstraint(item: self, attribute: .leading, relatedBy: .equal, toItem: view, attribute: isSuperview ? .leading : .trailing, multiplier: 1.0, constant: margin)
        superview?.addConstraint(constraint)
        return constraint
    }
    /// attaches the trailing edge of the current view to the given view
    @discardableResult
    func layoutAttachTrailing(to: UIView? = nil, margin : CGFloat = 0.0, priority: UILayoutPriority? = nil) -> NSLayoutConstraint {
        
        let view: UIView? = to ?? superview
        let isSuperview = (view == superview) || false
        let constraint = NSLayoutConstraint(item: self, attribute: .trailing, relatedBy: .equal, toItem: view, attribute: isSuperview ? .trailing : .leading, multiplier: 1.0, constant: -margin)
        if let priority = priority {
            constraint.priority = priority
        }
        superview?.addConstraint(constraint)
        
        return constraint
    }
}
extension UITextField{
    func bottomBorder(color: UIColor, ht: CGFloat){
        let bottomLine = CALayer()
        bottomLine.frame = CGRect(x: 0.0, y: self.frame.height - 1, width: self.frame.width, height: ht)
        bottomLine.backgroundColor = color.cgColor
        self.borderStyle = UITextField.BorderStyle.none
        self.layer.addSublayer(bottomLine)
    }
}
extension UIImageView {
    func setImageColor(color: UIColor) {
        let templateImage = self.image?.withRenderingMode(.alwaysTemplate)
        self.image = templateImage
        self.tintColor = color
    }
}
extension NSMutableAttributedString {
    func text(_ value: String, color: UIColor = .black, font: UIFont, alignment: NSTextAlignment = .left) -> NSMutableAttributedString {
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = alignment
        let attributes:[NSAttributedString.Key: Any] = [
            .font: font,
            .foregroundColor: color,
            .paragraphStyle: paragraphStyle
        ]
        self.append(NSAttributedString(string: value, attributes: attributes))
        return self
    }
    func underlined(_ value: String, color: UIColor = .black, font: UIFont) -> NSMutableAttributedString {
        let attributes: [NSAttributedString.Key: Any] = [
            .font: font,
            .foregroundColor: color,
            .underlineStyle: (NSUnderlineStyle.thick.rawValue | NSUnderlineStyle.patternDot.rawValue)
        ]
        self.append(NSAttributedString(string: value, attributes: attributes))
        return self
    }
    func strikeThrough(value: String)->NSMutableAttributedString{
        let attributeString: NSMutableAttributedString =  NSMutableAttributedString(string: value)
        attributeString.addAttribute(NSAttributedString.Key.strikethroughStyle, value: NSUnderlineStyle.single.rawValue, range: NSMakeRange(0, attributeString.length))
        return attributeString
    }
}
extension String {
    func isValidEmail() -> Bool {
        // here, `try!` will always succeed because the pattern is valid
        let regex = try! NSRegularExpression(pattern: "[a-zA-Z0-9+._%\\-]{1,256}" + "@" + "[a-zA-Z][a-zA-Z\\-]{0,64}" + "(" + "\\." + "[a-zA-Z][a-zA-Z\\-]{0,25}" + ")+",options:.caseInsensitive)
        return regex.firstMatch(in: self,options:[],range:NSRange(location:0,length:count)) != nil
    }
    func isValidPassword() -> Bool {
        let password = self.trimmingCharacters(in: CharacterSet.whitespaces)
        let passwordRegx = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&<>*~:`-]).{8,}$"
        let passwordCheck = NSPredicate(format: "SELF MATCHES %@",passwordRegx)
        return passwordCheck.evaluate(with: password)
    }
    //Method is used for validation of phone number
    func isValidPhone(phone: String) -> Bool {
        let phoneRegex = "^[0-9+]{0,1}+[0-9]{9}$"
        let phoneTest = NSPredicate(format: "SELF MATCHES %@", phoneRegex)
        return phoneTest.evaluate(with: phone)
    }
}
extension UIViewController
{
    func borderwidth(newView:UIView,borderWidth:CGFloat,borderColor:UIColor,cornerRadius:CGFloat)
    {
        newView.layer.borderColor = borderColor.cgColor
        newView.layer.borderWidth = borderWidth
        newView.clipsToBounds = true
        newView.layer.cornerRadius = cornerRadius
    }
    func alert(message:String,title:String = "")  {
        let alertController = UIAlertController(title: title, message: message,preferredStyle: .alert)
        let OkAction = UIAlertAction(title: "Ok", style: .default, handler: nil)
        alertController.addAction(OkAction)
        self.present(alertController, animated: true, completion: nil)
    }
}
// MARK: # color code
extension UIColor {
    convenience init(hexString: String) {
        let hex = hexString.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int = UInt64()
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    }
}
extension UIViewController {
    func statusBarColor()
    {
        let statusBarView = UIView(frame: UIApplication.shared.statusBarFrame)
        let statusBarColor = UIColor.white
        statusBarView.backgroundColor = statusBarColor
        view.addSubview(statusBarView)
    }
}
class HeaderView: UICollectionReusableView {
    // Create a label to display the section title
    let titleLabel: UILabel = {
        let label = UILabel()
        label.translatesAutoresizingMaskIntoConstraints = false
        label.font = UIFont.boldSystemFont(ofSize: 16)
        label.textColor = .black
        return label
    }()
    // Create a button for "View All"
    let viewAllButton: UIButton = {
        let button = UIButton(type: .system)  // You can change the style as needed
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitle("View All", for: .normal)
        button.setTitleColor(UIColor.init(hexString: "FF8D3A"), for: .normal)
        button.titleLabel?.font = UIFont.boldSystemFont(ofSize: 15)
        return button
    }()
    override init(frame: CGRect) {
        super.init(frame: frame)
        // Add titleLabel and viewAllButton to the view hierarchy
        addSubview(titleLabel)
        addSubview(viewAllButton)
        // Set constraints for titleLabel (left-aligned)
        NSLayoutConstraint.activate([
            titleLabel.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: 16),
            titleLabel.centerYAnchor.constraint(equalTo: self.centerYAnchor)
        ])
        // Set constraints for viewAllButton (right-aligned)
        NSLayoutConstraint.activate([
            viewAllButton.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -16),
            viewAllButton.centerYAnchor.constraint(equalTo: self.centerYAnchor)
        ])
    }
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
}
class AddToCartAnimation: UIView {
    func animateAddToCart(imageView: UIImageView, cartButton: UIButton, completion: (() -> Void)? = nil) {
        // Step 1: Create a copy of the image view
        let imageCopy = UIImageView(image: imageView.image)
        imageCopy.frame = imageView.frame
        imageCopy.contentMode = .scaleAspectFit
        imageCopy.clipsToBounds = true
        imageCopy.layer.masksToBounds = false
        // Add the image copy to the main view
        self.addSubview(imageCopy)
        // Step 2: Calculate the position of the cart button
        let cartButtonPosition = cartButton.convert(cartButton.bounds, to: self)
        // Step 3: Set the initial position of the image copy
        imageCopy.center = imageView.center
        // Step 4: Animate the image copy to the cart button
        UIView.animate(withDuration: 0.5, animations: {
            imageCopy.center = CGPoint(x: cartButtonPosition.midX, y: cartButtonPosition.midY)
            imageCopy.transform = CGAffineTransform(scaleX: 0.1, y: 0.1)
            imageCopy.alpha = 0 // Fade out effect
        }) { _ in
            // Step 5: Remove the image copy from the superview after the animation
            imageCopy.removeFromSuperview()
            // Call the completion handler if provided
            completion?()
        }
    }
}
// MARK: // Desired output format: 02 Friday, May 2025
extension String {
    func dateAndDay() -> String? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"  // Input format
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        if let date = dateFormatter.date(from: self) {
            dateFormatter.dateFormat = "dd EEEE, MMMM yyyy"  // Desired output: 01 Monday, June 2025
            return dateFormatter.string(from: date)
        }
        return nil
    }
}
// MARK: // Desired output format: 16/04/25
extension String {
    func Date() -> String? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"  // Format of the input date string
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        
        if let date = dateFormatter.date(from: self) {
            dateFormatter.dateFormat = "dd/MM/yy"  // Desired output format: 16/04/25
            return dateFormatter.string(from: date)
        }
        return nil
    }
}
// MARK: // Desired output format: 01 Apr, 2025
extension String {
    func toDisplayDate() -> String? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"  // Format of the input date string
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        if let date = dateFormatter.date(from: self) {
            dateFormatter.dateFormat = "dd MMM, yyyy"  // Desired display format
            return dateFormatter.string(from: date)
        }
        return nil
    }
}
// MARK: // Desired output format: 16/04/2025
extension String {
    func date() -> String? {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"  // Format of the input date string
        dateFormatter.locale = Locale(identifier: "en_US_POSIX")
        
        if let date = dateFormatter.date(from: self) {
            dateFormatter.dateFormat = "dd/MM/yyyy"
            return dateFormatter.string(from: date)
        }
        return nil
    }
}
// MARK: // Desired output format: 16/04/2025 01:00 PM 
extension String {
    func dateAndTime() -> String? {
        let inputDateFormatter = DateFormatter()
        inputDateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ" // Input format for the ISO 8601 date string
        inputDateFormatter.locale = Locale(identifier: "en_US_POSIX")
        guard let date = inputDateFormatter.date(from: self) else {
            return nil // Return nil if the input string cannot be parsed
        }
        let outputDateFormatter = DateFormatter()
        outputDateFormatter.dateFormat = "dd/MM/yyyy hh:mm a" // Desired output format
        outputDateFormatter.locale = Locale.current // Use the user's locale for output
        return outputDateFormatter.string(from: date)
    }
}
// MARK: Date And time convert Readable format ==>
extension String {
    func toDisplayDateAndTime() -> String? {
        let inputDateFormatter = DateFormatter()
        inputDateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ" // Input format for the ISO 8601 date string
        inputDateFormatter.locale = Locale(identifier: "en_US_POSIX")
        guard let date = inputDateFormatter.date(from: self) else {
            return nil // Return nil if the input string cannot be parsed
        }
        let outputDateFormatter = DateFormatter()
        outputDateFormatter.dateFormat = "dd MMM, yyyy hh:mm a" // Desired output format
        outputDateFormatter.locale = Locale.current // Use the user's locale for output
        return outputDateFormatter.string(from: date)
    }
}
extension String {
    // Method to convert the date string to a formatted time string
    func toFormattedTimeString() -> String? {
        // 1. Create DateFormatter to parse the input date string
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssZ" // Format matching the input string
        
        // 2. Convert the string to Date
        if let date = inputFormatter.date(from: self) {
            
            // 3. Create another DateFormatter to display only the time in desired format
            let outputFormatter = DateFormatter()
            outputFormatter.dateFormat = "h:mm a" // 12-hour format with AM/PM
            
            // 4. Format the Date object to string (time only)
            return outputFormatter.string(from: date)
        }
        
        // Return nil if the date format is invalid
        return nil
    }
}
// MARK: Download PDF Invoice ==>
extension UIViewController {
    func downloadInvoice(invoiceUrl: String) {
        self.startLoading() // Start loading indicator
        
        guard let url = URL(string: invoiceUrl) else {
            print("Invalid URL")
            self.stopLoading()
            self.alert(message: "File not found.", title: "Error")
            return
        }
        
        let task = URLSession.shared.downloadTask(with: url) { (location, response, error) in
            guard let location = location, error == nil else {
                DispatchQueue.main.async {
                    self.stopLoading()
                    print("Download error: \(String(describing: error))")
                    self.alert(message: "Failed to download", title: "Error")
                }
                return
            }
            
            // Get the destination path in the app's Documents directory
            let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
            let destinationURL = documentsDirectory?.appendingPathComponent(url.lastPathComponent)
            
            do {
                if let destinationURL = destinationURL {
                    // If a file already exists, remove it
                    if FileManager.default.fileExists(atPath: destinationURL.path) {
                        try FileManager.default.removeItem(at: destinationURL)
                    }
                    
                    // Move the file from temp location to Documents
                    try FileManager.default.moveItem(at: location, to: destinationURL)
                    print("File downloaded to: \(destinationURL.path)")
                    
                    DispatchQueue.main.async {
                        self.stopLoading()
                        self.alert(message: "Downloaded successfully.", title: "Success")
                        
                        // Present share sheet to let user choose where to save/share the file
                        let activityVC = UIActivityViewController(activityItems: [destinationURL], applicationActivities: nil)
                        activityVC.popoverPresentationController?.sourceView = self.view // For iPad compatibility
                        self.present(activityVC, animated: true, completion: nil)
                    }
                }
            } catch {
                DispatchQueue.main.async {
                    self.stopLoading()
                    print("Error saving file: \(error)")
                    self.alert(message: "An error occurred while saving the file.", title: "Error")
                }
            }
        }
        
        task.resume() // Start the download task
    }
}
extension UIViewController {
    func makeCall() {
        let phoneNumber = "1800 121 6254" // Global phone number
        if let phoneURL = URL(string: "tel://\(phoneNumber)"),
           UIApplication.shared.canOpenURL(phoneURL) {
            UIApplication.shared.open(phoneURL, options: [:], completionHandler: nil)
        } else {
            print("Call functionality is not available.")
        }
    }
}
// MARK: HTML to String convert ==>
extension String {
    func htmlToAttributedString_(fontName: String, fontSize: Float) -> NSAttributedString? {
        let style = "<style>body { font-family: '\(fontName)'; font-size:\(fontSize)px; }</style>"
        guard let data = (self + style).data(using: .utf8) else {
            return nil
        }
        return try? NSAttributedString(data: data, options: [.documentType: NSAttributedString.DocumentType.html, .characterEncoding:String.Encoding.utf8.rawValue], documentAttributes: nil)
    }
    func htmlToAttributedString(fontName: String = "System", fontSize: Float = 17) -> NSAttributedString? {
        let styledHTML = """
        <style>
        body {
            font-family: -apple-system, \(fontName);
            font-size: \(fontSize)px;
        }
        a {
            color: #007AFF;
            text-decoration: underline;
        }
        </style>
        \(self)
        """
        guard let data = styledHTML.data(using: .utf8) else { return nil }
        return try? NSAttributedString(
            data: data,
            options: [
                .documentType: NSAttributedString.DocumentType.html,
                .characterEncoding: String.Encoding.utf8.rawValue
            ],
            documentAttributes: nil
        )
    }
}
