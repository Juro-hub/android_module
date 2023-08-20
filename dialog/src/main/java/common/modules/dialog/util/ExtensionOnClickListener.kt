package common.modules.dialog.util

import android.view.View

class ExtensionOnClickListener : View.OnClickListener {
    private val listenerList = mutableListOf<View.OnClickListener>()

    fun add(listener: View.OnClickListener) {
        listenerList.add(listener)
    }
    override fun onClick(v: View?) {
        for (listener in listenerList) {
            listener.onClick(v)
        }
    }
}