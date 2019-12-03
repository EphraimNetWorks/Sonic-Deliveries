package com.example.deliveryapp.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.deliveryapp.R
import kotlinx.android.synthetic.main.timeline_view.view.*

class TimelineView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0,
                                             defStyleRes: Int = 0): FrameLayout(context, attrs, defStyleAttr,defStyleRes) {

    private var markerImageview:ImageView
    private var startLine:View
    private var endLine:View

    init {
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

    fun setMarker(@DrawableRes drawableId: Int, @ColorRes markerTintId:Int){
        val drawable = VectorDrawableCompat.create(context.resources, drawableId, context.theme)
        markerImageview.setImageDrawable(drawable)
        ImageViewCompat.setImageTintList(markerImageview, ColorStateList.valueOf(ContextCompat.getColor(context,markerTintId)))
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