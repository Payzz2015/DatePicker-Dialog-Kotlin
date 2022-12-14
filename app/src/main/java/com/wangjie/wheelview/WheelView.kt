package com.wangjie.wheelview

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.wangjie.wheelview.sample.MainActivity
import android.graphics.RectF





class WheelView(context: Context, attrs: AttributeSet?) : ScrollView(context, attrs) {

    companion object {
        const val OFF_SET_DEFAULT = 1
        private const val SCROLL_DIRECTION_UP = 0
        private const val SCROLL_DIRECTION_DOWN = 1
    }


    open class OnWheelViewListener {
        open fun onSelected(index: Int, item: String?) {

        }

        open fun onClickItem(index: Int, item: String?) {

        }
    }

    private var views: LinearLayout? = null

    private var items = ArrayList<String>()
    fun getItems(): List<String> {
        return items
    }

    fun setItems(list: List<String>?) {
        items.clear()
        items.addAll(list!!)

        for (i in 0 until offset) {
            items.add(0, "")
            items.add("")
        }
        initData()
    }
    private var scrollerTask: Runnable? = null

    var offset = OFF_SET_DEFAULT
    private var displayItemCount  = 0
    private var currentIndex = 1
    private var initialY = 0
    private var newCheck = 50
    private var itemHeight = 0
    private var selectedAreaBorder: IntArray? = null
    private var scrollDirection = -1
    private var paint: Paint? = null
    private var end: Paint? = null
    private var viewWidth = 0
    var onWheelViewListener: OnWheelViewListener? = null

    val selectedItem: String
        get() = items[currentIndex]
    val selectedIndex: Int
        get() = currentIndex/* - offset*/

    init {
        this.isVerticalScrollBarEnabled = false
        views = LinearLayout(context)
        views!!.orientation = LinearLayout.VERTICAL
        this.addView(views)
        scrollerTask = Runnable {
            val newY = scrollY
            if (initialY - newY == 0) {
                val remainder = initialY % itemHeight
                val divided = initialY / itemHeight
                if (remainder == 0) {
                    currentIndex = divided + offset
                    onSelectedCallBack()
                } else {
                    if (remainder > itemHeight / 2) {
                        post {
                            smoothScrollTo(0, initialY - remainder + itemHeight)
                            currentIndex = divided + offset + 1
                            onSelectedCallBack()
                        }
                    } else {
                        post {
                            smoothScrollTo(0, initialY - remainder)
                            currentIndex = divided + offset
                            onSelectedCallBack()
                        }
                    }
                }
            } else {
                initialY = scrollY
                postDelayed(scrollerTask, newCheck.toLong())
            }
        }
    }


    private fun startScrollerTask() {
        initialY = scrollY
        postDelayed(scrollerTask, newCheck.toLong())
    }



    private fun initData() {
        displayItemCount = offset * 2 + 1
        for (i in items.indices) {
            views!!.addView(createView(items[i], i - offset))
        }
        refreshItemView(0)
    }



    private fun createView(item: String, index: Int): TextView {
        val tv = TextView(context)
        tv.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.isSingleLine = true
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        tv.text = item
        tv.tag = index
        tv.gravity = Gravity.CENTER
        val padding = dip2px(15f)
        tv.setPadding(padding, padding, padding, padding)
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv)
            views!!.layoutParams =
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount)
            val lp = this.layoutParams as LinearLayout.LayoutParams
            this.layoutParams = LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount)
        }
        tv.setOnClickListener { v ->
            val index = v.tag as Int
            onClickItemCallBack()
        }
        return tv
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        refreshItemView(t)
        scrollDirection = if (t > oldt) {
            SCROLL_DIRECTION_DOWN
        } else {
            SCROLL_DIRECTION_UP
        }
    }

    private fun refreshItemView(y: Int) {
        var position = y / itemHeight + offset
        val remainder = y % itemHeight
        val divided = y / itemHeight
        if (remainder == 0) {
            position = divided + offset
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1
            }
        }
        val childSize = views!!.childCount
        for (i in 0 until childSize) {
            val itemView = views!!.getChildAt(i) as TextView
            if (position == i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    itemView.setTextColor(Color.parseColor("#000000"))
                }
            } else {
                itemView.setTextColor(Color.parseColor("#000000"))
            }
        }
    }

    private fun obtainSelectedAreaBorder(): IntArray {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = IntArray(3)
            selectedAreaBorder!![0] = itemHeight * offset
            selectedAreaBorder!![1] = itemHeight * (offset + 1)
            selectedAreaBorder!![2] = itemHeight * (offset + 2)
        }
        return selectedAreaBorder!!
    }

    override fun setBackgroundDrawable(background1: Drawable?) {
        if (viewWidth == 0) {
            viewWidth = (context as Activity?)!!.windowManager.defaultDisplay.width
        }
        if (null == paint) {
            paint = Paint()
            paint!!.color = Color.parseColor("#5ebcc7")
            paint!!.strokeWidth = dip2px(3f).toFloat()
            paint!!.isAntiAlias = true
            paint!!.style = Paint.Style.STROKE

        }
        if (null == end) {
            end = Paint()
            end!!.color = Color.parseColor("#bbbbbb")
            end!!.strokeWidth = dip2px(1f).toFloat()
        }
        val background = object : Drawable() {
            override fun draw(canvas: Canvas) {
                /*canvas.drawOval(
                    (viewWidth / 15).toFloat(),
                    , ,
                    ,
                    paint!!
                )*/
                canvas.drawRoundRect(RectF(
                    (viewWidth / 15).toFloat(),
                    obtainSelectedAreaBorder()[0].toFloat(),
                    (viewWidth * 14 / 15).toFloat(),
                    obtainSelectedAreaBorder()[1].toFloat()),
                    100f, 125f, paint!!)

                canvas.drawLine(
                    (viewWidth / 15).toFloat(),
                    obtainSelectedAreaBorder()[2].toFloat(), (viewWidth * 14 / 15).toFloat(),
                    obtainSelectedAreaBorder()[2].toFloat(),
                    end!!
                )
            }

            override fun setAlpha(alpha: Int) {}
            override fun setColorFilter(cf: ColorFilter?) {}
            override fun getOpacity(): Int {
                return PixelFormat.TRANSLUCENT
            }
        }
        super.setBackgroundDrawable(background)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        setBackgroundDrawable(null)
    }

    private fun onSelectedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener!!.onSelected(currentIndex, items[currentIndex])
        }
    }

    private fun onClickItemCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener!!.onClickItem(currentIndex, items[currentIndex])
        }
    }


    fun setSelection(position: Int) {
        currentIndex = position + offset
        post {
            smoothScrollTo(0, position * itemHeight)
        }
    }




    override fun fling(velocityY: Int) {
        super.fling(velocityY / 3)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            startScrollerTask()
        }
        return super.onTouchEvent(ev)
    }

    private fun dip2px(dpValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun getViewMeasuredHeight(view: View): Int {
        val width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val expandSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        view.measure(width, expandSpec)
        return view.measuredHeight
    }


}
