package se.frost.contactsgenerator

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_contact.view.*
import se.frost.contactsgenerator.models.ContactModel
import smartadapter.viewholder.SmartViewHolder

class ContactViewHolder(parentView: ViewGroup?) : SmartViewHolder<ContactModel>(LayoutInflater.from(parentView?.context).inflate(R.layout.list_item_contact, parentView, false)) {

	override fun bind(item: ContactModel?) {
		item?.let {
			itemView.contactName.text = it.name
			itemView.contactNumber.text = it.phone
		}
	}
}