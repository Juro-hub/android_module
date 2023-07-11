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
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import common.modules.qr.R
import common.modules.qr.databinding.ActivityScanQrCodeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * QrCode 스캔
 */
class ScanQrCodeActivity : AppCompatActivity() {
    private lateinit var bind: ActivityScanQrCodeBinding
    private val REQUEST_CODE_CAMERA = 5000
    private var isScan = true   // 스캔 완료 Flag

    // 카메라 권한 없을 경우 요청 결과 수신 처리
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA) {
            // 카메라 권한 요청했으나 거부할 경우.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                //TODO 안내 후 종료 필요.
            } else {
                initScanner()
            }
        }
    }

    // QR 스캔 Callback
    private val barcodeCallback = object : BarcodeCallback {
        // 결과 획득한 경우.
        override fun barcodeResult(result: BarcodeResult?) {
            CoroutineScope(Dispatchers.IO).launch {
                if (isScan) {
                    isScan = false
                    // Data 있는 경우.
                    if (result != null) {
                        try {
                            // QR 인식 일시정지.
                            runOnUiThread {
                                bind.scanQrCodeZxingScanner.pauseAndWait()
                            }

                            return@launch
                        } catch (e: Exception) {
                            //TODO Alert 관련
                            return@launch
                        }
                    }

                }
            }
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
        initData()

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

    private fun initLayout() {
        bind.qrBack.setOnClickListener {
            if (!isFinishing) {
                finish()
            }
        }
    }

    private fun initData() {
        //TODO 앱 전달 데이터 Init
    }

    private fun initScanner() {
        val decoratedQrScanner = findViewById<DecoratedBarcodeView>(R.id.scan_qr_code_zxing_scanner)
        decoratedQrScanner.setStatusText("") // QR 스캐너 하단 Prompt Message 제거.
        val viewfinderView = decoratedQrScanner.viewFinder
        viewfinderView.visibility = View.GONE

        bind.scanQrCodeZxingScanner.resume()
        bind.scanQrCodeZxingScanner.decodeSingle(barcodeCallback)
    }
}