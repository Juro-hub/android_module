package common.modules.dialog.util

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * 휴대폰 번호 입력 시 Hyphen(-) 추가
 */
class AddHyphenPhoneNumber(private val view: EditText) : TextWatcher {
    private var beforeLength = 0
    override fun beforeTextChanged(message: CharSequence?, start: Int, count: Int, after: Int) {
        // 메세지 있을경우.
        if (message != null) {
            // 메세지 길이 지정.
            beforeLength = message.length
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onTextChanged(message: CharSequence?, start: Int, before: Int, count: Int) {
        // 입력한 값이 있을경우만 진행.
        if (message != null) {
            if (message.isEmpty()) {
                return
            }

            val afterLength = message.length
            if (beforeLength < afterLength) {
                if (afterLength == 4 && !message.toString().contains("-")) {
                    view.setText(message.toString().substring(0,3) + "-" +message.toString()[3])
                    view.setSelection(5)
                } else if (afterLength == 9 && !message.toString().substring(8, 9).contains("-")) {
                    // 9 번째 글자 입력 시, 8번째에 - 이 없을 경우.
                    view.setText(message.toString().substring(0, 8) + "-" + message.toString()[8])
                    view.setSelection(10)
                }
            }
            view.setSelection(view.length())
        }
    }

    override fun afterTextChanged(s: Editable?) {
    }
}