package com.frontiir.dasher.qr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View

import com.frontiir.dasher.R
import com.journeyapps.barcodescanner.CaptureManager

class CaptureActivity : Activity() {
    private var capture: CaptureManager? = null
    private var barcodeScannerView: com.journeyapps.barcodescanner.DecoratedBarcodeView? = null
    private var cameraFlashOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeScannerView = initializeContent()
        val torchEventListener = TorchEventListener(this)
        barcodeScannerView!!.setTorchListener(torchEventListener)

        // turn the flash on if set via intent
        val scanIntent = intent
        if (scanIntent.hasExtra(CAMERA_FLASH_ON)) {
            if (scanIntent.getBooleanExtra(CAMERA_FLASH_ON, false)) {
                barcodeScannerView!!.setTorchOn()
            }
        }

        barcodeScannerView!!.setStatusText("")

        capture = CaptureManager(this, barcodeScannerView!!)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.decode()
    }

    /**
     * Override to use a different layout.
     *
     * @return the DecoratedBarcodeView
     */
    protected fun initializeContent(): com.journeyapps.barcodescanner.DecoratedBarcodeView {
        setContentView(R.layout.layout_sacnner_view)
        return findViewById<View>(com.google.zxing.client.android.R.id.zxing_barcode_scanner) as com.journeyapps.barcodescanner.DecoratedBarcodeView
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true
        }
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {}

    fun toggleFlash(view: View) {
        if (cameraFlashOn) {
            barcodeScannerView!!.setTorchOff()
        } else {
            barcodeScannerView!!.setTorchOn()
        }
    }


    internal inner class TorchEventListener(private val activity: CaptureActivity) : com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener {

        override fun onTorchOn() {
            this.activity.cameraFlashOn = true
        }

        override fun onTorchOff() {
            this.activity.cameraFlashOn = false
        }
    }

    companion object {
        val CAMERA_FLASH_ON = "CAMERA_FLASH_ON"
    }
}
