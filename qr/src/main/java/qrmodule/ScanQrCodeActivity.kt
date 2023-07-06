package qrmodule

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import network.NetworkClient
import network.NetworkResParser
import network.NetworkURL.Companion.getSecurityUrl
import network.NetworkUtil
import org.json.JSONObject

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

                            //TODO 이후 처리 (앱별 개별)
                            networkProcess()
                            return@launch
                        } catch (e: Exception) {
                            //TODO Alert 관련
                            return@launch
                        }
                    }

                    //TODO 실패 처리
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
        decoratedQrScanner.setStatusText("") // QR 스캐너 하단 Prompt Message 제거. ( 바코드를 사각형 안에 비춰주세요. )
        val viewfinderView = decoratedQrScanner.viewFinder
        viewfinderView.visibility = View.GONE

        bind.scanQrCodeZxingScanner.resume()
        bind.scanQrCodeZxingScanner.decodeSingle(barcodeCallback)
    }

    private fun networkProcess() {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkUtil.showLoadingDialog(act = this@ScanQrCodeActivity, isShow = true) // 로딩 시작.

            //TODO
            val response = NetworkClient(getSecurityUrl() + "채워주세요", JSONObject().apply {}).executeRequest(userAgent = "")

            NetworkUtil.showLoadingDialog(act = this@ScanQrCodeActivity, isShow = false) // 로딩 종료.

            //TODO
            NetworkResParser.resParser(this@ScanQrCodeActivity, response, onSuccess = { _, _ ->
                runOnUiThread {
                    setResult(Activity.RESULT_OK)
                    Toast.makeText(this@ScanQrCodeActivity, "채워주세요", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }, onFailure = { _, _ ->
                runOnUiThread {
                    //TODO Alert 관련
                }
            })
        }
    }
}