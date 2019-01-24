package se.frost.contactsgenerator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_add_contacts.*
import se.frost.contactsgenerator.extensions.toFlagEmoji
import se.frost.contactsgenerator.helpers.ContactsHelper
import se.frost.contactsgenerator.helpers.PhoneNumberFaker
import se.frost.contactsgenerator.models.ContactModel
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
		Array(100) { it }
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
		val readableRegions = regions?.map { "${it.toFlagEmoji()} ${Locale("", it).displayCountry}" }?.toMutableList() ?: return
		regionSpinner.adapter = ArrayAdapter<String>(context ,android.R.layout.simple_spinner_item, readableRegions)
				.apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

		amountSpinner.adapter = ArrayAdapter<Int>(context ,android.R.layout.simple_spinner_item, amounts)
				.apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
	}

	private fun initButtons() {
		addButton.setOnClickListener {
			ContactsHelper.addContacts(generatedContacts, context)
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