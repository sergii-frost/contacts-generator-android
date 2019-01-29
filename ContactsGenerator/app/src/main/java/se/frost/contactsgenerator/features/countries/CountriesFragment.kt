package se.frost.contactsgenerator.features.countries

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.android.synthetic.main.fragment_contacts.*
import se.frost.contactsgenerator.R
import se.frost.contactsgenerator.core.ContactsHelper
import se.frost.contactsgenerator.features.contacts.ContactModel
import smartadapter.SmartRecyclerAdapter
import java.util.*
import kotlin.collections.ArrayList

class CountriesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

	interface OnFragmentInteractionListener {
		fun openCountryContacts(model: CountryContactsModel)
		fun addContacts()
	}

	private var countryContacts: List<CountryContactsModel> = ArrayList()

	companion object {
		val TAG = CountriesFragment::class.java.canonicalName

		fun newInstance(): CountriesFragment {
			val args = Bundle()
			return CountriesFragment().apply {
				arguments = args
			}
		}
	}

	private var countriesAdapter: SmartRecyclerAdapter? = null
	private var listener: OnFragmentInteractionListener? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_contacts, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initUI()
	}

	override fun setUserVisibleHint(isVisibleToUser: Boolean) {
		super.setUserVisibleHint(isVisibleToUser)
		if (isVisibleToUser) {
			updateWithData()
		}
	}

	override fun onResume() {
		super.onResume()
		updateWithData()
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		listener = context as? OnFragmentInteractionListener
	}

	override fun onAttach(activity: Activity?) {
		super.onAttach(activity)
		listener = activity as? OnFragmentInteractionListener
	}

	override fun onDetach() {
		super.onDetach()
		listener = null
	}

	override fun onRefresh() {
		refreshContactsLayout.isRefreshing = true
		updateWithData()
		refreshContactsLayout.isRefreshing = false
	}

	private fun initUI() {
		initButtons()
		initRecyclerView()
		initSwipeToRefreshLayout()
	}

	private fun initButtons() {
		addContactsButton.setOnClickListener {
			listener?.addContacts()
		}
		deleteContactsButton.setOnClickListener {
			val count = ContactsHelper.deleteGeneratedContacts(context)
			updateWithData()
			Toast.makeText(context, String.format(getString(R.string.message_x_contacts_deleted_successfully), count), Toast.LENGTH_SHORT).show()
		}
	}

	private fun initRecyclerView() {
		val viewManager = LinearLayoutManager(context)
		contactsRecyclerView.layoutManager = viewManager
		countriesAdapter = SmartRecyclerAdapter.empty()
				.map(CountryContactsModel::class.java, CountryContactsViewHolder::class.java)
				.addViewEventListener { view, actionId, position ->
					listener?.openCountryContacts(countryContacts[position])
				}
				.into(contactsRecyclerView)
	}

	private fun initSwipeToRefreshLayout() {
		refreshContactsLayout.setOnRefreshListener(this)
	}

	private fun updateWithData() {
		Log.d(TAG, "Update with data")
		countryContacts = getCountryContacts(ContactsHelper.getAllContacts(context))
		countriesAdapter?.setItems(countryContacts)
	}


	private fun getCountryContacts(contacts: List<ContactModel>): List<CountryContactsModel> {
		val phoneNumberUtil = PhoneNumberUtil.getInstance()
		val countryContactsList: ArrayList<CountryContactsModel> = ArrayList()
		val countriesMap: HashMap<String, ArrayList<ContactModel>> = HashMap()
		contacts.filter {
			phoneNumberUtil.isPossibleNumber(it.phone, null)
		}.forEach {
			val phoneNumber = phoneNumberUtil.parse(it.phone, null)
			phoneNumber?.let { number ->
				phoneNumberUtil.getRegionCodeForNumber(number)?.let { region ->
					if (countriesMap.containsKey(region) && countriesMap.contains(region)) {
						countriesMap[region]?.add(it)
					} else {
						countriesMap.put(region, arrayListOf(it))
					}
				}
			}
		}

		countriesMap.entries.forEach {
			countryContactsList.add(CountryContactsModel(it.key, it.value.apply { sortBy { contactModel -> contactModel.name } }))
		}

		countryContactsList.sortByDescending { it.contacts.size }

		return countryContactsList
	}
}