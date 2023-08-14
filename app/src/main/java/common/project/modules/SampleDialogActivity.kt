package common.project.modules

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import common.modules.dialog.CustomDialog
import common.modules.dialog.DialogRegexFilter
import common.modules.dialog.DialogSetter
import common.modules.dialog.DialogSetter.Companion.NEGATIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.DialogSetter.Companion.NEGATIVE_BTN_TEXT_COLOR
import common.modules.dialog.DialogSetter.Companion.POSITIVE_BTN_BACKGROUND_COLOR
import common.modules.dialog.DialogSetter.Companion.POSITIVE_BTN_TEXT_COLOR

class SampleDialogActivity : AppCompatActivity(), View.OnClickListener {
    private val dialog: CustomDialog by lazy {
        CustomDialog(this).apply {
            setTitle("Dialog Title")    // Title 세팅 없을 경우 미 노출
            setMessage("Dialog Message")
        }
    }

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

        findViewById<Button>(R.id.one_btn_dialog).setOnClickListener(this)
        findViewById<Button>(R.id.two_btn_dialog).setOnClickListener(this)
        findViewById<Button>(R.id.edit_text_dialog).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.one_btn_dialog -> {
                    dialog.apply {
                        setPositiveButton("Confirm") {
                            dialog.dismiss()
                        }
                        show()
                    }
                }

                R.id.two_btn_dialog -> {
                    dialog.apply {
                        setPositiveButton("Positive") {
                            //TODO Positive
                            dialog.dismiss()
                        }
                        setNegativeButton("Negative") {
                            //TODO Negative
                            dialog.dismiss()
                        }
                        show()
                    }
                }

                R.id.edit_text_dialog -> {
                    dialog.apply {
                        showEditText(DialogRegexFilter.DIALOG_REGEX_TYPE_PHONE)
                        setPositiveButton("Positive") {
                            if (checkRegex(DialogRegexFilter.DIALOG_REGEX_TYPE_PHONE)) {
                                dialog.dismiss()
                                Toast.makeText(this@SampleDialogActivity, "Your Phone Number :${dialog.getEditText()}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@SampleDialogActivity, "Please Enter PhoneNumber", Toast.LENGTH_SHORT).show()
                            }
                        }
                        show()
                    }
                }

                else -> {}
            }
        }
    }
}