package com.zuluft.mvi.activities

import android.os.Bundle
import com.zuluft.mvi.annotations.LayoutResourceId
import com.zuluft.impl.SafeFragmentTransactorActivity
import com.zuluft.mvi.presenters.BasePresenter
import com.zuluft.mvi.views.BaseView
import dagger.Lazy
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

@Suppress("unused")
abstract class BaseActivity<V : Any, P : BasePresenter<V, out BaseView<V>>>
    : SafeFragmentTransactorActivity() {

    private var presenter: P? = null
    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        val layoutResourceId = javaClass.getAnnotation(LayoutResourceId::class.java)
        if (layoutResourceId != null) {
            setContentView(layoutResourceId.value)
        }
        renderView(savedInstanceState)
        AndroidInjection.inject(this)
    }

    protected abstract fun renderView(savedInstanceState: Bundle?)

    @Suppress("UNCHECKED_CAST", "unused")
    @Inject
    fun setPresenter(lazy: Lazy<P>) {
        presenter = if (lastNonConfigurationInstance == null) {
            lazy.get()
        } else {
            lastOtherNonConfigInstance as P?
        }
        onPresenterReady(presenter!!)
    }

    protected abstract fun onPresenterReady(presenter: P)

    override fun onRetainOtherNonConfigInstance(): Any {
        return presenter!!
    }


    @Suppress("unused")
    fun subscribe(viewStateObservable: Observable<V>) {
        compositeDisposable!!.add(viewStateObservable.subscribe(this::reflectState))
    }

    protected abstract fun reflectState(state: V)

    override fun onDestroy() {
        presenter!!.detach(isFinishing)
        if (compositeDisposable != null) {
            compositeDisposable!!.dispose()
            compositeDisposable!!.clear()
        }
        super.onDestroy()
    }

    protected fun registerDisposables(vararg disposables: Disposable) {
        compositeDisposable!!.addAll(*disposables)
    }

}