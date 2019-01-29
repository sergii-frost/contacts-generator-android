/*
 * Created by Sergii @Frost° 29/1/2019
 *
 * Copyright (c) 2019.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package se.frost.contactsgenerator.extensions

import android.app.Activity
import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import se.frost.contactsgenerator.R

enum class FragmentTransition {
    FRAGMENT_ADD_TO_POP_STACK,
    FRAGMENT_ADD_TO_POP_STACK_VERTICAL,
    FRAGMENT_REPLACE
}

fun Activity.startNextActivity(nextActivityClazz: Class<*>) {
    startActivity(Intent(this, nextActivityClazz))
    finish()
}

fun AppCompatActivity.getVisibleFragment(@IdRes contentFrameRes: Int): Fragment? {
    return supportFragmentManager.findFragmentById(contentFrameRes)
}

fun AppCompatActivity.canNavigateBack(): Boolean {
    return supportFragmentManager.backStackEntryCount > 0
}

fun AppCompatActivity.initFragment(fragment: Fragment, tag: String, transition: FragmentTransition, @IdRes contentFrameRes: Int) {
    if (fragment.equals(getVisibleFragment(contentFrameRes))) {
        // Abort if same fragment is shown
        return
    }

    when (transition) {
        FragmentTransition.FRAGMENT_REPLACE -> {
            supportFragmentManager.popBackStack()
            if (supportFragmentManager.findFragmentByTag(tag) == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(contentFrameRes, fragment, tag)
                    .setTransition(FragmentTransaction.TRANSIT_NONE)
                    .commitAllowingStateLoss()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .show(fragment)
                    .commitAllowingStateLoss()
            }
        }
        FragmentTransition.FRAGMENT_ADD_TO_POP_STACK -> {
            supportFragmentManager
                .beginTransaction()
                .add(contentFrameRes, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(tag)
                .commitAllowingStateLoss()
        }
        FragmentTransition.FRAGMENT_ADD_TO_POP_STACK_VERTICAL -> {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.go_next_in_vert, R.anim.go_next_out_vert, R.anim.go_prev_in_vert, R.anim.go_prev_out_vert)
                .add(contentFrameRes, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(tag)
                .commitAllowingStateLoss()
        }
    }
    supportFragmentManager.executePendingTransactions()
    supportActionBar?.setDisplayHomeAsUpEnabled(canNavigateBack())
    invalidateOptionsMenu()
}

fun Activity.showLoading() {
    findViewById<View>(R.id.loadingIndicator)?.run { visibility = View.VISIBLE }
}

fun Activity.hideLoading() {
    findViewById<View>(R.id.loadingIndicator)?.run { visibility = View.GONE }
}