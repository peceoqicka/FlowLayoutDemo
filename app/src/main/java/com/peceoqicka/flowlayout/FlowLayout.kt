package com.peceoqicka.flowlayout

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * <pre>
 *      author  :   Peceoqicka
 *      e-mail  :   ratchet@qq.com
 *      time    :   2018/6/20
 *      desc    :
 *      version :   1.0
 * </pre>
 */
class FlowLayout : ViewGroup {
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        getParams(context, attrs, defStyleAttr)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var adapter: BaseAdapter? = null
    private var columnSpace = 0
    private var rowSpace = 0
    private val lineHeightList = ArrayList<Int>()
    private var adapterDataSetObserver: AdapterDataSetObserver? = null

    private fun getParams(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        context.obtainStyledAttributes(attrs, R.styleable.FlowLayout, defStyleAttr, 0).apply {
            columnSpace = getDimensionPixelSize(R.styleable.FlowLayout_fl_columnSpace, 0)
            rowSpace = getDimensionPixelSize(R.styleable.FlowLayout_fl_rowSpace, 0)
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        /*
            1.EXACTLY       定长
            2.AT_MOST       给定一个最大长度
            3.UNSPECIFIED   要多少给多少
         */
        var width = if (widthMode == MeasureSpec.EXACTLY) widthSize else 0
        var height = if (heightMode == MeasureSpec.EXACTLY) heightSize else 0

        val availableWidth = widthSize - paddingStart - paddingEnd
        lineHeightList.clear()

        var maxLineWidth = 0
        var maxLineHeight = 0
        var lineWidth = 0
        var totalHeight = 0
        var columnIndex = 0
        var rowIndex = 0

        (0 until childCount).forEach {
            val childView = getChildAt(it)
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val layoutParams = childView.layoutParams as FlowLayout.LayoutParams

            val childWidth = childView.measuredWidth + layoutParams.marginStart + layoutParams.marginEnd
            val childHeight = childView.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin

            val predictLineWidth = lineWidth + (if (lineWidth == 0) childWidth else (columnSpace + childWidth))

            if (predictLineWidth <= availableWidth) {
                //不换行
                lineWidth = predictLineWidth
                maxLineHeight = Math.max(maxLineHeight, childHeight)

                layoutParams.layoutColumn = columnIndex
                layoutParams.layoutRow = rowIndex

                columnIndex++
            } else {
                //换行
                columnIndex = 0
                rowIndex++
                maxLineWidth = Math.max(lineWidth, maxLineWidth)
                totalHeight += (maxLineHeight + rowSpace)
                lineHeightList.add(maxLineHeight)

                layoutParams.layoutColumn = columnIndex
                layoutParams.layoutRow = rowIndex

                maxLineHeight = if (it == childCount - 1) maxLineHeight else 0
                lineWidth = childWidth
                columnIndex++
            }
        }

        totalHeight += maxLineHeight
        lineHeightList.add(maxLineHeight)

        if (widthMode != MeasureSpec.EXACTLY) {
            width = maxLineWidth
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            height = totalHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        (0 until childCount).forEach {
            val childView = getChildAt(it)
            val layoutParams = childView.layoutParams as FlowLayout.LayoutParams

            val cLeft = paddingStart + layoutParams.marginStart + calculateLeftPosition(it, layoutParams.layoutColumn)
            val cTop = paddingTop + calculateTopPosition(it, layoutParams.layoutRow)
            val cRight = cLeft + childView.measuredWidth
            val cBottom = cTop + childView.measuredHeight

            childView.layout(cLeft, cTop, cRight, cBottom)
        }
    }

    private fun calculateLeftPosition(layoutIndex: Int, colIndex: Int): Int {
        return if (colIndex > 0) {
            val previousChild = getChildAt(layoutIndex - 1)
            val previousLayoutParams = previousChild.layoutParams as FlowLayout.LayoutParams
            val previousWidth = previousChild.measuredWidth + previousLayoutParams.marginStart + previousLayoutParams.marginEnd
            columnSpace + previousWidth + calculateLeftPosition(layoutIndex - 1, colIndex - 1)
        } else {
            0
        }
    }

    private fun calculateTopPosition(layoutIndex: Int, rowIndex: Int): Int {
        return if (rowIndex > 0) {
            rowSpace + lineHeightList[rowIndex] + calculateTopPosition(layoutIndex - 1, rowIndex - 1)
        } else {
            0
        }
    }


    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return FlowLayout.LayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return FlowLayout.LayoutParams(context, attrs)
    }

    fun setAdapter(adapter: BaseAdapter?) {
        if (this.adapter !== adapter) {
            this.adapter?.let { a ->
                adapterDataSetObserver?.let { o ->
                    a.unregisterDataSetObserver(o)
                }
            }
            adapter?.let {
                adapterDataSetObserver = AdapterDataSetObserver()
                it.registerDataSetObserver(adapterDataSetObserver)

                this.adapter = it.apply {
                    (0 until this@apply.count).forEach {
                        val childView = this@apply.getView(it, null, this@FlowLayout)
                        addView(childView, generateLayoutParams(childView.layoutParams))
                    }
                }

                requestLayout()
            } ?: let {
                this.adapter = null
                removeAllViews()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if ((adapter != null) and (adapterDataSetObserver == null)) {
            adapterDataSetObserver = AdapterDataSetObserver()
            adapter?.registerDataSetObserver(adapterDataSetObserver)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        adapter?.let { a ->
            adapterDataSetObserver?.let { ob ->
                a.unregisterDataSetObserver(ob)
                adapterDataSetObserver = null
            }
        }
    }

    private fun resetData() {
        adapter?.run {
            removeAllViews()

            (0 until this.count).forEach {
                val childView = this.getView(it, null, this@FlowLayout)
                addView(childView, generateLayoutParams(childView.layoutParams))
            }
        }
    }

    inner class AdapterDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            resetData()
            requestLayout()
        }

        override fun onInvalidated() {
            resetData()
            requestLayout()
        }
    }

    open class LayoutParams : MarginLayoutParams {
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
        @Suppress("unused")
        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: ViewGroup.LayoutParams) : super(source)

        var layoutColumn = 0
        var layoutRow = 0
    }
}