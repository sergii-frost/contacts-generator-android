package se.frost.contactsgenerator.helpers

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.text.TextUtils
import se.frost.contactsgenerator.ContactModel
import java.util.*


object ContactsHelper {

	fun getAllContacts(context: Context?): List<ContactModel> {
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

	fun deleteAllContacts(context: Context?) {
		context?.contentResolver?.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)?.let {
			while (it.moveToNext()) {
				val lookupKey = it.getString(it.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
				val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
				context.contentResolver.delete(uri, null, null)
			}
			it.close()
		}
	}
}