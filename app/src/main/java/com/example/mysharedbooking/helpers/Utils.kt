package com.example.mysharedbooking.helpers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setItemDecoratorWithPadding( padding: Int){

    this.addItemDecoration(
        object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                with(outRect) {
                    if (parent.getChildAdapterPosition(view) == 0) {
                        top = padding
                    }
                    left =  padding
                    right = padding
                    bottom = padding
                }
            }
        }
    )
}