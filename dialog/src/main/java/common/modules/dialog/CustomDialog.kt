package common.modules.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.widget.Toast
import common.modules.dialog.data.DialogRegexFilter
import common.modules.dialog.data.DialogSetter.Companion.NEGATIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.data.DialogSetter.Companion.POSITIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.data.DialogSetter.Companion.POSITIVE_BTN_TEXT_COLOR
import common.modules.dialog.databinding.CustomDialogBinding
import common.modules.dialog.util.AddHyphenPhoneNumber
import common.modules.dialog.util.DialogFunction.Companion.parseColor
import common.modules.dialog.util.ExtensionOnClickListener
import java.util.regex.Pattern

//TODO Builder Pattern 고민해보기.
class CustomDialog(private val act: Activity) : Dialog(act) {
    private var regexFilter: DialogRegexFilter? = null

    private val bind: CustomDialogBinding by lazy {
        CustomDialogBinding.inflate(layoutInflater)
    }

    init {
        setContentView(bind.root)
        setCancelable(false)

        // 긍정 버튼/문구 색상 변경
        (bind.customDialogPositiveBtn.background as GradientDrawable).setColor(POSITIVE_BTN_BACKGROUND_COLOR.parseColor("#2763ba"))
        bind.customDialogPositiveBtn.setTextColor(POSITIVE_BTN_TEXT_COLOR.parseColor("#FFFFFF"))

        // 부정 버튼/문구 색상 변경
        (bind.customDialogNegativeBtn.background as GradientDrawable).setColor(NEGATIVE_BTN_BACKGROUND_COLOR.parseColor("#FF0000"))
        bind.customDialogNegativeBtn.setTextColor(POSITIVE_BTN_TEXT_COLOR.parseColor("#FFFFFF"))
    }

    override fun show() {
        if (isShowing) return

        if (act.isFinishing) return

        bind.customDialogInput.setText("")
        act.runOnUiThread {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            super.show()
        }
    }

    fun setTitle(title: String) {
        bind.customDialogTitle.text = title
        bind.customDialogTitleLayout.visibility = VISIBLE
    }

    // 다이얼로그 메시지 내용 설정
    fun setMessage(str: String) {
        bind.customDialogMessage.text = str
        val paint = bind.customDialogMessage.paint
        var message = bind.customDialogMessage.text as String

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
        bind.customDialogMessage.text = result
    }

    // 다이얼로그 긍정 버튼 설정
    fun setPositiveButton(text: String, listener: OnClickListener) {
        val dialogListener = OnClickListener {
            regexFilter?.let {
                if (!Pattern.compile(it.filter).matcher(bind.customDialogInput.text.toString()).find()) {
                    Toast.makeText(act, it.errorMessage, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }
            }

            if (isShowing) {
                dismiss()
            }
        }

        val extensionOnClickListener = ExtensionOnClickListener()
        extensionOnClickListener.add(listener)
        extensionOnClickListener.add(dialogListener)
        bind.customDialogPositiveBtn.apply {
            setText(text)
            setOnClickListener(extensionOnClickListener)
        }
    }

    // 다이얼로그 부정 버튼 설정
    fun setNegativeButton(text: String, listener: OnClickListener) {
        val dialogListener = OnClickListener {
            if (isShowing) {
                dismiss()
            }
        }

        val extensionOnClickListener = ExtensionOnClickListener()
        extensionOnClickListener.add(listener)
        extensionOnClickListener.add(dialogListener)
        bind.customDialogNegativeBtn.apply {
            visibility = VISIBLE
            setText(text)
            setOnClickListener(dialogListener)
        }
    }

    fun showEditText(filter: DialogRegexFilter? = null) {
        regexFilter = filter
        bind.customDialogInput.visibility = VISIBLE

        when (filter) {
            DialogRegexFilter.DIALOG_REGEX_TYPE_PHONE -> {
                bind.customDialogInput.addTextChangedListener(AddHyphenPhoneNumber(bind.customDialogInput))
                bind.customDialogInput.inputType = InputType.TYPE_CLASS_PHONE
            }
            DialogRegexFilter.DIALOG_REGEX_TYPE_NUMBER, DialogRegexFilter.DIALOG_REGEX_TYPE_POSITIVE_NUMBER -> {
                bind.customDialogInput.inputType = InputType.TYPE_CLASS_NUMBER
            }
            DialogRegexFilter.DIALOG_REGEX_TYPE_PASSWORD -> {
                bind.customDialogInput.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            else -> {}
        }
    }

    fun getEditText(): String {
        return bind.customDialogInput.text.toString()
    }
}