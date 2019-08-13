package com.example.deliveryapp.utils

import android.view.View
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.PerformException
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion


class RecyclerItemViewAssertion<A>(
    private val position: Int,
    private val item: A,
    private val itemViewAssertion: RecyclerViewInteraction.ItemViewAssertion<A>
) : ViewAssertion {

    override fun check(view: View, e: NoMatchingViewException?) {
        val recyclerView = view as RecyclerView
        val viewHolderForPosition = recyclerView.findViewHolderForLayoutPosition(position)
        if (viewHolderForPosition == null) {
            throw PerformException.Builder()
                .withActionDescription(toString())
                .withViewDescription(HumanReadables.describe(view))
                .withCause(IllegalStateException("No view holder at position: $position"))
                .build()
        } else {
            val viewAtPosition = viewHolderForPosition.itemView
            itemViewAssertion.itemViewCheck(item, viewAtPosition, e)
        }
    }
}