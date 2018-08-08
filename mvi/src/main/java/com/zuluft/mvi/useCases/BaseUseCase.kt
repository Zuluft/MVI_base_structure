package com.zuluft.mvi.useCases

import io.reactivex.Observable


abstract class BaseUseCase<out Repository, in Argument, ReturnType>
constructor(protected val repository: Repository) {
    abstract fun createObservable(arg: Argument? = null): Observable<ReturnType>
}