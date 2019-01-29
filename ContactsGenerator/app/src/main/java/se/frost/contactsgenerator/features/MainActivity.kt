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

package se.frost.contactsgenerator.features

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import se.frost.contactsgenerator.R
import se.frost.contactsgenerator.extensions.FragmentTransition
import se.frost.contactsgenerator.extensions.canNavigateBack
import se.frost.contactsgenerator.extensions.getVisibleFragment
import se.frost.contactsgenerator.extensions.initFragment
import se.frost.contactsgenerator.features.contacts.CountryContactsFragment
import se.frost.contactsgenerator.features.countries.CountriesFragment
import se.frost.contactsgenerator.features.countries.CountryContactsModel
import se.frost.contactsgenerator.helpers.PermissionsHelper

class MainActivity : AppCompatActivity(), ContactsPermissionsFragment.OnFragmentInterationListener, CountriesFragment.OnFragmentInteractionListener {

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

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		when (item?.itemId) {
			android.R.id.home -> {
				onBackPressed()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun initFragments() {
		supportFragmentManager.addOnBackStackChangedListener {
			getVisibleFragment(R.id.content_frame)?.userVisibleHint = true
			supportActionBar?.setDisplayHomeAsUpEnabled(canNavigateBack())
			invalidateOptionsMenu()
		}

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
		initFragment(CountriesFragment.newInstance(), CountriesFragment.TAG, FragmentTransition.FRAGMENT_REPLACE, R.id.content_frame)
	}

	private fun showCountryContacts(model: CountryContactsModel) {
		initFragment(CountryContactsFragment.newInstance(model), CountryContactsFragment.TAG, FragmentTransition.FRAGMENT_ADD_TO_POP_STACK, R.id.content_frame)
	}

	private fun showAddContacts() {
		initFragment(AddContactsFragment.newInstance(), AddContactsFragment.TAG, FragmentTransition.FRAGMENT_ADD_TO_POP_STACK, R.id.content_frame)
	}

}
