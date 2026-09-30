//
//  S3.swift
//  BharatPhoton
//
//  Created by Sachin Kamal on 16/09/24.
//

 
import Foundation
import UIKit

let aws_s3BucketName = UserDefaults.standard.string(forKey: "AWS_BUCKET_NAME") ?? ""
//let aws_s3access_key = "AKIAZGWIOO6WXK6TPEEU"
//let aws_s3Secret = "E8JX4cdexgBpkI/ev1MQYSchnaGNdHRjBH+/v9Fc"
//let aws_s3region = "us-east-2"

let no_internet_message = "Please check your internet connection"
let upload_success_message = "Upload Successful"
let delete_success_message = "Delete Successful"
let download_success_message = "Download Successful"

//====================================================//

import AWSS3

typealias progressBlock = (_ progress: Double) -> Void
typealias completionBlock = (_ response: Any?, _ error: Error?) -> Void
typealias CompletionHandler = (_ success:Bool) -> Void


class AWSS3Manager {
    static let shared = AWSS3Manager()
    
    private init () { }
    
    let bucketName = UserDefaults.standard.string(forKey: "AWS_BUCKET_NAME") ?? ""
    // Upload image using UIImage object
    func uploadImage(image: UIImage, progress: progressBlock?, completion: completionBlock?) {
        guard let imageData = image.jpegData(compressionQuality: 0.5) else {
            let error = NSError(domain:"", code:402, userInfo:[NSLocalizedDescriptionKey: "invalid image"])
            completion?(nil, error)
            return
        }
        let tmpPath = NSTemporaryDirectory() as String
        let fileName: String = ProcessInfo.processInfo.globallyUniqueString + (".jpeg")
        let filePath = tmpPath + "/" + fileName
        let fileUrl = URL(fileURLWithPath: filePath)
        do {
            try imageData.write(to: fileUrl)
            self.uploadfile(fileUrl: fileUrl, fileName: "BharatPhoton/ios/\(fileName)", contenType: "image", progress: progress, completion: completion)
        } catch {
            let error = NSError(domain:"", code:402, userInfo:[NSLocalizedDescriptionKey: "invalid image"])
            completion?(nil, error)
        }
    }
    // Upload video from local path url
    func uploadVideo(videoUrl: URL, progress: progressBlock?, completion: completionBlock?) {
        let fileName = self.getUniqueFileName(fileUrl: videoUrl)
        self.uploadfile(fileUrl: videoUrl, fileName: "BharatPhoton/ios/\(fileName)", contenType: "video", progress: progress, completion: completion)
    }
    // Upload audio from local path url
    func uploadAudio(audioUrl: URL, progress: progressBlock?, completion: completionBlock?) {
        let fileName = self.getUniqueFileName(fileUrl: audioUrl)
        self.uploadfile(fileUrl: audioUrl, fileName: "BharatPhoton/ios/\(fileName)", contenType: "audio", progress: progress, completion: completion)
    }
    // Upload files like Text, Zip, etc from local path url
    func uploadOtherFile(fileUrl: URL, conentType: String, progress: progressBlock?, completion: completionBlock?) {
        let fileName = self.getUniqueFileName(fileUrl: fileUrl)
        self.uploadfile(fileUrl: fileUrl, fileName: "BharatPhoton/ios/\(fileName)", contenType: conentType, progress: progress, completion: completion)
    }
    // Get unique file name
    func getUniqueFileName(fileUrl: URL) -> String {
        let strExt: String = "." + (URL(fileURLWithPath: fileUrl.absoluteString).pathExtension)
        return (ProcessInfo.processInfo.globallyUniqueString + (strExt))
    }
    //MARK:- AWS file upload
    // fileUrl : file local path url
    // fileName : name of file, like “SampleVImage.jpg” “SampleVideoClip.mp4”
    // contenType: file MIME type
    // progress: file upload progress, value from 0 to 1, 1 for 100% complete
    // completion: completion block when uplaoding is finish, we will get S3 url of upload file here
    private func uploadfile(fileUrl: URL, fileName: String, contenType: String, progress: progressBlock?, completion: completionBlock?) {
        // Upload progress block
        let expression = AWSS3TransferUtilityUploadExpression()
        //Mark ACL as public
        expression.setValue("public-read", forRequestParameter: "x-amz-acl")
        expression.setValue("public-read", forRequestHeader: "x-amz-acl")
        expression.progressBlock = {(task, awsProgress) in
            guard let uploadProgress = progress else { return }
            DispatchQueue.main.async {
                uploadProgress(awsProgress.fractionCompleted)
            }
        }
        // Completion block
        var completionHandler: AWSS3TransferUtilityUploadCompletionHandlerBlock?
        completionHandler = { (task, error) -> Void in
            DispatchQueue.main.async(execute: {
                if error == nil {
                    let url = AWSS3.default().configuration.endpoint.url
                    let publicURL = url?.appendingPathComponent(self.bucketName).appendingPathComponent(fileName)
                    // print(“Uploaded to:\(String(describing: publicURL))”)
                    if let completionBlock = completion {
                        completionBlock(publicURL?.absoluteString, nil)
                    }
                } else {
                    if let completionBlock = completion {
                        completionBlock(nil, error)
                    }
                }
            })
        }
        // Start uploading using AWSS3TransferUtility
        let awsTransferUtility = AWSS3TransferUtility.default()
        awsTransferUtility.uploadFile(fileUrl, bucket: bucketName, key: fileName, contentType: contenType, expression: expression, completionHandler: completionHandler).continueWith { (task) -> Any? in
            if let error = task.error {
                print("error is: \(error.localizedDescription)")
            }
            if let _ = task.result {
                // your uploadTask
                // print(“result is: \(result)”)
            }
            return nil
        }
    }
    func deletefile(deletefileName: String, completionHandler: @escaping CompletionHandler) {
        let s3 = AWSS3.default()
        let deleteObjectRequest = AWSS3DeleteObjectRequest()
        deleteObjectRequest!.bucket = aws_s3BucketName
        deleteObjectRequest!.key = deletefileName
        s3.deleteObject(deleteObjectRequest!).continueWith { (task:AWSTask) -> AnyObject? in
            if let error = task.error {
                print("Error occurred: \(error)")
                completionHandler(false)
                return nil
            }
            print("Deleted successfully.")
            completionHandler(true)
            return nil
        }
    }
    //MARK:- AWS file download
    // fileName : name of file, like “SampleVImage.jpg” “SampleVideoClip.mp4”
    // progress: file download progress, value from 0 to 1, 1 for 100% complete
    // completion: completion block when uplaoding is finish, we will get S3 url of upload file here
    private func downloadfile(fileName: String,progress: progressBlock?, completion: completionBlock?) {
        // Download progress block
        let expression = AWSS3TransferUtilityDownloadExpression()
        expression.progressBlock = {(task, awsProgress) in
            guard let downloadProgress = progress else { return }
            DispatchQueue.main.async {
                downloadProgress(awsProgress.fractionCompleted)
            }
        }
        // Completion block
        var completionHandler: AWSS3TransferUtilityDownloadCompletionHandlerBlock?
        completionHandler = {(task, location, data, error) -> Void in
            DispatchQueue.main.async(execute: {
                if ((error) != nil){
                    //NSLog(“Failed with error”)
                    print("Error: %@",error!);
                    if let completionBlock = completion {
                        completionBlock(nil, error)
                    }
                }
                else{
                    print("Success");
                    print(data as Any)
                    if let completionBlock = completion {
                        completionBlock(data, nil)
                    }
                }
            })
        }
        // Start downloading using AWSS3TransferUtility
        let awsTransferUtility = AWSS3TransferUtility.default()
        awsTransferUtility.downloadData(fromBucket: aws_s3BucketName, key: fileName, expression: expression, completionHandler: completionHandler).continueWith { (task) -> Any? in
            if let error = task.error {
                print("Error: %@",error.localizedDescription);
            }
            if let _ = task.result {
                //print(“Download Starting!”)
            }
            return nil;
        }
    }
    // Download single file from AWS S3 single bucket
    func downloadSingleFileFromAWS(fileName: String, progress: progressBlock?, completion: completionBlock?) {
        self.downloadfile(fileName: fileName, progress: progress, completion: completion)
    }
    // Download all files from AWS S3 single bucket
    func downloadAllFilesFromAWSBucket(progress: progressBlock?, completion: completionBlock?) {
        let s3 = AWSS3.default()
        let listRequest: AWSS3ListObjectsRequest = AWSS3ListObjectsRequest()
        listRequest.bucket = aws_s3BucketName
        var allFiles = [String]()
        s3.listObjects(listRequest).continueWith { (task) -> AnyObject? in
            if let error = task.error {
                print("Error occurred: \(error)")
                return nil
            }
            if ((task.result?.contents) != nil){
                for object in (task.result?.contents)! {
                    // print(“Object key = \(object.key!)”)
                    allFiles.append(object.key!)
                }
            }
            if let completionBlock = completion {
                completionBlock(allFiles, nil)
            }
            return nil
        }
    }
}
