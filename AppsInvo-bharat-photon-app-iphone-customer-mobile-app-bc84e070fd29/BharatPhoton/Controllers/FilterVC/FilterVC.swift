//
//  HomeFilterVC.swift
//  Bharat Photon
//
//  Created by Sachin Kamal on 07/11/24.
//

import UIKit
import DropDown

class FilterVC: UIViewController {
    @IBOutlet weak var selectProductCategoryTF: UITextField!
    @IBOutlet weak var selectTypeOfApplicationsTF: UITextField!
    @IBOutlet weak var selectVariantTF: UITextField!
    @IBOutlet weak var selectSensorTypeTF: UITextField!
    var Dropdown = DropDown()
    var catArry = ["Sensor tap","Urinal sensor","Hand dryer"]
    var ApplicationArry = ["Basin Mounted","Basin Mounted 1","Basin Mounted 2"]
    var variantArry = ["Electrical"]
    var sensorTypeArry = ["Linear Sensor","Linear Sensor 1","Linear Sensor 2"]
    override func viewDidLoad() {
        super.viewDidLoad()
        statusBarColor()
        if #available(iOS 13.0, *) {
        overrideUserInterfaceStyle = .light
        }
    }
    @IBAction func hideFilterPopup(_ sender: UIButton) {
        self.dismiss(animated: false, completion: nil)
    }
    @IBAction func productCategoryAction(_ sender: UIButton) {
        Dropdown.dataSource = catArry
        Dropdown.anchorView = sender
        Dropdown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        Dropdown.show()
        Dropdown.selectionAction = { [weak self] (index: Int, item: String) in
            guard let _ = self else { return }
            self!.selectProductCategoryTF.text! = self!.catArry[index]
        }
    }
    @IBAction func typeOfApplicationAction(_ sender: UIButton) {
        Dropdown.dataSource = ApplicationArry
        Dropdown.anchorView = sender
        Dropdown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        Dropdown.show()
        Dropdown.selectionAction = { [weak self] (index: Int, item: String) in
            guard let _ = self else { return }
            self!.selectTypeOfApplicationsTF.text! = self!.ApplicationArry[index]
        }
    }
    @IBAction func variantAction(_ sender: UIButton) {
        Dropdown.dataSource = variantArry
        Dropdown.anchorView = sender
        Dropdown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        Dropdown.show()
        Dropdown.selectionAction = { [weak self] (index: Int, item: String) in
            guard let _ = self else { return }
            self!.selectVariantTF.text! = self!.variantArry[index]
        }
    }
    @IBAction func sensorTypeAction(_ sender: UIButton) {
        Dropdown.dataSource = sensorTypeArry
        Dropdown.anchorView = sender
        Dropdown.bottomOffset = CGPoint(x: 0, y: sender.frame.size.height)
        Dropdown.show()
        Dropdown.selectionAction = { [weak self] (index: Int, item: String) in
            guard let _ = self else { return }
            self!.selectSensorTypeTF.text! = self!.sensorTypeArry[index]
        }
    }
    @IBAction func applyBtnAction(_ sender: UIButton) {
        if  selectProductCategoryTF.text!.isEmpty{
            self.alert(message: "Please select product category")
        }
        else if selectTypeOfApplicationsTF.text!.isEmpty{
            self.alert(message: "Please select type of applications")
        }
        else if selectVariantTF.text!.isEmpty{
            self.alert(message: "Please select variant")
        }
        else if selectSensorTypeTF.text!.isEmpty{
            self.alert(message: "Please select sensor type")
        }
        else {
            self.dismiss(animated: false, completion: nil)
        }
    }
}
