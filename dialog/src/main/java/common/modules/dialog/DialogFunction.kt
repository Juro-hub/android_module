package common.modules.dialog

import android.app.Activity
import android.graphics.Color

class DialogFunction {
    companion object {
        private var dialog: CustomDialog? = null

        // 확인 버튼 Dialog
        fun confirmDialog(act: Activity, msg: String) {
            if (act.isFinishing) return

            dialog?.let {
                if (it.isShowing) return
            }

            dialog = CustomDialog(act)
            act.runOnUiThread {
                dialog?.apply {
                    setMessage(msg)
                    setPositiveButton("확인") {
                        dismiss()
                    }
                    show()
                }
            }
        }

        // 액티비티 종료 Dialog
        fun finishActDialog(act: Activity, msg: String) {
            if (act.isFinishing) return

            dialog?.let {
                if (it.isShowing) return
            }

            act.runOnUiThread {
                dialog = CustomDialog(act)
                dialog?.apply {
                    setMessage(msg)
                    setPositiveButton("확인") {
                        dismiss()
                        act.finish()
                    }

                    show()
                }
            }
        }

        // 앱 종료 Dialog
        fun finishAppDialog(act: Activity, msg: String) {
            if (act.isFinishing) return

            dialog?.let {
                if (it.isShowing) return
            }

            act.runOnUiThread {
                dialog = CustomDialog(act)
                dialog?.apply {
                    setMessage(msg)
                    setPositiveButton("확인") {
                        dismiss()
                        act.finishAffinity()
                    }

                    show()
                }
            }
        }

        // Dialog Color Setter 잘못 설정 시 def 로 색상 설정
        fun String.parseColor(defValue: String): Int {
            return try {
                Color.parseColor(this)
            } catch (e: java.lang.Exception) {
                Color.parseColor(defValue)
            }
        }
    }
}