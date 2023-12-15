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
import java.io.ByteArrayOutputStream
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun captureScreenshot(scaleFactor: Float = 1f, activity: Activity): ByteArray? {
        try {
            val rootView = activity.window.decorView.rootView
            if (rootView.width <= 0 || rootView.height <= 0) return null

            val bitmapConfig = if (rootView.isHardwareAccelerated) Bitmap.Config.ARGB_8888 else Bitmap.Config.ARGB_8888
            val originalBitmap = Bitmap.createBitmap(rootView.width, rootView.height, bitmapConfig)
            val canvas = Canvas(originalBitmap)
            rootView.draw(canvas)

            val newWidth = (originalBitmap.width * scaleFactor).toInt()
            val newHeight = (originalBitmap.height * scaleFactor).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

            val stream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            if (scaledBitmap != originalBitmap) {
                originalBitmap.recycle()
            }
            scaledBitmap.recycle()

            return byteArray
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun cleanup() {
        coroutineScope.cancel()
    }
}