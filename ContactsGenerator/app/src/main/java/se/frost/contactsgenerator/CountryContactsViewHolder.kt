package se.frost.contactsgenerator

import android.view.LayoutInflater
import android.view.ViewGroup
import smartadapter.viewholder.SmartViewHolder
import kotlinx.android.synthetic.main.list_item_country_counter.view.*

data class CountryContacts(val countryCode: String, val contacts: List<String>)

class CountryContactsViewHolder(parentView: ViewGroup?) : SmartViewHolder<CountryContacts>(LayoutInflater.from(parentView?.context).inflate(R.layout.list_item_country_counter, parentView, false)) {

	override fun bind(item: CountryContacts?) {
		item?.let {
			itemView.countryNameTextView.text = it.countryCode
			itemView.countryContactsCountTextView.text = "${it.contacts.size}"
		}
	}

}