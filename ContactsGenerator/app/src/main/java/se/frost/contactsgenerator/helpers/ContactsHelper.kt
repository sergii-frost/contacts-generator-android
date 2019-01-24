package se.frost.contactsgenerator.helpers

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.text.TextUtils
import se.frost.contactsgenerator.models.ContactModel
import java.lang.IllegalArgumentException
import java.util.*


object ContactsHelper {

	private val PROJECTION: Array<String> = arrayOf(
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
			ContactsContract.CommonDataKinds.Phone.NUMBER
	)

	private val CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

	fun getAllContacts(context: Context?): List<ContactModel> {
		val contacts: ArrayList<ContactModel> = ArrayList()
		context?.contentResolver?.query(CONTENT_URI, PROJECTION, null, null, null)?.let {
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
		context?.contentResolver?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)?.let {
			while (it.moveToNext()) {
				val lookupKey = it.getString(it.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
				val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
				context.contentResolver.delete(uri, null, null)
			}
			it.close()
		}
	}

	@Throws(IllegalArgumentException::class)
	fun generateContacts(region: String, count: Int, context: Context?): Array<ContactModel> {
		if (context == null) {
			return emptyArray()
		}

		val numbers = PhoneNumberFaker.generateNumbers(region, count, context)
		return Array(count) {
			ContactModel(String.format("%s %02d", region.toUpperCase(), it+1), numbers[it])
		}
	}

	fun addContacts(contacts: Array<ContactModel>, context: Context?) {
		//TODO: implement
	}
}