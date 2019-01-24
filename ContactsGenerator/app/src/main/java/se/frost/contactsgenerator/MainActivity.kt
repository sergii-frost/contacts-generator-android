package se.frost.contactsgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import se.frost.contactsgenerator.extensions.FragmentTransition
import se.frost.contactsgenerator.extensions.initFragment
import se.frost.contactsgenerator.helpers.PermissionsHelper
import se.frost.contactsgenerator.models.CountryContactsModel

class MainActivity : AppCompatActivity(), ContactsPermissionsFragment.OnFragmentInterationListener, ContactsFragment.OnFragmentInteractionListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initFragments()
	}

	override fun onContactsPermissionsGranted() {
		showContactsUI()
	}

	override fun openCountryContacts(model: CountryContactsModel) {
		showCountryContacts(model)
	}

	override fun addContacts() {
		showAddContacts()
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

	private fun showCountryContacts(model: CountryContactsModel) {
		initFragment(CountryContactsFragment.newInstance(model), CountryContactsFragment.TAG, FragmentTransition.FRAGMENT_ADD_TO_POP_STACK, R.id.content_frame)
	}

	private fun showAddContacts() {
		initFragment(AddContactsFragment.newInstance(), AddContactsFragment.TAG, FragmentTransition.FRAGMENT_ADD_TO_POP_STACK, R.id.content_frame)
	}

}
