package tk.zwander.fabricateoverlaysample.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

internal class MarginItemDecoration : ItemDecoration() {
    private val itemMargin: Int = 2.dpToPx()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position != state.itemCount - 1) {
            outRect.bottom = itemMargin
        }
    }
}