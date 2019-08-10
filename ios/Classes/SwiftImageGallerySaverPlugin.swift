import Flutter
import UIKit

public class SwiftImageGallerySaverPlugin: NSObject, FlutterPlugin {
    var result: FlutterResult?;

    public static func register(with registrar: FlutterPluginRegistrar) {
      let channel = FlutterMethodChannel(name: "image_gallery_saver", binaryMessenger: registrar.messenger())
      let instance = SwiftImageGallerySaverPlugin()
      registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      self.result = result
      if call.method == "saveImageToGallery" {
          let bytes = ((call.arguments as! NSDictionary)["imageBytes"] as? FlutterStandardTypedData)!
          if let image = UIImage(data: bytes.data) {
              UIImageWriteToSavedPhotosAlbum(image, self, #selector(didFinishSaving(image:error:contextInfo:)), nil)
          }
      } else {
          result(FlutterMethodNotImplemented)
      }
    }


    /// finish saving，if has error，parameters error will not nill
    @objc func didFinishSaving(image: UIImage, error: NSError?, contextInfo: UnsafeMutableRawPointer?) {
        result?(error == nil)
    }
}
