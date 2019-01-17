package se.frost.contactsgenerator

import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.i18n.phonenumbers.PhoneNumberUtil
import kotlinx.android.synthetic.main.fragment_contacts.*
import se.frost.contactsgenerator.extensions.toFlagEmoji
import smartadapter.SmartRecyclerAdapter
import java.util.*

class ContactsFragment : Fragment() {

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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_contacts, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initRecyclerView()
		updateWithData()
	}

	private fun initRecyclerView() {
		val viewManager = LinearLayoutManager(context)
		contactsRecyclerView.layoutManager = viewManager
		countriesAdapter = SmartRecyclerAdapter.empty()
				.map(CountryContacts::class.java, CountryContactsViewHolder::class.java)
				.into(contactsRecyclerView)
	}

	private fun updateWithData() {
		countriesAdapter?.setItems(getCountryContacts(readContacts()))
	}

	private fun readContacts(): List<ContactModel> {
		val contacts: ArrayList<ContactModel> = ArrayList()
		context?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)?.let {
			while (it.moveToNext()) {
				val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY))
				val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

				if (!TextUtils.isEmpty(phoneNumber)) {
					contacts.add(ContactModel(name, phoneNumber))
				}
			}
			it.close()
		}

		return contacts
	}

	private fun getCountryContacts(contacts: List<ContactModel>): List<CountryContacts> {
		val phoneNumberUtil = PhoneNumberUtil.getInstance()
		val countryContacts: ArrayList<CountryContacts> = ArrayList()
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
			countryContacts.add(CountryContacts(it.key, it.value))
		}

		return countryContacts
	}
}