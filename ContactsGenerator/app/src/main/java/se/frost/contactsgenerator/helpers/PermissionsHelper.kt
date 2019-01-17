package se.frost.contactsgenerator.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import kotlin.collections.ArrayList

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