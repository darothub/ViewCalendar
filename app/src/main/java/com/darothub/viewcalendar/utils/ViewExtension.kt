package com.darothub.viewcalendar.com.darothub.viewcalendar.utils

import android.view.View

/**
 * Hide view
 * @return bool
 */
fun View.hide(): Boolean {
    if (this.visibility == View.VISIBLE || this.visibility == View.INVISIBLE) {
        this.visibility = View.GONE
        return true
    }
    return false
}

/**
 * Show view
 * @return bool
 */
fun View.show(): Boolean {
    if (this.visibility == View.INVISIBLE || this.visibility == View.GONE) {
        this.visibility = View.VISIBLE
        return true
    }
    return false
}