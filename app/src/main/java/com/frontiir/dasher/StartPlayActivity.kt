package com.frontiir.dasher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

import com.frontiir.dasher.qr.QRScanActivity

class StartPlayActivity : AppCompatActivity() {
    private var editText: EditText? = null
    private var btnUrl: Button? = null
    private var context: Context? = null
    private var igvQr: ImageView? = null

    private val QrReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
            editText!!.setText("");
            try {
                val bundle = intent.extras

                if (bundle != null) {

                    when (intent.getStringExtra(QRScanActivity.KEY)) {
                        QRScanActivity.KEY_VALUE -> editText!!.setText(intent.getStringExtra(QRScanActivity.MESSAGE))
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById<View>(R.id.edt_url) as EditText
        btnUrl = findViewById<View>(R.id.btn_url) as Button
        igvQr = findViewById<View>(R.id.igv_qr_camera) as ImageView
        editText!!.setText("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8");
        context = this
        btnUrl!!.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("URL", editText!!.text.toString())
            startActivity(intent)
        }

        igvQr!!.setOnClickListener {
            startActivity(Intent(context, QRScanActivity::class.java))
            LocalBroadcastManager.getInstance(context!!).registerReceiver(QrReceiver, IntentFilter(QRScanActivity.NOTIFICATION))
        }

    }

}
