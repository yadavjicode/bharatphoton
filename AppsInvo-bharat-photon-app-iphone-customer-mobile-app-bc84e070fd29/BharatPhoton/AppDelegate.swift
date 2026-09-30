//
//  AppDelegate.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 17/06/24.


import UIKit
import IQKeyboardManager
import GoogleSignIn
import FBSDKLoginKit
import FBSDKCoreKit
import AWSS3
import FirebaseCore
import FirebaseAuth
import FirebaseAnalytics
import UserNotifications
import FirebaseMessaging
import UserNotificationsUI

 
@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    let gcmMessageIDKey = "gcm.message_id"
    // Facebook AppID ==> fb1662284741021546
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        Thread.sleep(forTimeInterval: 3.0)
        serviceAWSS3API()
        IQKeyboardManager.shared().isEnabled = true
        GIDSignIn.sharedInstance().clientID = "59265085652-cqar5scqkom368r767gg7lpdr02u0fg9.apps.googleusercontent.com"
        GIDSignIn.sharedInstance()?.restorePreviousSignIn()
        Settings.appID = "604226429231287"
        ApplicationDelegate.shared.application(application, didFinishLaunchingWithOptions: launchOptions)
        let deviceId = uniqueDeviceIdentifier()
        print("Device ID: \(deviceId)")
        UserDefaults.standard.set(deviceId, forKey: "DeviceId")
        // pushNotification ==>
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                DispatchQueue.main.async {
                    application.registerForRemoteNotifications()
                }
            } else {
                // Handle denial of notification permission
            }
        }
        return true
    }
    func application(_ app: UIApplication,open url: URL,options: [UIApplication.OpenURLOptionsKey : Any]) -> Bool {
        return GIDSignIn.sharedInstance().handle(url)
        // MARK: ==> FB
        ApplicationDelegate.shared.application(app,open: url,sourceApplication: options[UIApplication.OpenURLOptionsKey.sourceApplication] as? String, annotation: options[UIApplication.OpenURLOptionsKey.annotation]
        )
    }
    // MARK: UISceneSession Lifecycle
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }
    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}
// MARK: AWSS3 API ====>
extension AppDelegate {
    func serviceAWSS3API(){
        let parms = [:] as [String: Any]
        BhartPhotonServices.SharedInstance.interactor(apiType: .GET, url: ServiceEndPoint.BhartPhotonUrl.baseURL + "s3", params: parms, token: "") { JSONResponse in
            let code = JSONResponse["code"].intValue
            switch code {
            case 0:
                print("error")
            case 1:
                print("Success")
                let dic = JSONResponse["data"].dictionaryValue
                let accessKeyId = dic["accessKeyId"]?.stringValue ?? ""
                let secretAccessKey = dic["secretAccessKey"]?.stringValue ?? ""
                let bucketName = dic["bucketName"]?.stringValue ?? ""
                UserDefaults.standard.setValue(bucketName, forKey: "AWS_BUCKET_NAME")
                self.initializeS3(accessKey: accessKeyId, secretKey: secretAccessKey)
            default:
                print("Unidentified code found")
                break
            }
          }failure: { Error, JSONResponse in
        }
    }
    func initializeS3(accessKey: String, secretKey: String) {
        let credentialsProvider = AWSStaticCredentialsProvider(accessKey: accessKey, secretKey: secretKey)
        let configuration = AWSServiceConfiguration(region:AWSRegionType.APSouth1, credentialsProvider:credentialsProvider)
        AWSServiceManager.default().defaultServiceConfiguration = configuration
    }
}
extension AppDelegate {
    // Retrieves a unique device identifier. If one does not exist, it generates and stores a new one.
    func uniqueDeviceIdentifier() -> String {
        let userDefaults = UserDefaults.standard
        // Check if the identifier already exists
        if let existingId = userDefaults.string(forKey: "uniqueDeviceId") {
            return existingId
        } else {
            // Generate a new unique identifier
            let newId = UUID().uuidString
            userDefaults.set(newId, forKey: "uniqueDeviceId")
            return newId
        }
    }
}
//MARK: Push Notifications
extension AppDelegate: UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data -> String in return String(format: "%02.2hhx", data) }
        let token = tokenParts.joined()
        print(token , ":   Device Token")
        Messaging.messaging().apnsToken = deviceToken
        Messaging.messaging().setAPNSToken(deviceToken,type: MessagingAPNSTokenType.unknown)
        fetchFirebaseToken()
    }
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications:", error.localizedDescription)
    }
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        if let info = userInfo as? [String: Any] {
            print(info)
//            if let notificationData = info["data"] as? String {
//                print(notificationData)
//                if let model = convertJSONStringToStruct(notificationData) {
//                    print(model)
//                    if let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
//                       let sceneDelegate = windowScene.delegate as? SceneDelegate {
//                        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "SplashVC") as? SplashVC
//                        vc?.pushNotificationModel = model
//                        let navigationController = CustomNavigationController(rootViewController: vc ?? UIViewController())
//                        navigationController.isNavigationBarHidden = true
//                        sceneDelegate.window?.rootViewController = navigationController
//                        sceneDelegate.window?.makeKeyAndVisible()
//                    }
//                }
//            }
        }
        completionHandler()
    }
//    func convertJSONStringToStruct(_ jsonString: String) -> PushNotificationModel? {
//        if let jsonData = jsonString.data(using: .utf8) {
//            do {
//                // Decode JSON data into PushNotificationModel struct
//                let pushNotification = try JSONDecoder().decode(PushNotificationModel.self, from: jsonData)
//                return pushNotification
//            } catch {
//                print("Failed to decode JSON string: \(error)")
//            }
//        } else {
//            print("Failed to convert JSON string to Data")
//        }
//        return nil
//    }
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("Firebase registration token: \(fcmToken ?? "")")
        UserDefaults.standard.setValue(fcmToken, forKey: "deviceToken")
    }
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        Messaging.messaging().appDidReceiveMessage(userInfo)
        if Auth.auth().canHandleNotification(userInfo) {
            completionHandler(.noData)
            return
        }
        print("Received remote notification: \(userInfo)")
        guard let aps = userInfo["aps"] as? [String: AnyObject] else {
            completionHandler(.failed)
            return
        }
        print(aps)
    }
    fileprivate func fetchFirebaseToken() {
        let token =  Messaging.messaging().fcmToken
        print(token ?? "","TOKEN FCM")
        UserDefaults.standard.setValue(token, forKey: "deviceToken")
    }
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        print(userInfo)
        completionHandler([.alert, .badge, .sound])
    }
}
