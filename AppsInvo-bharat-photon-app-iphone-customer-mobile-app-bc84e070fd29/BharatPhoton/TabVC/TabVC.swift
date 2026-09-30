//
//  TabVC.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 19/06/24.
//

import UIKit
import SDWebImage

class TabVC: UITabBarController {
    var guestUser = 0
    var profileImage = ""
    lazy var tabView: UIView! = {
        let v = UIView(frame: CGRect(x: 16, y: UIScreen.main.bounds.height - 80, width: UIScreen.main.bounds.width - 32, height: 65))
        v.clipsToBounds = true
        v.layer.cornerRadius = 15
        v.layer.maskedCorners = [.layerMaxXMaxYCorner, .layerMinXMaxYCorner]
        v.backgroundColor = .black
        
        return v
    }()
    //MARK: Home Selected View
    lazy var homeView: UIView! = {
        let v = UIView(frame: CGRect(x: (tabView.bounds.width / 50), y: 12, width: 95, height:  36))
        v.clipsToBounds = true
        v.layer.cornerRadius = v.bounds.height/2
        v.backgroundColor = UIColor(hexString: "FF8D3A")
        
        return v
    }()
    lazy var homeImg: UIImageView! = {
        let v = UIImageView(frame: CGRect(x: 10, y: 18, width: 24, height: 24))
        v.image = UIImage(named: "home")
        v.backgroundColor = .white
        return v
    }()
    //MARK: Category Selected View
    lazy var categoryView: UIView! = {
        let v = UIView(frame: CGRect(x: (tabView.bounds.width / 3.4), y: 12, width: 95, height:  36))
        v.clipsToBounds = true
        v.layer.cornerRadius = v.bounds.height/2
        v.backgroundColor = UIColor(hexString: "FF8D3A")
        
        return v
    }()
    lazy var categoryImg: UIImageView! = {
        let v = UIImageView(frame: CGRect(x: (tabView.bounds.width / 2) - 60, y: 18, width: 24, height: 24))
        v.image = UIImage(named: "categories")
        
        return v
    }()
    //MARK: Order Selected View
    lazy var myOrderView: UIView! = {
        let v = UIView(frame: CGRect(x: (tabView.bounds.width / 2) + 25, y: 12, width: 95, height:  36))
        v.clipsToBounds = true
        v.layer.cornerRadius = v.bounds.height/2
        v.backgroundColor = UIColor(hexString: "FF8D3A")
        
        return v
    }()
    lazy var myOrderImg: UIImageView! = {
        let v = UIImageView(frame: CGRect(x: (tabView.bounds.width / 1.5) - 25, y: 18, width: 24, height: 24))
        v.image = UIImage(named: "myOrder")
        return v
    }()
    //MARK: Profile Selected View
    lazy var myProfileView: UIView! = {
        let v = UIView(frame: CGRect(x: (tabView.bounds.width / 1.5) + 15, y: 12, width: 95, height:  36))
        v.layer.cornerRadius = 18
        v.clipsToBounds = true
        v.layer.cornerRadius = v.bounds.height/2
        v.backgroundColor = UIColor(hexString: "FF8D3A")
        return v
    }()
    lazy var myProfileImg: UIImageView! = {
        let v = UIImageView(frame: CGRect(x: tabView.bounds.width - 35, y: 18, width: 24, height: 24))
        // v.image = UIImage(named: "profilePlaceholder")
        v.sd_setImage(with: URL(string: self.profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
        v.contentMode = .scaleAspectFill
        v.clipsToBounds = true
        v.layer.cornerRadius = 12
        return v
    }()
    lazy var homeTitle: UILabel! = {
        let v = UILabel(frame: CGRect(x: (tabView.bounds.width / 12), y: 18, width: 60, height: 24))
        v.text = "Home"
        v.textAlignment = .center
        v.textColor = .blue
        v.font = UIFont.systemFont(ofSize: 12)
        return v
    }()
    lazy var categoryTitle: UILabel! = {
        let v = UILabel(frame: CGRect(x: (tabView.bounds.width / 2.5), y: 18, width: 60, height: 24))
        v.text = "Category"
        v.textAlignment = .center
        v.font = UIFont.systemFont(ofSize: 12)
        return v
    }()
    lazy var myOrderTitle: UILabel! = {
        let v = UILabel(frame: CGRect(x: (tabView.bounds.width / 1.5), y: 18, width: 60, height: 24))
        v.text = "My order"
        v.textAlignment = .center
        v.font = UIFont.systemFont(ofSize: 12)
        return v
    }()
    lazy var myProfileTitle: UILabel! = {
        let v = UILabel(frame: CGRect(x: (tabView.bounds.width / 1.22) - 10, y: 18, width: 60, height: 24))
        v.text = "My profile"
        v.textAlignment = .center
        v.font = UIFont.systemFont(ofSize: 12)
        return v
    }()
    lazy var homeBtn: UIButton! = {
        let v = UIButton(frame: CGRect(x: 10, y: 10, width: 70, height: 35))
        v.addTarget(self, action: #selector(self.homeAction(sender:)), for: .touchUpInside)
        return v
    }()
    lazy var categoryBtn: UIButton! = {
        let v = UIButton(frame: CGRect(x: (tabView.bounds.width / 2) - 70, y: 10, width: 70, height: 35))
        v.addTarget(self, action: #selector(self.categoryAction(sender:)), for: .touchUpInside)
        return v
    }()
    lazy var myorderBtn: UIButton! = {
        let v = UIButton(frame: CGRect(x: (tabView.bounds.width / 1.65) - 10, y: 10, width: 70, height: 35))
        v.addTarget(self, action: #selector(self.myorderAction(sender:)), for: .touchUpInside)
        return v
    }()
    lazy var myProfileBtn: UIButton! = {
        let v = UIButton(frame: CGRect(x: tabView.bounds.width - 66, y: 10, width: 70, height: 35))
        v.addTarget(self, action: #selector(self.myprofileAction(sender:)), for: .touchUpInside)
        return v
    }()
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.isNavigationBarHidden = true
        self.selectedIndex = 0
        self.view.addSubview(tabView)
        tabView.addSubview(homeView)
        tabView.addSubview(categoryView)
        tabView.addSubview(myOrderView)
        tabView.addSubview(myProfileView)
        
        homeView.addSubview(homeImg)
        tabView.addSubview(homeImg)
        categoryView.addSubview(categoryImg)
        tabView.addSubview(categoryImg)
        tabView.addSubview(myOrderImg)
        tabView.addSubview(myProfileImg)
        
        tabView.addSubview(homeTitle)
        tabView.addSubview(categoryTitle)
        tabView.addSubview(myOrderTitle)
        tabView.addSubview(myProfileTitle)
        
        tabView.addSubview(homeBtn)
        tabView.addSubview(categoryBtn)
        tabView.addSubview(myorderBtn)
        tabView.addSubview(myProfileBtn)
        
        self.homeView.isHidden = false
        self.categoryView.isHidden = true
        self.myOrderView.isHidden = true
        self.myProfileView.isHidden = true
        
        self.homeTitle.isHidden = false
        self.homeTitle.textColor = .white
        self.categoryTitle.isHidden = true
        self.categoryTitle.textColor = .white
        self.myOrderTitle.isHidden = true
        self.myOrderTitle.textColor = .white
        self.myProfileTitle.isHidden = true
        self.myOrderTitle.textColor = .white
        
        self.homeImg.image = UIImage(named: "Home")
        self.homeImg.backgroundColor = .clear
        self.categoryImg.image = UIImage(named: "categories")
        self.categoryImg.backgroundColor = .clear
        self.myOrderImg.image = UIImage(named: "myOrder")
        self.myOrderImg.backgroundColor = .clear
        
        if guestUser == 1{
            self.myProfileImg.sd_setImage(with: URL(string: self.profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
        }
        else{
            self.serviceUserProfileAPI()
        }
    }
    @objc func homeAction(sender: UIButton) {
        UIView.animate(withDuration: 0.3) {
            self.categoryImg.frame = CGRect(x: (self.tabView.bounds.width / 2) - 60, y: 18, width: 24, height: 24)
            self.myOrderImg.frame = CGRect(x: (self.tabView.bounds.width / 1.5) - 25, y: 18, width: 24, height: 24)
            self.homeImg.image = UIImage(named: "Home")
            self.myOrderImg.image = UIImage(named: "myOrder")
            self.categoryImg.image = UIImage(named: "categories")
            self.myProfileImg.sd_setImage(with: URL(string: self.profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
            self.myProfileImg.frame = CGRect(x: self.tabView.bounds.width - 35, y: 18, width: 24, height: 24)
            self.homeTitle.isHidden = false
            self.homeTitle.textColor = .white
            self.homeView.isHidden = false
            self.categoryView.isHidden = true
            self.myOrderView.isHidden = true
            self.myProfileView.isHidden = true
            self.categoryTitle.isHidden = true
            self.myProfileTitle.isHidden = true
            self.myOrderTitle.isHidden = true
            self.selectedIndex = 0
        }
    }
    @objc func categoryAction(sender: UIButton) {
        UIView.animate(withDuration: 0.3) {
            self.categoryImg.frame = CGRect(x: (self.tabView.bounds.width / 2) - 60, y: 18, width: 24, height: 24)
            self.myOrderImg.frame = CGRect(x: (self.tabView.bounds.width / 1.5) - 25, y: 18, width: 24, height: 24)
            self.categoryImg.image = UIImage(named: "Categories")
            self.homeImg.image = UIImage(named: "home")
            self.myOrderImg.image = UIImage(named: "myOrder")
            self.myProfileImg.sd_setImage(with: URL(string: self.profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
            self.myProfileImg.frame = CGRect(x: self.tabView.bounds.width - 35, y: 18, width: 24, height: 24)
            self.categoryTitle.isHidden = false
            self.categoryTitle.textColor = .white
            self.categoryView.isHidden = false
            self.homeTitle.isHidden = true
            self.myOrderTitle.isHidden = true
            self.myProfileTitle.isHidden = true
            self.myOrderView.isHidden = true
            self.myProfileView.isHidden = true
            self.homeView.isHidden = true
            self.selectedIndex = 1
        }
    }
    @objc func myorderAction(sender: UIButton) {
        UIView.animate(withDuration: 0.3) {
            if self.guestUser == 1{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
                self.navigationController?.pushViewController(vc, animated: true)
            }
            else{
                self.categoryImg.frame = CGRect(x: (self.tabView.bounds.width / 2) - 60, y: 18, width: 24, height: 24)
                self.myOrderImg.frame = CGRect(x: (self.tabView.bounds.width / 1.5) - 25, y: 18, width: 24, height: 24)
                self.myOrderImg.image = UIImage(named: "MyOrder")
                self.homeImg.image = UIImage(named: "home")
                self.categoryImg.image = UIImage(named: "categories")
                self.myProfileImg.sd_setImage(with: URL(string: self.profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
                self.myProfileImg.frame = CGRect(x: self.tabView.bounds.width - 35, y: 18, width: 24, height: 24)
                self.myProfileTitle.isHidden = true
                self.myOrderView.isHidden = false
                self.myOrderTitle.isHidden = false
                self.myOrderTitle.textColor = .white
                self.homeView.isHidden = true
                self.homeTitle.isHidden = true
                self.myProfileView.isHidden = true
                self.myProfileTitle.isHidden = true
                self.categoryTitle.isHidden = true
                self.categoryView.isHidden = true
                self.selectedIndex = 2
            }
        }
    }
    @objc func myprofileAction(sender: UIButton) {
        UIView.animate(withDuration: 0.3) {
            if self.guestUser == 1{
                let vc = self.storyboard?.instantiateViewController(withIdentifier: "LoginVC") as! LoginVC
                self.navigationController?.pushViewController(vc, animated: true)
            }
            else{
                self.myOrderImg.frame = CGRect(x: (self.tabView.bounds.width / 1.8), y: 18, width: 24, height: 24)
                self.categoryImg.frame = CGRect(x: (self.tabView.bounds.width / 3.5), y: 18, width: 24, height: 24)
                self.myProfileImg.frame = CGRect(x: (self.tabView.bounds.width / 1.4), y: 18, width: 24, height: 24)
                self.myProfileImg.sd_setImage(with: URL(string: self.profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
                self.homeImg.image = UIImage(named: "home")
                self.myOrderImg.image = UIImage(named: "myOrder")
                self.categoryImg.image = UIImage(named: "categories")
                self.myProfileView.isHidden = false
                self.myProfileTitle.isHidden = false
                self.myProfileTitle.textColor = .white
                self.homeView.isHidden = true
                self.homeTitle.isHidden = true
                self.myOrderView.isHidden = true
                self.myOrderTitle.isHidden = true
                self.categoryTitle.isHidden = true
                self.categoryView.isHidden = true
                self.selectedIndex = 3
            }
        }
    }
}
extension TabVC {
    func serviceUserProfileAPI(){
        self.stopLoading()
        let parms = [:] as [String: Any]
        let token = UserDefaults.standard.string(forKey: "token") ?? ""
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "userProfile", params: parms, token: token) { JSONResponse in
            self.stopLoading()
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                self.alert(message: JSONResponse["message"].stringValue)
            case 1:
                let dic = JSONResponse["data"].dictionaryValue
                let profileImage = dic["profileImage"]?.stringValue ?? ""
                self.profileImage = profileImage
                self.myProfileImg.sd_setImage(with: URL(string: profileImage), placeholderImage: UIImage(named: "profilePlaceholder"))
            default:
                print("Unidentified code found")
                break
            }
        } failure: { Error, JSONResponse in
            self.stopLoading()
            //self.alert(message: JSONResponse["message"].stringValue, title: Error)
            UserDefaults.standard.removeObject(forKey: "_id")
            UserDefaults.standard.removeObject(forKey: "token")
        }
    }
}
