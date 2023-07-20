package common.project.modules

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import common.modules.dialog.CustomDialog
import common.modules.dialog.DialogSetter
import common.modules.dialog.DialogSetter.Companion.NEGATIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.DialogSetter.Companion.NEGATIVE_BTN_TEXT_COLOR
import common.modules.dialog.DialogSetter.Companion.POSITIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.DialogSetter.Companion.POSITIVE_BTN_TEXT_COLOR

class SampleDialogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_dialog)

        // 색상 설정.
        DialogSetter().apply {
            POSITIVE_BTN_BACKGROUND_COLOR = "#2763ba"
            POSITIVE_BTN_TEXT_COLOR = "#ffffff"
            NEGATIVE_BTN_BACKGROUND_COLOR = "#FF0000"
            NEGATIVE_BTN_TEXT_COLOR = "#ffffff"
        }

        val dialog = CustomDialog(this)
        dialog.setMessage("서버에서 필요한 정보를 받아오는데 실패하였습니다.\n잠시 후 다시 시도해주세요.")
        dialog.apply {
            setInput()
            setNegativeButton("부정") {
                Toast.makeText(this@SampleDialogActivity, "부정", Toast.LENGTH_SHORT).show()
            }
            setPositiveButton("긍정") {
                Toast.makeText(this@SampleDialogActivity, getEditText(), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            show()
        }
    }
}