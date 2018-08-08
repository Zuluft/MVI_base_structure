package com.zuluft.mvi.views

import io.reactivex.Observable


interface BaseView<T : Any> {
    fun subscribe(continuousViewStateObservable: Observable<T>,
                  viewStateObservable: Observable<T>)
}