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

package se.frost.contactsgenerator.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment

class PermissionsHelper private constructor() {

	private var actionsList: ArrayList<PermissionsAction> = ArrayList()

	companion object {
		val CONTACTS_PERMISSIONS_GROUP = arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)
		val instance: PermissionsHelper by lazy {
			PermissionsHelper()
		}
	}

	fun hasContactsPermissions(context: Context?) : Boolean {
		return CONTACTS_PERMISSIONS_GROUP.all { hasPermission(context, it) }
	}

	fun hasPermission(context: Context?, permission: String): Boolean {
		context?.let {
			return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
		}

		return false
	}

	fun requestPermissionsForResult(activity: Activity?, permissions: Array<String>, action: PermissionsAction) {
		val safeActivity = activity ?: return

		ActivityCompat.requestPermissions(safeActivity, permissions, 1)
		addAction(action)
	}

	fun requestPermissionsForResult(fragment: Fragment?, permissions: Array<String>, action: PermissionsAction) {
		fragment?.requestPermissions(permissions, 1)
		addAction(action)
	}

	fun notifyPermissionChange(permissions: Array<out String>, results: IntArray) {
		val size = if (results.size < permissions.size) results.size else permissions.size

		val iterator = actionsList.iterator()
		while (iterator.hasNext()) {
			iterator.next().apply {
				for (i in 0..size) {
					if (onResult(permissions[i], results[i])) {
						iterator.remove()
						break
					}
				}
			}
		}
	}

	private fun addAction(action: PermissionsAction) {
		actionsList.add(action)
	}

}

enum class PermissionsStatus {
	GRANTED,
	DENIED
}

abstract class PermissionsAction {

	private val mainLooper = Looper.getMainLooper()

	abstract fun onGranted()

	abstract fun onDenied()

	abstract fun onPermanentlyDenied()

	@Synchronized
	fun onResult(permission: String, result: Int): Boolean {
		return if (result == PackageManager.PERMISSION_GRANTED) {
			onResult(permission, PermissionsStatus.GRANTED)
		} else {
			onResult(permission, PermissionsStatus.DENIED)
		}
	}

	@Synchronized
	fun onResult(permission: String, status: PermissionsStatus): Boolean {
		if (PermissionsStatus.GRANTED === status) {
			Handler(mainLooper).post { this.onGranted() }

			return true

		} else if (PermissionsStatus.DENIED === status) {
			Handler(mainLooper).post { this.onDenied() }

			return true
		}

		return false
	}
}