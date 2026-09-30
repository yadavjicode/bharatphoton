//
//  ServiceApiCalling.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 30/07/24.
//

import Foundation
import SwiftyJSON
let sceneDelegateReference = UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate
class ServicesAPICalling {
    
    static let shared = ServicesAPICalling()
    private init() {}
    
    func requestGET(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (String, JSON) -> Void)  {
        let data : Data = (params?.queryString.data(using: .utf8))!
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "GET"
        request.addValue("application/json", forHTTPHeaderField:"Content-Type")
      // request.addValue(UserDefaults.standard.string(forKey: "token") ?? "", forHTTPHeaderField: "authtoken")
        request.setValue(NSLocalizedString("lang", comment: ""), forHTTPHeaderField:"Accept-Language")
        request.setValue( token, forHTTPHeaderField: "authtoken")
       // request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        request.httpBody = data
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Client error: \(statusCode)", jsonRes)
                    case 500...599:
                        print("Server error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Server error: \(statusCode)", jsonRes)
                    default:
                        print("Unexpected status code: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Unexpected status code: \(statusCode)", jsonRes)
                    }
                } else {
                    if let error = error {
                        sceneDelegateReference?.window?.rootViewController?.alert(message: error.localizedDescription)                    }
                }
            }
        }
        task.resume()
    }
    func requestPOST(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void)  {
        let data : Data = (params?.queryString.data(using: .utf8))!
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField:"Content-Type")
      //  request.addValue(UserDefaults.standard.string(forKey: "token") ?? "", forHTTPHeaderField: "authtoken")
        request.setValue( token, forHTTPHeaderField: "authtoken")
        request.setValue(NSLocalizedString("lang", comment: ""), forHTTPHeaderField:"Accept-Language")
        request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        request.httpBody = data
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.sync {
                if let data = data {
                    let jsonRes = JSON(data)
                    success(jsonRes)
                }
                if let error = error {
                    failure(error.self)
                }
            }
        }
        task.resume()
    }
    func requestPUT(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void)  {
        let data : Data = (params?.queryString.data(using: .utf8))!
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "PUT"
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField:"Content-Type")
        request.setValue(NSLocalizedString("lang", comment: ""), forHTTPHeaderField:"Accept-Language")
        request.setValue( token, forHTTPHeaderField: "Authorization")
        request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        request.httpBody = data
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.sync {
                if let data = data {
                    let jsonRes = JSON(data)
                    success(jsonRes)
                }
                if let error = error {
                    failure(error.self)
                }
            }
        }
        task.resume()
    }
    
    func putJSON(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (String, JSON) -> Void) {
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "PUT"
        request.setValue(NSLocalizedString("lang", comment: ""), forHTTPHeaderField:"Accept-Language")
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue( token, forHTTPHeaderField: "authtoken")
        request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        let jsonData = try? JSONSerialization.data(withJSONObject: params!)
        request.httpBody = jsonData
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Client error: \(statusCode)", jsonRes)
                    case 500...599:
                        print("Server error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Server error: \(statusCode)", jsonRes)
                    default:
                        print("Unexpected status code: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Unexpected status code: \(statusCode)", jsonRes)
                    }
                } else {
                    if let error = error {
                        sceneDelegateReference?.window?.rootViewController?.alert(message: error.localizedDescription)                    }
                }
            }
        }
        task.resume()
    }
    func requestPATCH(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void)  {
        print("PARAMS:- URL: \(strURL)   Body: \(String(describing: params))   Token: \(token)")
        let data : Data = (params?.queryString.data(using: .utf8))!
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "PATCH"
        request.setValue("application/json", forHTTPHeaderField:"Content-Type")
        request.setValue(NSLocalizedString("lang", comment: ""), forHTTPHeaderField:"Accept-Language")
        request.setValue( token, forHTTPHeaderField: "Authorization")
        request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        request.httpBody = data
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in

            DispatchQueue.main.sync {
                if let data = data {
                    let jsonRes = JSON(data)
                    success(jsonRes)
                }
                if let error = error {
                    failure(error.self)
                }
            }
        }
        task.resume()
    }
    func patchJSON(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (String, JSON) -> Void) {
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "PATCH"
        request.addValue("application/json", forHTTPHeaderField:"Content-Type")
        request.addValue(UserDefaults.standard.string(forKey: "token") ?? "", forHTTPHeaderField: "authtoken")
        request.setValue(NSLocalizedString("lang", comment: ""), forHTTPHeaderField:"Accept-Language")
        request.setValue( token, forHTTPHeaderField: "Authorization")
        request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        let jsonData = try? JSONSerialization.data(withJSONObject: params!)
        request.httpBody = jsonData
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Client error: \(statusCode)", jsonRes)
                    case 500...599:
                        print("Server error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Server error: \(statusCode)", jsonRes)
                    default:
                        print("Unexpected status code: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Unexpected status code: \(statusCode)", jsonRes)
                    }
                } else {
                    if let error = error {
                        sceneDelegateReference?.window?.rootViewController?.alert(message: error.localizedDescription)                    }
                }
            }
        }
        task.resume()
    }
    func requestDELETE(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (String, JSON) -> Void)  {
        let data : Data = (params?.queryString.data(using: .utf8))!
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "DELETE"
        request.addValue("application/json", forHTTPHeaderField:"Content-Type")
      //  request.addValue(UserDefaults.standard.string(forKey: "token") ?? "", forHTTPHeaderField: "authtoken")
        request.addValue( token, forHTTPHeaderField: "authtoken")
       // request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        request.httpBody = data
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Client error: \(statusCode)", jsonRes)
                    case 500...599:
                        print("Server error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Server error: \(statusCode)", jsonRes)
                    default:
                        print("Unexpected status code: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Unexpected status code: \(statusCode)", jsonRes)
                    }
                } else {
                    if let error = error {
                        sceneDelegateReference?.window?.rootViewController?.alert(message: error.localizedDescription)                    }
                }
            }
        }
        task.resume()
    }
    func postJSON(_ strURL : String, params : [String : Any]?, token: String, success:@escaping (JSON) -> Void, failure:@escaping (String, JSON) -> Void) {
        print(strURL)
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "POST"
        request.addValue("application/json", forHTTPHeaderField:"Content-Type")
//        request.addValue(UserDefaults.standard.string(forKey: "token") ?? "", forHTTPHeaderField: "authtoken")
        request.addValue( token, forHTTPHeaderField: "authtoken")
       // request.setValue(UserDefaults.standard.string(forKey: "DeviceId") ?? "", forHTTPHeaderField: "Device_id")
        let jsonData = try? JSONSerialization.data(withJSONObject: params!)
        request.httpBody = jsonData
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Client error: \(statusCode)", jsonRes)
                    case 500...599:
                        print("Server error: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Server error: \(statusCode)", jsonRes)
                    default:
                        print("Unexpected status code: \(statusCode)")
                        let jsonRes = JSON(data!)
                        failure("Unexpected status code: \(statusCode)", jsonRes)
                    }
                } else {
                    if let error = error {
                        sceneDelegateReference?.window?.rootViewController?.alert(message: error.localizedDescription)                    }
                }
            }
        }
        task.resume()
    }
    func postMultiplart(_ strURL : String, params : [String : Any], image: UIImage, token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void) {
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "POST"
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        var body = Data()
        for (key, value) in params {
            if let parameterData = "--\(boundary)\r\n".data(using: .utf8),
               let dispositionData = "Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n".data(using: .utf8),
               let valueData = "\(value)\r\n".data(using: .utf8) {
                body.append(parameterData)
                body.append(dispositionData)
                body.append(valueData)
            }
        }
        if let imageData = image.jpegData(compressionQuality: 0.8) {
            if let imageDataString = "--\(boundary)\r\nContent-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n".data(using: .utf8),
               let imageDataEndString = "\r\n".data(using: .utf8) {
                body.append(imageDataString)
                body.append(imageData)
                body.append(imageDataEndString)
            }
        }
        if let boundaryEndData = "--\(boundary)--\r\n".data(using: .utf8) {
            body.append(boundaryEndData)
        }
        request.httpBody = body
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                //    let lang = UserDefaults.standard.string(forKey: "language") ?? "en"
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        print(String(data: data!, encoding: .utf8) ?? "NA")
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        sceneDelegateReference?.window?.rootViewController?.alert(message: "Something went wrong")
                        failure(NSError(domain: "Client Error", code: statusCode, userInfo: nil))
                    case 500...599:
                        print("Server error: \(statusCode)")
                        sceneDelegateReference?.window?.rootViewController?.alert(message: "Something went wrong")
                        failure(NSError(domain: "Server Error", code: statusCode, userInfo: nil))
                    default:
                        print("Unexpected status code: \(statusCode)")
                        sceneDelegateReference?.window?.rootViewController?.alert(message: "Something went wrong")
                        failure(NSError(domain: "Unknown Error", code: statusCode, userInfo: nil))
                    }
                } else {
                    if let error = error {
                        failure(error)
                    }
                }
            }
        }
        task.resume()
    }
    func postMultiplartMultipleImage(_ strURL : String, params : [String : Any], images: [UIImage], token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void) {
        var request : URLRequest = URLRequest(url: URL(string: strURL)!)
        request.httpMethod = "POST"
        let boundary = UUID().uuidString
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        var body = Data()
        for (key, value) in params {
            if let parameterData = "--\(boundary)\r\n".data(using: .utf8),
               let dispositionData = "Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n".data(using: .utf8),
               let valueData = "\(value)\r\n".data(using: .utf8) {
                body.append(parameterData)
                body.append(dispositionData)
                body.append(valueData)
            }
        }
        for image in images {
            if let imageData = image.jpegData(compressionQuality: 0.8) {
                if let imageDataString = "--\(boundary)\r\nContent-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n".data(using: .utf8),
                   let imageDataEndString = "\r\n".data(using: .utf8) {
                    body.append(imageDataString)
                    body.append(imageData)
                    body.append(imageDataEndString)
                }
            }
        }
        
        if let boundaryEndData = "--\(boundary)--\r\n".data(using: .utf8) {
            body.append(boundaryEndData)
        }
        request.httpBody = body
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (data, response, error) in
            DispatchQueue.main.async {
                if let httpResponse = response as? HTTPURLResponse {
                    var lang = UserDefaults.standard.string(forKey: "language") ?? "en"
                    let statusCode = httpResponse.statusCode
                    switch statusCode {
                    case 200...299:
                        let jsonRes = JSON(data!)
                        print(String(data: data!, encoding: .utf8) ?? "NA")
                        success(jsonRes)
                    case 400...499:
                        print("Client error: \(statusCode)")
                        sceneDelegateReference?.window?.rootViewController?.alert(message: "Something went wrong")
                        failure(NSError(domain: "Client Error", code: statusCode, userInfo: nil))
                    case 500...599:
                        print("Server error: \(statusCode)")
                        sceneDelegateReference?.window?.rootViewController?.alert(message: "Something went wrong")
                        failure(NSError(domain: "Server Error", code: statusCode, userInfo: nil))
                    default:
                        print("Unexpected status code: \(statusCode)")
                        sceneDelegateReference?.window?.rootViewController?.alert(message: "Something went wrong")
                        failure(NSError(domain: "Unknown Error", code: statusCode, userInfo: nil))
                    }
                } else {
                    if let error = error {
                        failure(error)
                    }
                }
            }
        }
        task.resume()
    }
}
