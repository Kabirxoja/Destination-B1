package uz.kabirhoja.destination.ui.common

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * Extension function to apply uniform smooth scrolling and bottom padding
 * to RecyclerViews across all fragments.
 */
fun RecyclerView.setupSmoothScroll(bottomPaddingDp: Int = 80) {
    val paddingPx = (bottomPaddingDp * resources.displayMetrics.density).toInt()
    clipToPadding = false
    setPadding(paddingLeft, paddingTop, paddingRight, paddingPx)
}

/**
 * Extension function to enable smooth nested scrolling for NestedScrollView.
 */
fun NestedScrollView.setupSmoothScroll() {
    isNestedScrollingEnabled = true
    overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS
}
