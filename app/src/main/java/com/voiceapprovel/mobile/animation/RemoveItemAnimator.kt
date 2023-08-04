package com.voiceapprovel.mobile.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Ajay Vamsee on 8/4/2023.
 * Time : 12:23
 */
class RemoveItemAnimator : DefaultItemAnimator() {
    override fun onAnimationFinished(viewHolder: RecyclerView.ViewHolder) {
        super.onAnimationFinished(viewHolder)
        dispatchAnimationsFinished()
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        val view = holder.itemView

        view.animate()
            .translationX(view.width.toFloat())
            .alpha(0f)
            .setDuration(removeDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchRemoveFinished(holder)

                }
            })
            .start()
        return false
    }
}