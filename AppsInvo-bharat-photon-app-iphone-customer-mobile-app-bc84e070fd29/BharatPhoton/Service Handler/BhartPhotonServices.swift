//
//  BhartPhotonServices.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 30/07/24.
//

 
import Foundation
import SwiftyJSON
class BhartPhotonServices {
    
    static let SharedInstance: BhartPhotonServices = BhartPhotonServices()
    private init(){}
    
    func interactor(apiType: ServiceType, url: String, params: [String: Any], token: String, success:@escaping (JSON) -> Void, failure:@escaping (String, JSON) -> Void) {
        print(params)
        print(url)
        switch apiType {
        case .postJSON:
            ServicesAPICalling.shared.postJSON(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { err,json  in
                failure(err, json)
            }
        case .putJSON:
            ServicesAPICalling.shared.putJSON(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { err,json  in
                failure(err, json)
            }
        case .GET:
            ServicesAPICalling.shared.requestGET(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { err,json  in
                failure(err, json)
            }
        case .POST:
            ServicesAPICalling.shared.requestPOST(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { Error in
//                failure(Error)
            }
        case .PUT:
            ServicesAPICalling.shared.requestPUT(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { Error in
//                failure(Error)
            }
        case .DELETE:
            ServicesAPICalling.shared.requestDELETE(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { err,json  in
                failure(err, json)
            }
        case .UPDATE:
            print("Not Implemented")
        case .PATCH:
            ServicesAPICalling.shared.requestPATCH(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { Error in
//                failure(Error)
            }
        case .patchJSON:
            ServicesAPICalling.shared.patchJSON(url, params: params, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { err,json  in
                failure(err, json)
            }
        case .PostMultipart:
            break
        case .PostMultipartMultipleImage:
            break
        }
    }
    func multipart(apiType: ServiceType, url: String, params: [String: Any], image: UIImage, token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void) {
        switch apiType {
        case .postJSON:
            break
        case .putJSON:
            break
        case .GET:
            break
        case .POST:
            break
        case .PUT:
            break
        case .DELETE:
            break
        case .UPDATE:
            break
        case .PATCH:
            break
        case .patchJSON:
            break
        case .PostMultipart:
            ServicesAPICalling.shared.postMultiplart(url, params: [:], image: image, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { Error in
                failure(Error)
            }
        case .PostMultipartMultipleImage:
            break
        }
    }
    func multipartMultipleImage(apiType: ServiceType, url: String, params: [String: Any], images: [UIImage], token: String, success:@escaping (JSON) -> Void, failure:@escaping (Error) -> Void) {
        switch apiType {
        case .postJSON:
            break
        case .putJSON:
            break
        case .GET:
            break
        case .POST:
            break
        case .PUT:
            break
        case .DELETE:
            break
        case .UPDATE:
            break
        case .PATCH:
            break
        case .patchJSON:
            break
        case .PostMultipart:
            break
        case .PostMultipartMultipleImage:
            ServicesAPICalling.shared.postMultiplartMultipleImage(url, params: params, images: images, token: token) { JSONResponse in
                success(JSONResponse)
            } failure: { Error in
                failure(Error)
            }
        }
    }
}
extension Dictionary {
    var queryString: String {
        var output: String = ""
        for (key,value) in self {
            output +=  "\(key)=\(value)&"
        }
        output = String(output.dropLast())
        return output
    }
}
