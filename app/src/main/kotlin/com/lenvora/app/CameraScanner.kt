package com.lenvora.app
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
class CameraScanner(private val context:Context,private val owner:LifecycleOwner,private val view:PreviewView){
 private var capture:ImageCapture?=null
 fun start(){val f=ProcessCameraProvider.getInstance(context);f.addListener({val p=f.get();val preview=Preview.Builder().build().also{it.surfaceProvider=view.surfaceProvider};capture=ImageCapture.Builder().build();p.unbindAll();p.bindToLifecycle(owner,CameraSelector.DEFAULT_BACK_CAMERA,preview,capture)},ContextCompat.getMainExecutor(context))}
 suspend fun captureBitmap():Bitmap=suspendCancellableCoroutine{c->val x=capture?:return@suspendCancellableCoroutine c.resumeWithException(IllegalStateException("Camera is not ready"));x.takePicture(ContextCompat.getMainExecutor(context),object:ImageCapture.OnImageCapturedCallback(){override fun onCaptureSuccess(i:ImageProxy){try{if(c.isActive)c.resume(i.toBitmap())}catch(e:Exception){if(c.isActive)c.resumeWithException(e)}finally{i.close()}}override fun onError(e:ImageCaptureException){if(c.isActive)c.resumeWithException(e)}})}
}
