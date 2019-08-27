import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class ImageGallerySaver {
  static const MethodChannel _channel =
      const MethodChannel('image_gallery_saver');

  /// save image to Gallery
  /// imageBytes can't null
  /// path: 外置存储路径
  static Future save(
    Uint8List imageBytes, {
    String fileName,
    String path,
  }) async {
    if(imageBytes == null){
      return false;
    }
    final result = await _channel.invokeMethod('saveImageToGallery', {
      'imageBytes': imageBytes,
      'fileName': fileName,
      'path': path,
    });
    return result;
  }
}
