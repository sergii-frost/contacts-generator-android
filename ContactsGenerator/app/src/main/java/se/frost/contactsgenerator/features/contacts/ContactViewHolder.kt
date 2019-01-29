package se.frost.contactsgenerator.features.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_contact.view.*
import se.frost.contactsgenerator.R
import smartadapter.viewholder.SmartViewHolder

class ContactViewHolder(parentView: ViewGroup?) : SmartViewHolder<ContactModel>(LayoutInflater.from(parentView?.context).inflate(R.layout.list_item_contact, parentView, false)) {

	override fun bind(item: ContactModel?) {
		item?.let {
			itemView.contactName.text = it.name
			itemView.contactNumber.text = it.phone
			itemView.generatedMarkerImageView.visibility = (if (it.isGenerated) View.VISIBLE else View.GONE)
		}
	}
}