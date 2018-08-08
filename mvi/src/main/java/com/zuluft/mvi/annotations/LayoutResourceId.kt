package com.zuluft.mvi.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LayoutResourceId(
        val value: Int
)