package com.zuluft.mvi.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.zuluft.mvi.R
import com.zuluft.mvi.annotations.LayoutResourceId
import com.zuluft.mvi.presenters.BasePresenter
import com.zuluft.mvi.views.BaseView
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject


abstract class BaseDialogFragment<V : Any, P : BasePresenter<V, out BaseView<V>>> :
        DialogFragment() {

    private val compositeDisposable = CompositeDisposable()
    private var presenter: P? = null

    private val dialogsList: ArrayList<Dialog> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.layout_dialog_fragment_container,
                container, false)
        val widthPercent = getWidthPercent()
        val startPercent = (1f - widthPercent) / 2f
        val endPercent = 1f - startPercent
        val startGuideline = rootView.findViewById<Guideline>(R.id.startGuideline)
        val endGuideline = rootView.findViewById<Guideline>(R.id.endGuideline)
        startGuideline.setGuidelinePercent(startPercent)
        endGuideline.setGuidelinePercent(endPercent)
        val contentViewGroup = rootView.findViewById<ViewGroup>(R.id.flDialogContent)
        contentViewGroup.addView(createView(inflater, contentViewGroup))
        val window = dialog.window
        if (window != null) {
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)
        }
        if (isClosable()) {
            rootView.setOnTouchListener { _, _ ->
                dismissAndClearDialogs()
                true
            }
        }
        return rootView
    }

    protected open fun isClosable(): Boolean {
        return true
    }

    abstract fun getWidthPercent(): Float

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderView(view, savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            window?.setLayout(width, height)
        }
    }

    protected fun registerDisposables(vararg disposables: Disposable) {
        compositeDisposable.addAll(*disposables)
    }

    protected fun registerDialog(dialog: Dialog) {
        dialogsList.add(dialog)
    }

    protected fun isDialogShowing(): Boolean {
        for (dialog in dialogsList) {
            if (dialog.isShowing) {
                return true
            }
        }
        return false
    }

    protected fun dismissAndClearDialogs() {
        for (dialog in dialogsList) {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        dialogsList.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    protected abstract fun renderView(view: View?, savedInstanceState: Bundle?)

    @Suppress("RedundantVisibilityModifier", "unused")
    public fun subscribe(continuousViewStateObservable: Observable<V>,
                         viewStateObservable: Observable<V>) {
        compositeDisposable.add(viewStateObservable.subscribe(this::reflectState))
        compositeDisposable.add(continuousViewStateObservable.subscribe(this::reflectState))
    }

    protected abstract fun reflectState(state: V)

    @Suppress("UNCHECKED_CAST", "unused")
    @Inject
    fun setPresenter(lazy: Lazy<P>) {
        if (presenter == null) {
            presenter = lazy.get()
        }
        onPresenterReady(presenter!!)
    }

    protected abstract fun onPresenterReady(presenter: P)

    @Suppress("MemberVisibilityCanBePrivate")
    protected open fun createView(inflater: LayoutInflater,
                                  container: ViewGroup?): View? {
        var view: View? = null
        val layoutResourceId = javaClass.getAnnotation(LayoutResourceId::class.java)
        if (layoutResourceId != null) {
            view = inflater.inflate(layoutResourceId.value, container, false)
        }
        return view
    }

    override fun onDestroyView() {
        dismissAndClearDialogs()
        presenter?.detach(false)
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        presenter?.detach(true)
        super.onDestroy()
    }


    private fun showErrorMessageAlertDialog(errorMessage: String,
                                            onClickListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(context!!).setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, onClickListener)
                .show()
    }

    protected fun changeKeyboardAppearance(resize: Boolean) {
        (context as Activity).window
                .setSoftInputMode(when (resize) {
                    false -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    else -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                })
    }

    protected fun closeKeyboard() {
        val activity = context as Activity
        val view = activity.currentFocus
        if (view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}