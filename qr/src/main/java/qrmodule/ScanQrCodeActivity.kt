package qrmodule

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import common.modules.qr.databinding.ActivityScanQrCodeBinding

/**
 * QrCode 스캔
 */
open class ScanQrCodeActivity : AppCompatActivity() {
    open lateinit var bind: ActivityScanQrCodeBinding
    private val REQUEST_CODE_CAMERA = 5000

    // 카메라 권한 없을 경우 요청 결과 수신 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            // 카메라 권한 요청했으나 거부할 경우.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                permissionReject()
            } else {
                initScanner()
            }
        }
    }

    // 권한 없을 경우 호출.
    open fun permissionReject() {}

    // QR 스캔 Callback
    open val barcodeCallback = object : BarcodeCallback {
        // 결과 획득한 경우.
        override fun barcodeResult(result: BarcodeResult?) {
        }

        // 화면에 QR 인식된 경우
        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityScanQrCodeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        initLayout()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA)
        } else {
            initScanner()
        }
    }

    override fun onResume() {
        super.onResume()
        bind.scanQrCodeZxingScanner.resume()
        bind.scanQrCodeZxingScanner.decodeSingle(barcodeCallback)
    }

    override fun onPause() {
        super.onPause()
        bind.scanQrCodeZxingScanner.pauseAndWait()
    }

    override fun onDestroy() {
        super.onDestroy()
        bind.scanQrCodeZxingScanner.pauseAndWait()
    }

    fun setMessage(message: String) {
        bind.scanQrCodeMessage.text = message
    }

    private fun initScanner() {
        bind.scanQrCodeZxingScanner.setStatusText("")
        bind.scanQrCodeZxingScanner.viewFinder.visibility = View.GONE
    }

    private fun initLayout() {
        bind.qrBack.setOnClickListener {
            if (!isFinishing) {
                finish()
            }
        }
    }

}