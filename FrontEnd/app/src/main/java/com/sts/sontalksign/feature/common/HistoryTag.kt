package com.sts.sontalksign.feature.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sts.sontalksign.R

class HistoryTag : ConstraintLayout{
    lateinit var layout: ConstraintLayout
    lateinit var tvTag: TextView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        getAttrs(attrs, defStyleAttr)
    }

    //초기화
    private fun init(context: Context?) {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_input_tag, this, false)
        addView(view)

        layout = findViewById(R.id.cl_tag)
        tvTag = findViewById(R.id.tv_tag)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTag)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle:Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTag, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val txtTitle = typedArray.getText(R.styleable.CustomTag_tagColor).toString()
        tvTag.background = ColorDrawable(Color.parseColor(txtTitle))
    }
}