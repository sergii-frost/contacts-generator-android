package se.frost.contactsgenerator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_contacts.*
import smartadapter.SmartRecyclerAdapter

class ContactsFragment : Fragment() {

	companion object {
		val TAG = ContactsFragment::class.java.canonicalName

		fun newInstance() : ContactsFragment {
			val args = Bundle()
			return ContactsFragment().apply {
				arguments = args
			}
		}
	}

	var countriesAdapter : SmartRecyclerAdapter? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_contacts, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRecyclerView()
	}

	private fun initRecyclerView() {
		val viewManager = LinearLayoutManager(context)
		contactsRecyclerView.layoutManager = viewManager
		val items = listOf(
				CountryContacts("SE", listOf("SE 001")),
				CountryContacts("GB", listOf("GB 001", "GB 002", "GB 003")),
				CountryContacts("NO", listOf("NO 001", "NO 002", "NO 003")),
				CountryContacts("CU", listOf("CU 001", "CU 002", "CU 003", "CU 004", "CU 005", "CU 006", "CU 007"))
		)
		countriesAdapter = SmartRecyclerAdapter.items(items)
				.map(CountryContacts::class.java, CountryContactsViewHolder::class.java)
				.into(contactsRecyclerView)
	}
}