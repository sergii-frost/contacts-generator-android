package se.frost.contactsgenerator

import android.view.LayoutInflater
import android.view.ViewGroup
import smartadapter.viewholder.SmartViewHolder
import kotlinx.android.synthetic.main.list_item_country_counter.view.*
import se.frost.contactsgenerator.models.CountryContactsModel

class CountryContactsViewHolder(parentView: ViewGroup?) : SmartViewHolder<CountryContactsModel>(LayoutInflater.from(parentView?.context).inflate(R.layout.list_item_country_counter, parentView, false)) {

	override fun bind(item: CountryContactsModel?) {
		item?.let {
			itemView.countryNameTextView.text = it.countryName()
			itemView.countryContactsCountTextView.text = "${it.contacts.size}"
		}
	}

}