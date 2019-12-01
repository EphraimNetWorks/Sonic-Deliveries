package com.example.deliveryapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.deliveryapp.R
import kotlinx.android.synthetic.main.timeline_view.view.*

class TimelineView: FrameLayout {

    private lateinit var markerImageview:ImageView
    private lateinit var startLine:View
    private lateinit var endLine:View

    constructor (context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        val view = View.inflate(context, R.layout.timeline_view, null)
        markerImageview = view.timeline_marker
        startLine = view.timeline_top
        endLine = view.timeline_bottom

        addView(view)
    }

    fun initLine(lineViewType : Int){
        when(lineViewType){
            ONLY_ITEM->{
                startLine.visibility = View.GONE
                endLine.visibility = View.GONE
            }
            FIRST-> {
                startLine.visibility = View.GONE
                endLine.visibility = View.VISIBLE
            }
            LAST-> {
                startLine.visibility = View.VISIBLE
                endLine.visibility = View.GONE
            }
            else -> {
                startLine.visibility = View.VISIBLE
                endLine.visibility = View.VISIBLE
            }
        }
    }

    fun setStartLineColor(color:Int){
        startLine.setBackgroundColor(color)
    }

    fun setEndLineColor(color:Int){
        endLine.setBackgroundColor(color)
    }

    fun setMarker(drawable: Drawable){
        markerImageview.setImageDrawable(drawable)
    }

    companion object{
        private const val ONLY_ITEM = -1
        private const val FIRST = 0
        private const val MID = 1
        private const val LAST = 2

        fun getTimeLineViewType(position:Int, count:Int)= when{
            count == 1 -> ONLY_ITEM
            position == 0 -> FIRST
            position == count-1 -> LAST
            else -> MID
        }
    }

}