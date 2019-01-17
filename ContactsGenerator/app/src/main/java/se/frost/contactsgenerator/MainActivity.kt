package se.frost.contactsgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import se.frost.contactsgenerator.extensions.ContactsPermissionsFragment
import se.frost.contactsgenerator.extensions.FragmentTransition
import se.frost.contactsgenerator.extensions.initFragment
import se.frost.contactsgenerator.helpers.PermissionsHelper

class MainActivity : AppCompatActivity(), ContactsPermissionsFragment.OnFragmentInterationListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initFragments()
	}

	override fun onContactsPermissionsGranted() {
		showContactsUI()
	}

	private fun initFragments() {
		if (PermissionsHelper.instance.hasContactsPermissions(applicationContext)) {
			showContactsUI()
		} else {
			showPermissionsUI()
		}
	}

	private fun showPermissionsUI() {
		initFragment(ContactsPermissionsFragment.newInstance(), ContactsPermissionsFragment.TAG, FragmentTransition.FRAGMENT_REPLACE, R.id.content_frame)
	}

	private fun showContactsUI() {
		initFragment(ContactsFragment.newInstance(), ContactsFragment.TAG, FragmentTransition.FRAGMENT_REPLACE, R.id.content_frame)
	}

}
