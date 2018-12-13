package com.frontiir.dasher.qr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity

import com.frontiir.dasher.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.CaptureActivity

class QRScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_qrscan)

        setUp()
    }

    fun setUp() {
        val integrator = IntentIntegrator(this)

        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt("")
        integrator.captureActivity = CaptureActivity::class.java
        integrator.setOrientationLocked(false)
        integrator.setCameraId(0)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        val scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            if (scanningResult != null) {
                val intent1 = Intent(NOTIFICATION)
                intent1.putExtra(KEY, KEY_VALUE)
                intent1.putExtra(MESSAGE, scanningResult.contents)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1)
                finish()
            }
        } else {
            finish()
        }
    }

    companion object {

        private val TAG = QRScanActivity::class.java.simpleName
        val NOTIFICATION = "utility.QRScanActivity.receiver"
        val KEY_VALUE = "QRScanActivity.utility"
        val KEY = "key"
        val MESSAGE = "message"
    }
}
