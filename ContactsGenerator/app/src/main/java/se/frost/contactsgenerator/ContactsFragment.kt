package se.frost.contactsgenerator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.android.synthetic.main.fragment_contacts.*
import se.frost.contactsgenerator.helpers.ContactsHelper
import se.frost.contactsgenerator.models.ContactModel
import se.frost.contactsgenerator.models.CountryContactsModel
import smartadapter.SmartRecyclerAdapter
import java.util.*
import kotlin.collections.ArrayList

class ContactsFragment : Fragment() {

	interface OnFragmentInteractionListener {
		fun openCountryContacts(model: CountryContactsModel)
	}

	private var countryContacts: List<CountryContactsModel> = ArrayList()

	companion object {
		val TAG = ContactsFragment::class.java.canonicalName

		fun newInstance(): ContactsFragment {
			val args = Bundle()
			return ContactsFragment().apply {
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
		initRecyclerView()
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

	private fun updateWithData() {
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
			phoneNumber?.let {number ->
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
			countryContactsList.add(CountryContactsModel(it.key, it.value))
		}

		return countryContactsList
	}
}