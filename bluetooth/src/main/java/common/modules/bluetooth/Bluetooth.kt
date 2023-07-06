package common.modules.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import common.modules.bluetooth.impl.BluetoothDataCallback
import common.modules.bluetooth.impl.BluetoothState
import java.util.*

@SuppressLint("MissingPermission")
class Bluetooth(private val serviceID: String, private val characteristicCode: String, private val descriptorCode: String, private val dataCallback: BluetoothDataCallback) {
    private var bluetoothGatt: BluetoothGatt? = null       // 블루투스 Gatt
    private var bluetoothAdapter: BluetoothAdapter? = null  // Bluetooth Adapter
    private lateinit var device: BluetoothDevice            // 연결된 Bluetooth Device
    private val deniedPermission = arrayListOf<String>()    // 권한 목록

    /**
     * 블루투스 기능 사용 가능 단말 조회
     * -> onFailure 일 경우 알림 띄우고 Return 하도록 로직 구성 필요.
     */
    fun isPossibleUseBluetooth(ctx: Context, onFailure: (cause: String) -> Unit): Boolean {
        // OS 위치 이용을 허락한 경우 -> Bluetooth 는 위치를 사용함.
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            onFailure(ctx.getString(R.string.bluetooth_msg_permission_locate))
            return false
        }

        val bluetoothManager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        // 블루투스 사용할 수 없는 기기 ( Bluetooth 기능 없는 단말 )
        if (bluetoothAdapter == null) {
            onFailure(ctx.getString(R.string.bluetooth_msg_old_device))
            return false
        }

        bluetoothAdapter?.let { adapter ->
            // 블루투스 Off
            if (!adapter.isEnabled) {
                onFailure(ctx.getString(R.string.bluetooth_msg_turn_on))
                return false
            }
        }

        return true
    }

    /**
     * 권환 확인 하는 용도
     * -> deniedPermission Return
     * -> deniedPermission size != 0 일경우 deniedPermission 이용하여 권한 요청을 수행한다.
     */
    fun isPermissionDenied(ctx: Context): ArrayList<String> {
        val wholePermissionArrays: Array<String> = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // 권한 체크. (순환)
        for (permissions in wholePermissionArrays) {
            if (ContextCompat.checkSelfPermission(ctx, permissions) == PackageManager.PERMISSION_DENIED) {
                deniedPermission.add(permissions)
            }
        }

        return deniedPermission
    }

    /**
     * Device 스캔 후 연결하는 함수.
     */
    fun scanBluetoothDevice(ctx: Context, deviceName: String, onSuccess: () -> Unit, onFailure: (msg: String) -> Unit) {
        val state = object : BluetoothState {
            override fun bluetoothConnect() {
                onSuccess()
            }

            override fun bluetoothDisConnected() {
                onFailure(ctx.getString(R.string.bluetooth_msg_un_connect))
            }
        }

        gattCallback.setState(state)
        bluetoothAdapter?.let { adapter ->
            adapter.cancelDiscovery()   // 스캔중일 경우, 스캔을 중지한다.
            val filter = mutableListOf<ScanFilter>()
            val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    super.onScanResult(callbackType, result)
                    result?.let {
                        it.device.name?.let { name ->
                            // 검색된 디바이스 이름이 동일한 경우
                            if (name.lowercase() == deviceName) {
                                device = it.device                                                          // device 설정
                                bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)                // 검색 종료.
                                bluetoothAdapter?.cancelDiscovery()                                         // 스캔중인 스캔 중지.
                                device.connectGatt(ctx, true, gattCallback)                     // 연결
                            }
                        }
                    }
                }
            }

            adapter.bluetoothLeScanner.stopScan(scanCallback)
            adapter.bluetoothLeScanner.startScan(filter, settings, scanCallback)  // 스캔 시작

            // 10초 후 Device 검색하지 못한 경우 실패 처리.
            Handler(Looper.getMainLooper()).postDelayed({
                if (!this::device.isInitialized || bluetoothGatt == null) {
                    // Device 검색 실패시
                    adapter.bluetoothLeScanner.stopScan(scanCallback)
                    onFailure(ctx.getString(R.string.bluetooth_msg_fail_to_connect))
                }
            }, 10000)
        }
    }

    // Bluetooth 통신 수행.
    fun notifyBluetooth(isEnable: Boolean) {
        bluetoothGatt?.let { gatt ->
            val service = gatt.getService(UUID.fromString(serviceID))
            val ch = service.getCharacteristic(UUID.fromString(characteristicCode))
            gatt.setCharacteristicNotification(ch, isEnable)
            val descriptor = ch.getDescriptor(UUID.fromString(descriptorCode)) ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (isEnable) {
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
                }
            } else {
                descriptor.value = if (isEnable) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
                gatt.writeDescriptor(descriptor)
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        private lateinit var bluetoothStateInterface: BluetoothState

        fun setState(bluetoothState: BluetoothState) {
            this.bluetoothStateInterface = bluetoothState
        }

        // Connection 관련 변경점이 있는 경우
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt?.let {
                        bluetoothStateInterface.bluetoothConnect()
                        bluetoothGatt = it
                    }
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    bluetoothStateInterface.bluetoothDisConnected()
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            dataCallback.getData(String(value))
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            if (gatt != null && characteristic != null) {
                onCharacteristicChanged(gatt, characteristic, characteristic.value)
            }
        }
    }
}
