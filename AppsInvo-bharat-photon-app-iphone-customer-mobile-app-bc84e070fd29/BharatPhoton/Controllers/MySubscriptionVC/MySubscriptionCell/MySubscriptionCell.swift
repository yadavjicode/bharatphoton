//
//  MySubscriptionCell.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 01/07/24.
//

import UIKit

class MySubscriptionCell: UITableViewCell {
    @IBOutlet weak var paymentModaLbl: UILabel!
    @IBOutlet weak var qtyLbl: UILabel!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var uploadSitePhotoBtn: UIButton!
    @IBOutlet weak var uploadSitePhotoLbl: UILabel!
    @IBOutlet weak var img: UIImageView!
    @IBOutlet weak var planLbl: UILabel!
    @IBOutlet weak var priceLbl: UILabel!
    @IBOutlet weak var duretionLbl: UILabel!
    @IBOutlet weak var productImg: UIImageView!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var descriptionLbl: UILabel!
    @IBOutlet weak var modalNameLbl: UILabel!
    @IBOutlet weak var totalWaterSavedLbl: UILabel!
    @IBOutlet weak var totalCostSavedLbl: UILabel!
    @IBOutlet weak var noteLbl: UILabel!
    @IBOutlet weak var orderIdLbl: UILabel!
    @IBOutlet weak var totalWaterSavedView: UIView!
    @IBOutlet weak var totalCostSavedView: UIView!
    @IBOutlet weak var installationMetricsTable: UITableView!
    @IBOutlet weak var installationMetricsTableHeight: NSLayoutConstraint!
    @IBOutlet weak var totalPriceLbl: UILabel!
    var installationMetricsArray: [InstallationMetrics] = []
    var id = ""
    var productName = ""
    var categoryName = ""
    var categoryId = ""
    var productId = ""
    override func awakeFromNib() {
        super.awakeFromNib()
        installationMetricsTable.estimatedRowHeight = 150
        installationMetricsTable.rowHeight = UITableView.automaticDimension
        installationMetricsTable.delegate = self
        installationMetricsTable.dataSource = self
        installationMetricsTable.register(UINib(nibName: "InstallationMetricsCell", bundle: nil), forCellReuseIdentifier: "InstallationMetricsCell")
        installationMetricsTable.separatorStyle = .none
        installationMetricsTable.addObserver(self, forKeyPath: "contentSize", options: .new, context: nil)
        DispatchQueue.main.async {
            self.installationMetricsTableHeight.constant = self.installationMetricsTable.contentSize.height
            self.layoutIfNeeded()
        }
    }
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == "contentSize", let tableView = object as? UITableView, tableView == self.installationMetricsTable {
            installationMetricsTableHeight.constant = tableView.contentSize.height
            self.layoutIfNeeded()
        }
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        // Configure the view for the selected state
    }
}
extension MySubscriptionCell: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return installationMetricsArray.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "InstallationMetricsCell", for: indexPath) as! InstallationMetricsCell
        cell.sinNoLbl.text = installationMetricsArray[indexPath.row].sin ?? ""
        cell.subscriptionStartDateLbl.text = installationMetricsArray[indexPath.row].subscriptionStartDate?.toDisplayDateAndTime() ?? ""
        cell.subscriptionExpiryDateLbl.text = installationMetricsArray[indexPath.row].subscriptionExpiryDate?.toDisplayDateAndTime() ?? ""
        cell.registerComplantBtn.tag = indexPath.row
        cell.registerComplantBtn.addTarget(self, action: #selector(registerComplantBtnTapped(sender:)), for: .touchUpInside)
        cell.renewPlanBtn.tag = indexPath.row
        cell.renewPlanBtn.addTarget(self, action: #selector(renewPlanBtnTapped(sender:)), for: .touchUpInside)
      
        cell.selectionStyle = .none
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return installationMetricsTable.rowHeight
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let parentVC = self.parentViewController() else { return }
        let vc = parentVC.storyboard?.instantiateViewController(withIdentifier: "MySubscriptionDetailVC") as! MySubscriptionDetailVC
        vc.id = self.id
        vc.installationId = installationMetricsArray[indexPath.row]._id ?? ""
        vc.sin = installationMetricsArray[indexPath.row].sin ?? ""
        parentVC.navigationController?.pushViewController(vc, animated: true)
    }
    //MARK: RegisterComplantBtnTapped Action ==>
    @objc func registerComplantBtnTapped(sender: UIButton) {
        let row = sender.tag
        guard let parentVC = self.parentViewController() else { return }
        let vc = parentVC.storyboard?.instantiateViewController(withIdentifier: "RegisterComplaintVC") as! RegisterComplaintVC
        vc.productName = self.productName
        vc.categoryName = self.categoryName
        vc.categoryId = self.categoryId
        vc.productId = self.productId
        vc.sin = installationMetricsArray[row].sin ?? ""
        vc.purchasedSubscriptionId = self.id
        vc.type = 2
        parentVC.navigationController?.pushViewController(vc, animated: true)
    }
    //MARK: RenewPlanBtnTapped Action ==>
    @objc func renewPlanBtnTapped(sender: UIButton) {
        let row = sender.tag
        let isPlanExpired = installationMetricsArray[row].isPlanExpired ?? 0
        guard let parentVC = self.parentViewController() else { return }
        if isPlanExpired == 0{
            let alertController = UIAlertController(title: "", message: "Your plan has not expired yet, so you can't renew it now", preferredStyle: .alert)
                alertController.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
                parentVC.present(alertController, animated: true, completion: nil)
        }
        else{
            guard let parentVC = self.parentViewController() else { return }
            let vc = parentVC.storyboard?.instantiateViewController(withIdentifier: "RenewPlanVC") as! RenewPlanVC
            vc._id = self.id
            vc.productId = productId
            let sin = installationMetricsArray[row].sin ?? ""
            vc.sin = sin
            vc.selectedQuantity = 1
            vc.sinNoArray = [sin]
            vc.isRenewPlan = 1
            parentVC.navigationController?.pushViewController(vc, animated: true)
        }
    }
}
extension UIResponder {
    func parentViewController() -> UIViewController? {
        var responder: UIResponder? = self
        while let nextResponder = responder?.next {
            if let viewController = nextResponder as? UIViewController {
                return viewController
            }
            responder = nextResponder
        }
        return nil
    }
}
