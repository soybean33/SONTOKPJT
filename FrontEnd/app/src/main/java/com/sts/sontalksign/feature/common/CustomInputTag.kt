package com.sts.sontalksign.feature.common

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.sts.sontalksign.R

class CustomInputTag : FrameLayout {
    lateinit var layout: FrameLayout
    lateinit var tvInputTitle: TextView
    lateinit var etInputContent: EditText

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

        layout = findViewById(R.id.custom_input_tag_layout)
        tvInputTitle = findViewById(R.id.tv_input_tag_title)
        etInputContent = findViewById(R.id.et_input_tag_content)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomInput)
        setTypeArray(typeArray)
    }

    private fun getAttrs(attrs:AttributeSet?, defStyle:Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomInput, defStyle, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val txtTitle = typedArray.getText(R.styleable.CustomInput_inputTitle)
        tvInputTitle.setText(txtTitle)
    }
}