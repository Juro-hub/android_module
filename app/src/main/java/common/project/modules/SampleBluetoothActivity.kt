package common.project.modules

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import common.modules.bluetooth.Bluetooth
import common.modules.bluetooth.impl.BluetoothDataCallback
import qrmodule.ScanQrCodeActivity

class SampleBluetoothActivity : AppCompatActivity() {

    private var bluetooth: Bluetooth? = null

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (element in grantResults) {
            if (element == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "권한 허용 해주세요", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    private val dataCallback = object : BluetoothDataCallback {
        override fun getData(data: String) {
            //TODO 데이터 획득 시 수행할 내용.

            // 데이터 획득 중지 처리 해야함
            bluetooth?.notifyBluetooth(false)    // 데이터 획득 중지
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_sample)

        bluetooth = Bluetooth(serviceID = "", characteristicCode = "", descriptorCode = "", dataCallback)

        // 필수 - 블루투스 사용 가능 단말 조회
        bluetooth?.let {
            if (!it.isPossibleUseBluetooth(this) { msg ->
                    //TODO ALERT
                    // msg -> 에러 관련 메시지
                    // 오래된 단말 / 블루투스 기능이 없는 단말일 경우
                }) {
                return
            }

            val permissions = it.isPermissionDenied(this)
            if (permissions.size != 0) {
                //onRequestPermissionsResult 참고.
                ActivityCompat.requestPermissions(this, permissions.toArray(arrayOf<String>()), 1000)
                return
            }

            // TODo loadingBar 관련 처리 필요.
            it.scanBluetoothDevice(this, "te", onSuccess = {
                Toast.makeText(this, "검색 성공", Toast.LENGTH_SHORT).show()
            }, onFailure = {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            })

            it.notifyBluetooth(true)     // 데이터 획득 -> dataCallback getData() 수행
            it.notifyBluetooth(false)    // 데이터 획득 중지
        }
    }
}