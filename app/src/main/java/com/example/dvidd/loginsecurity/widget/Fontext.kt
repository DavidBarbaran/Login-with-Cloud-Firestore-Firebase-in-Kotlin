package com.example.dvidd.loginsecurity.widget

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.Nullable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.example.dvidd.loginsecurity.R

/**
 * Created by dvidd on 30/03/2018.
 */

class FontText(context: Context, @Nullable attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    private var mFontPath: String? = null

    init {

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextView)
            mFontPath = typedArray.getString(R.styleable.TextView_typeface)
            if (mFontPath != null && !mFontPath!!.isEmpty()) {
                val typeface = Typeface.createFromAsset(context.assets, mFontPath)
                setTypeface(typeface)
            }
            transformationMethod = null
        }
    }
}
