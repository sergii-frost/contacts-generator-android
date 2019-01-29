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
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_add_contacts.*
import se.frost.contactsgenerator.R
import se.frost.contactsgenerator.core.ContactModel
import se.frost.contactsgenerator.core.ContactsHelper
import se.frost.contactsgenerator.core.PhoneNumberFaker
import se.frost.contactsgenerator.extensions.toFlagEmoji
import se.frost.contactsgenerator.features.contacts.ContactViewHolder
import smartadapter.SmartRecyclerAdapter
import java.util.*

class AddContactsFragment : Fragment() {

	companion object {
		val TAG = AddContactsFragment::class.java.canonicalName

		fun newInstance(): AddContactsFragment {
			val args = Bundle()

			return AddContactsFragment().apply {
				arguments = args
			}
		}
	}

	private val regions by lazy { PhoneNumberFaker.getSupportedRegions(context) }

	private val amounts by lazy {
		Array(100) { it+1 }
	}

	private var adapter: SmartRecyclerAdapter? = null

	private var generatedContacts: Array<ContactModel> = emptyArray()
		set(value) {
			field = value
			adapter?.setItems(field.toMutableList())
		}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_add_contacts, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initUI()
	}

	private fun initUI() {
		initSpinners()
		initButtons()
		initRecyclerView()
	}

	private fun initSpinners() {
		val safeContext = context ?: return

		val readableRegions = regions?.
				sortedBy { Locale("", it).displayCountry }?.
				map { "${it.toFlagEmoji()} ${Locale("", it).displayCountry}" }?.
				toMutableList() ?: return

		regionSpinner.adapter = ArrayAdapter<String>(safeContext,android.R.layout.simple_spinner_item, readableRegions)
				.apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

		amountSpinner.adapter = ArrayAdapter<Int>(safeContext ,android.R.layout.simple_spinner_item, amounts)
				.apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
	}

	private fun initButtons() {
		addButton.setOnClickListener {
			ContactsHelper.addContacts(generatedContacts, context)
			Toast.makeText(context, String.format(getString(R.string.message_x_contacts_added_successfully), generatedContacts.size), Toast.LENGTH_SHORT).show()
			generatedContacts = emptyArray()
		}
		generateButton.setOnClickListener {
			val region = regions?.get(regionSpinner.selectedItemPosition) ?: return@setOnClickListener
			val amount = amounts[amountSpinner.selectedItemPosition]
			generatedContacts = ContactsHelper.generateContacts(region, amount, context)
		}
	}

	private fun initRecyclerView() {
		val viewManager = LinearLayoutManager(context)
		generatedContactsPreviewRecyclerView.layoutManager = viewManager

		adapter = SmartRecyclerAdapter
				.items(generatedContacts.toMutableList())
				.map(ContactModel::class.java, ContactViewHolder::class.java)
				.into(generatedContactsPreviewRecyclerView)
	}
}