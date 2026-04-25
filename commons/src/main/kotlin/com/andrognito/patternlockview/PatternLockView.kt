package com.andrognito.patternlockview

import android.content.Context
import android.util.AttributeSet
import android.view.View

class PatternLockView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var correctStateColor: Int = 0
    var normalStateColor: Int = 0

    enum class PatternViewMode { CORRECT, WRONG }

    fun setViewMode(mode: PatternViewMode) {}
    fun clearPattern() {}

    // inner stub type for Dot to match usage
    class Dot
}

