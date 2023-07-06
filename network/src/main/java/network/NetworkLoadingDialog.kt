package network

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.ImageView
import com.bumptech.glide.Glide
import common.modules.network.R

/** 네트워크 수행 시 Dialog 띄어주는 Cls. */
class NetworkLoadingDialog(ctx: Context) : Dialog(ctx) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)

        val dialogImage = findViewById<ImageView>(R.id.network_loading_dialog)

        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

        if (context is ContextThemeWrapper || context is Activity) {
            val activity = if (context is ContextThemeWrapper) {
                (context as ContextThemeWrapper).baseContext as Activity
            } else {
                context as Activity
            }

            if (!activity.isFinishing) {
                activity.runOnUiThread {
                    Glide.with(context)
                            .asGif()
                            .optionalCenterInside()
                            .override(context.resources.getDimensionPixelSize(R.dimen.loading_gif_size))
                            .load(R.raw.default_loading)
                            .into(dialogImage)
                }
            }
        }
    }
}