package common.modules.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import common.modules.dialog.DialogFunction.Companion.parseColor
import common.modules.dialog.DialogSetter.Companion.NEGATIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.DialogSetter.Companion.POSITIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.DialogSetter.Companion.POSITIVE_BTN_TEXT_COLOR

class CustomDialog(private val act: Activity) : Dialog(act) {

    init {
        setContentView(R.layout.custom_dialog)
        setCancelable(false)

        // 긍정 버튼/문구 색상 변경
        (findViewById<AppCompatButton>(R.id.custom_dialog_positive_btn).background as GradientDrawable).setColor(POSITIVE_BTN_BACKGROUND_COLOR.parseColor("#2763ba"))
        (findViewById<TextView>(R.id.custom_dialog_positive_btn).setTextColor(POSITIVE_BTN_TEXT_COLOR.parseColor("#FFFFFF")))

        // 부정 버튼/문구 색상 변경
        (findViewById<AppCompatButton>(R.id.custom_dialog_negative_btn).background as GradientDrawable).setColor(NEGATIVE_BTN_BACKGROUND_COLOR.parseColor("#FF0000"))
        (findViewById<TextView>(R.id.custom_dialog_negative_btn).setTextColor(POSITIVE_BTN_TEXT_COLOR.parseColor("#FFFFFF")))


    }

    fun setTitle(title: String) {
        findViewById<TextView>(R.id.custom_dialog_title).apply {
            text = title
            visibility = VISIBLE
        }

        findViewById<View>(R.id.custom_dialog_divide).visibility = VISIBLE
    }

    // 다이얼로그 메시지 내용 설정
    fun setMessage(str: String) {
        val textView = findViewById<TextView>(R.id.custom_dialog_message)
        textView.text = str
        val paint = textView.paint
        var message = textView.text as String

        val frameWidth = paint.measureText(str) // 메시지 영역 Width

        // 기존 너비에 맞춰 \n 추가하는 로직
        //cf)https://rockdrumy.tistory.com/1104
        var startIndex = 0
        var endIndex = paint.breakText(message, true, frameWidth, null)
        var result = message.substring(startIndex, endIndex)

        while (true) {
            startIndex = endIndex
            message = message.substring(startIndex)
            if (message.isEmpty()) break

            endIndex = paint.breakText(message, true, frameWidth, null)
            result += "\n" + message.substring(0, endIndex)
        }

        // TextView 에 최종 결과물 설정.
        textView.text = result
    }

    // 다이얼로그 긍정 버튼 설정
    fun setPositiveButton(text: String, listener: OnClickListener) {
        findViewById<Button>(R.id.custom_dialog_positive_btn).apply {
            setText(text)
            setOnClickListener(listener)
        }
    }

    // 다이얼로그 부정 버튼 설정
    fun setNegativeButton(text: String, listener: OnClickListener) {
        findViewById<Button>(R.id.custom_dialog_negative_btn).apply {
            visibility = VISIBLE
            setText(text)
            setOnClickListener(listener)
        }
    }

    override fun show() {
        if (findViewById<TextView>(R.id.custom_dialog_title).text.toString().isEmpty()) {
            findViewById<RelativeLayout>(R.id.custom_dialog_title_layout).visibility = View.GONE
        }

        if (isShowing) return

        if (act.isFinishing) return

        act.runOnUiThread {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            super.show()
        }
    }
}
