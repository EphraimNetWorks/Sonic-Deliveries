package com.example.deliveryapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.Espresso.onView
import org.hamcrest.Matcher


class RecyclerViewInteraction<A> {

    private var viewMatcher: Matcher<View>? = null
    private var items: List<A>? = null

    constructor(viewMatcher: Matcher<View>){
        this.viewMatcher = viewMatcher
    }

    fun withItems(items: List<A>): RecyclerViewInteraction<A> {
        this.items = items
        return this
    }

    fun customCheck(itemViewAssertion: ItemViewAssertion<A>): RecyclerViewInteraction<A> {
        for (i in items!!.indices) {
            onView(viewMatcher)
                .perform(scrollToPosition<RecyclerView.ViewHolder>(i))
                .check(RecyclerItemViewAssertion(i, items!![i], itemViewAssertion))
        }
        return this
    }

    interface ItemViewAssertion<A> {
        fun itemViewCheck(item: A, view: View, e: NoMatchingViewException?)
    }

    companion object{

        fun <A> onRecyclerView(viewMatcher: Matcher<View>): RecyclerViewInteraction<A> {
            return RecyclerViewInteraction(viewMatcher)
        }
    }
}