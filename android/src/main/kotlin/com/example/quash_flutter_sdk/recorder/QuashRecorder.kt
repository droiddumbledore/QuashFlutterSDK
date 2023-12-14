package com.example.quash_flutter_sdk.recorder

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class QuashRecorder {

    companion object {
        @Volatile
        private var INSTANCE: QuashRecorder? = null

        fun getInstance(): QuashRecorder {
            return INSTANCE ?: synchronized(this) {
                val instance = QuashRecorder()
                INSTANCE = instance
                instance
            }
        }

        private const val INTERVAL_MS = 100L
    }


    private val screenshotBuffer = QuashCircularBuffer<Bitmap>(450)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    private var lastScreenshot: Bitmap? = null

    private fun isBitmapDifferent(newBitmap: Bitmap, oldBitmap: Bitmap?): Boolean {
        try {
            if (oldBitmap == null) return true
            if (newBitmap.width != oldBitmap.width || newBitmap.height != oldBitmap.height) return true
            return !newBitmap.sameAs(oldBitmap)
        } catch (e: Exception) {
            return false
        }
    }

    private val screenshotTask = object : Runnable {
        override fun run() {
            captureScreenshot()?.let { newScreenshot ->
                if (isBitmapDifferent(newScreenshot, lastScreenshot)) {
                    screenshotBuffer.add(newScreenshot)
                    lastScreenshot = newScreenshot
                    //saveBitmapToScopedStorage(newScreenshot)
                    Log.e("TAG", "buffer: ${screenshotBuffer.getAll().size}")
                }
            }
            handler.postDelayed(this, INTERVAL_MS)
        }
    }

//    suspend fun createGifWithFFmpegAsync(outputPath: String): Uri? {
//        return withContext(Dispatchers.IO) {
//            try {
//                Log.e("TAG", "buffer: ${screenshotBuffer.getAll().size}")
//
//                val screenshots = screenshotBuffer.getAll()
//                val tempDir = File(Quash.getInstance().context.cacheDir, "temp_screenshots")
//                tempDir.mkdirs()
//
//                val savedScreenshots = screenshots.mapIndexed { index, bitmap ->
//                    val file = File(tempDir, "screenshot$index.jpg")
//                    val fos = FileOutputStream(file)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
//                    fos.close()
//                    bitmap.recycle()
//                    file.absolutePath
//                }
//                val inputPathPattern = "${tempDir.absolutePath}/screenshot%d.jpg"
//                val command =
//                    "-framerate 3 -i $inputPathPattern -vf 'scale=320:-1' -gifflags -transdiff -y $outputPath"
//                val rc = FFmpeg.execute(command)
//                if (rc != RETURN_CODE_SUCCESS) {
//                    Bitmap.Config.(Log.ERROR)
//                }
//                savedScreenshots.forEach { File(it).delete() }
//                Uri.parse(outputPath)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }

    // Compile screenshots to video
    fun start() {
        Log.e("TAG", "start: ")
        handler.post(screenshotTask)
    }

    fun stop() {
        Log.e("TAG", "stop: ")
        handler.removeCallbacks(screenshotTask)
    }

    fun clear(){
        Log.e("TAG", "clear: ")
        screenshotBuffer.clear()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun captureScreenshot(scaleFactor: Float = 1f, activity: Activity): Bitmap? {
        try {
            val currentActivity = activity ?: return null
            val rootView = currentActivity.window.decorView.rootView
            if (rootView.width <= 0 || rootView.height <= 0) return null // Check width and height before creating Bitmap
            val originalBitmap: Bitmap
            val canvas: Canvas
            if (rootView.isHardwareAccelerated) {
                // If hardware acceleration is enabled, create a Bitmap with software rendering
                originalBitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.RGB_565)
                canvas = Canvas(originalBitmap)
            } else {
                // If hardware acceleration is disabled, create a Bitmap with hardware rendering
                originalBitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
                canvas = Canvas(originalBitmap)
            }

            rootView.draw(canvas)

            val newWidth = (originalBitmap.width * scaleFactor).toInt()
            val newHeight = (originalBitmap.height * scaleFactor).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

            if (scaledBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            return scaledBitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun cleanup() {
        coroutineScope.cancel()
    }
}