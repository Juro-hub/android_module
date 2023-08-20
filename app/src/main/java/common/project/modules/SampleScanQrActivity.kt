package common.project.modules

import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import common.modules.dialog.CustomDialog
import common.modules.dialog.util.DialogFunction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import qrmodule.QrUtil.Companion.playSound
import qrmodule.ScanQrCodeActivity

class SampleScanQrActivity : ScanQrCodeActivity() {
    override val barcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult?) {
            CoroutineScope(Dispatchers.IO).launch {
                // Data 있는 경우.
                if (result != null) {
                    // QR 인식 일시정지.
                    runOnUiThread {
                        bind.scanQrCodeZxingScanner.pauseAndWait()
                    }

                    playSound(this@SampleScanQrActivity, R.raw.detect_done)

                    runOnUiThread {
                        CustomDialog(this@SampleScanQrActivity).apply {
                            setMessage(result.result.text)
                            setPositiveButton("확인") {
                                dismiss()
                                scannerResume()
                            }

                            show()
                        }
                    }
                    return@launch
                }

                DialogFunction.finishActDialog(this@SampleScanQrActivity, "실패")
                return@launch
            }
        }

        override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {
        }
    }

    private fun scannerResume() {
        bind.scanQrCodeZxingScanner.apply {
            resume()
            decodeSingle(barcodeCallback)
        }
    }

    override fun permissionReject() {
        DialogFunction.finishActDialog(this, "권한이 없을 경우 앱 실행이 불가능합니다.")
    }
}