package com.zuluft.mvi.actions


abstract class ViewStateAction<ViewState> : Action {
    abstract fun newState(oldState: ViewState): ViewState

    open fun shouldBeSaved(): Boolean {
        return false
    }
}