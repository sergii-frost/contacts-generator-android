package se.frost.contactsgenerator.features.countries

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_country_counter.view.*
import se.frost.contactsgenerator.R
import smartadapter.viewholder.SmartViewHolder

class CountryContactsViewHolder(parentView: ViewGroup?) : SmartViewHolder<CountryContactsModel>(LayoutInflater.from(parentView?.context).inflate(R.layout.list_item_country_counter, parentView, false)) {

	override fun bind(item: CountryContactsModel?) {
		item?.let {
			itemView.countryNameTextView.text = it.countryName()
			itemView.countryContactsCountTextView.text = "${it.contacts.size}"
		}
	}

}