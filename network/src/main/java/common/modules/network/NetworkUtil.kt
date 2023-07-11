package common.modules.network

import android.app.Activity
import android.util.Log

/** 네트워크 수행 시 사용되는 Func */
class NetworkUtil {
    companion object {
        // 네트워크 로딩 다이얼로그
        private var loadingDialog: NetworkLoadingDialog? = null

        // 네트워크 로딩 다이얼로그 띄우기.
        fun showLoadingDialog(act: Activity, isShow: Boolean) {
            act.runOnUiThread {
                try {
                    if (loadingDialog == null) {
                        loadingDialog = NetworkLoadingDialog(act)
                    }

                    loadingDialog?.let {
                        it.setCancelable(false)

                        // show 해야하는 경우.
                        if (isShow) {
                            // 보이지 않을 경우 -> show()
                            if (!it.isShowing) {
                                if (!act.isFinishing) {
                                    it.show()
                                }
                            }
                        } else {
                            if (it.isShowing) it.dismiss()
                            loadingDialog = null
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        Log.d("NetworkUtil: showLoadingDialog", it)
                    }
                }
            }
        }
    }
}