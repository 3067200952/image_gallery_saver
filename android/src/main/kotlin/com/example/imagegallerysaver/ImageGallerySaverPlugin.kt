package com.example.imagegallerysaver

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageGallerySaverPlugin(private val registrar: Registrar): MethodCallHandler {

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "image_gallery_saver")
      channel.setMethodCallHandler(ImageGallerySaverPlugin(registrar))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result): Unit {
    if (call.method == "saveImageToGallery") {
      var bytes = call.argument<ByteArray>("imageBytes")
      var name = call.argument<String>("fileName")
      var path = call.argument<String>("path")
      result.success(saveImageToGallery(BitmapFactory.decodeByteArray(bytes,0,bytes!!.size),name,path ))
    } else {
      result.notImplemented()
    }
  }

  private fun saveImageToGallery(bmp: Bitmap,name:String?, path:String?): Boolean {
    val context = registrar.activeContext().applicationContext
    
    var storePath = (Environment.getExternalStorageDirectory().absolutePath)
    if(path == null){
        storePath += (File.separator + getApplicationName())
    }else{
      storePath += path;
    }
    
    val appDir = File(storePath)
    if (!appDir.exists()) {
      appDir.mkdirs()
    }
    var fileName = name
    if(fileName == null){
      fileName = System.currentTimeMillis().toString() 
    }
    fileName = fileName + ".png"
    val file = File(appDir, fileName)
    try {
      val fos = FileOutputStream(file)
      val isSuccess = bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
      fos.flush()
      fos.close()
      val uri = Uri.fromFile(file)
      context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
      return isSuccess
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return false
  }

  private fun getApplicationName(): String {
    val context = registrar.activeContext().applicationContext
    var ai: ApplicationInfo? = null
    try {
        ai = context.packageManager.getApplicationInfo(context.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
    }
    var appName: String
    if (ai != null) {
      val charSequence = context.packageManager.getApplicationLabel(ai)
      appName = StringBuilder(charSequence.length).append(charSequence).toString()
    } else {
      appName = "image_gallery_saver"
    }
    return  appName
  }
}
