@file:Suppress("MemberVisibilityCanBePrivate")

package com.zuluft.mvi.presenters

import com.zuluft.mvi.actions.Action
import com.zuluft.mvi.actions.NavigatorAction
import com.zuluft.mvi.actions.ViewStateAction
import com.zuluft.mvi.views.BaseView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject


@Suppress("unused")
abstract class BasePresenter<ViewState : Any, View : BaseView<ViewState>> {

    private var view: View? = null
    private var isFirstAttach: Boolean = true
    private val viewStateSubject: PublishSubject<ViewState> = PublishSubject.create()
    private val perPresenterDisposables: CompositeDisposable = CompositeDisposable()
    private val perViewDisposables: CompositeDisposable = CompositeDisposable()
    private var lastState: ViewState? = null

    private var isLastStateShared = false


    fun attach(view: View) {
        this.view = view
        if (isFirstAttach) {
            lastState = getInitialViewState()
        }
        this.view!!.subscribe(viewStateSubject)
        if (!isLastStateShared) {
            viewStateSubject.onNext(lastState!!)
            isLastStateShared = true
        }
        if (isFirstAttach) {
            onFirstAttach()
        }
        onAttach(isFirstAttach)
        isFirstAttach = false
    }

    abstract fun getInitialViewState(): ViewState

    abstract fun onFirstAttach()

    fun detach(destroy: Boolean) {
        view = null
        onDetach()
        perViewDisposables.clear()
        if (destroy) {
            onDestroy()
            perPresenterDisposables.clear()
        }
    }

    protected fun dispatchAction(action: Action) {
        when (action) {
            is ViewStateAction<*> -> {
                @Suppress("UNCHECKED_CAST")
                val viewStateAction = action as (ViewStateAction<ViewState>)
                shareState(viewStateAction)
            }
            is NavigatorAction<*> -> action.commitNavigatorAction()
        }
    }

    private fun shareState(viewStateAction: ViewStateAction<ViewState>) {
        val viewState = viewStateAction.newState(lastState!!)
        lastState = viewState
        isLastStateShared = if (view != null) {
            viewStateSubject.onNext(lastState!!)
            true
        } else {
            false
        }
    }

    protected fun onDestroy() {

    }

    protected fun onDetach() {

    }

    protected fun getView(): View {
        return view!!
    }

    fun registerPerPresenterDisposables(vararg disposables: Disposable) {
        perPresenterDisposables.addAll(*disposables)
    }

    fun registerPerViewDisposables(vararg disposables: Disposable) {
        perViewDisposables.addAll(*disposables)
    }

    abstract fun onAttach(isFirstAttach: Boolean)
}