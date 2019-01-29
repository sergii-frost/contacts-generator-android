package se.frost.contactsgenerator.features

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_contacts_permissions.*
import se.frost.contactsgenerator.R
import se.frost.contactsgenerator.helpers.PermissionsAction
import se.frost.contactsgenerator.helpers.PermissionsHelper

class ContactsPermissionsFragment : Fragment() {

	interface OnFragmentInterationListener {
		fun onContactsPermissionsGranted()
	}


	companion object {
		val TAG = ContactsPermissionsFragment::class.java.canonicalName

		fun newInstance() : ContactsPermissionsFragment {
			val args = Bundle()
			return ContactsPermissionsFragment().apply {
				arguments = args
			}
		}
	}

	var listener: OnFragmentInterationListener? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_contacts_permissions, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initUI()
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		if (context is OnFragmentInterationListener) {
			listener = context
		}
	}

	override fun onAttach(activity: Activity?) {
		super.onAttach(activity)
		if (activity is OnFragmentInterationListener) {
			listener = activity
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		PermissionsHelper.instance.notifyPermissionChange(permissions, grantResults)
	}

	private fun initUI() {
		contactsPermissionsAllowButton.setOnClickListener {
			if (PermissionsHelper.instance.hasContactsPermissions(context)) {
				listener?.onContactsPermissionsGranted()
				return@setOnClickListener
			}

			PermissionsHelper.instance.requestPermissionsForResult(this, PermissionsHelper.CONTACTS_PERMISSIONS_GROUP, object: PermissionsAction() {
				override fun onGranted() {
					listener?.onContactsPermissionsGranted()
				}

				override fun onDenied() {
					//Show message?
				}

				override fun onPermanentlyDenied() {
					//Show message?
				}
			})
		}
	}
}