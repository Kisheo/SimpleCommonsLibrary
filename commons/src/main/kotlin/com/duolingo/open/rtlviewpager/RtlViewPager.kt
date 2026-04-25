package com.duolingo.open.rtlviewpager

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

// Lightweight local shim for Duolingo RtlViewPager to avoid external dependency.
open class RtlViewPager : ViewPager {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
}

