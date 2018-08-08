package com.zuluft.mvi.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.zuluft.mvi.R


class Loader(context: Context) : Dialog(context) {

    companion object {
        fun show(context: Context): Loader {
            var loaderDialog: Loader? = null
            try {
                loaderDialog = Loader(context)
                loaderDialog.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return loaderDialog!!
        }
    }

    override fun dismiss() {
        try {
            super.dismiss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_progress_dialog)
        setCancelable(false)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}