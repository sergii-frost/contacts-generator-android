/*
 * Created by Sergii @Frost° 29/1/2019
 *
 * Copyright (c) 2019.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package se.frost.contactsgenerator.core

import android.content.ContentProviderOperation
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.TextUtils


object ContactsHelper {

	val TAG = ContactsHelper::class.java.canonicalName

	private val PROJECTION: Array<String> = arrayOf(
			ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
			ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
			ContactsContract.CommonDataKinds.Phone.NUMBER
	)

	private val CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

	private val GROUP_TITLE = "ContactsGenerator"

	fun getAllContacts(context: Context?): List<ContactModel> {
		val contentResolver = context?.contentResolver ?: return emptyList()
		val generatedContactsIds = getContactsIdsWithinGroup(GROUP_TITLE, context)
		val contacts: ArrayList<ContactModel> = ArrayList()
		contentResolver.query(CONTENT_URI, PROJECTION, null, null, null)?.let {
			while (it.moveToNext()) {
				val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY))
				val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
				val id = it.getLong(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))

				if (!TextUtils.isEmpty(phoneNumber)) {
					contacts.add(ContactModel(id, name, phoneNumber, generatedContactsIds.contains(id)))
				}
			}
			it.close()
		}

		return contacts
	}

	fun getGeneratedContacts(context: Context?): List<ContactModel> {
		return getAllContactsWithinGroup(GROUP_TITLE, context)
	}

	private fun getAllContactsWithinGroup(groupTitle: String, context: Context?): List<ContactModel> {
		val contentResolver = context?.contentResolver ?: return emptyList()
		val contacts: ArrayList<ContactModel> = ArrayList()

		val contactIds = getContactsIdsWithinGroup(groupTitle, context)
		contactIds.forEach {
			val whereContactClause = "${Phone.RAW_CONTACT_ID}=$it"

			contentResolver.query(CONTENT_URI, null, whereContactClause, null, null)?.let { contactCursor ->
				while (contactCursor.moveToNext()) {

					val name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY))
					val phoneNumber = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

					val id = contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))

					if (!TextUtils.isEmpty(phoneNumber)) {
						contacts.add(ContactModel(id, name, phoneNumber, true))
					}
				}
				contactCursor.close()
			}
		}

		return contacts
	}

	fun deleteGeneratedContacts(context: Context?): Int {
		var count = 0
		val contentResolver = context?.contentResolver ?: return 0
		val generatedContactsIds = getContactsIdsWithinGroup(GROUP_TITLE, context)
		val idsString = generatedContactsIds.joinToString { "$it" }

		val projection = arrayOf(
				ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
		)

		val whereClause = "${ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID} in ($idsString)"

		contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, whereClause, null, null)?.let {
			while (it.moveToNext()) {
				count++
				val rawContactId = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID))
				val uri = Uri.withAppendedPath(ContactsContract.RawContacts.CONTENT_URI, rawContactId)
				contentResolver.delete(uri, null, null)
			}
			it.close()
		}

		return count
	}

	@Throws(IllegalArgumentException::class)
	fun generateContacts(region: String, count: Int, context: Context?): Array<ContactModel> {
		if (context == null) {
			return emptyArray()
		}

		val numbers = PhoneNumberFaker.generateNumbers(region, count, context)
		return Array(count) {
			ContactModel(null, String.format("%s %02d", region.toUpperCase(), it + 1), numbers[it], true)
		}
	}

	fun addContacts(contacts: Array<ContactModel>, context: Context?) {
		val contentResolver = context?.contentResolver ?: return
		val ops = ArrayList<ContentProviderOperation>()
		val groupId = getOrCreateContactsGroup(GROUP_TITLE, context)
				?: return
		contacts.forEach {
			//First, insert new contact and get its contact id
			val contentValues = ContentValues().apply {
				put(ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY, it.name)
				put(ContactsContract.RawContacts.DISPLAY_NAME_ALTERNATIVE, it.name)
			}
			val contactId = ContentUris.parseId(contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, contentValues))

			//Now, update contact with name
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, it.name)
					.build())

			//Now, update contact with phone number
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
					.withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
					.withValue(Phone.NUMBER, it.phone)
					.withValue(Phone.TYPE, Phone.TYPE_MOBILE)
					.build())

			//Now, update contact with group
			ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValue(ContactsContract.Data.RAW_CONTACT_ID, contactId)
					.withValue(ContactsContract.CommonDataKinds.StructuredName.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId)
					.build())
		}
		contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
	}

	private fun getOrCreateContactsGroup(title: String, context: Context?): Long? {
		var groupId: Long? = null

		val whereQuery = "${ContactsContract.Groups.TITLE}='$title'"

		context?.contentResolver?.query(ContactsContract.Groups.CONTENT_URI, arrayOf(ContactsContract.Groups._ID), whereQuery, null, null)?.let {
			if (it.count > 0) {
				it.moveToFirst()
				groupId = it.getLong(it.getColumnIndex(ContactsContract.Groups._ID))
			}
			it.close()
		}

		//Need to create new group
		if (groupId == null) {
			val contentValues = ContentValues().apply {
				put(ContactsContract.Groups.TITLE, title)
			}
			val groupUri = context?.contentResolver?.insert(ContactsContract.Groups.CONTENT_URI, contentValues)
			groupId = ContentUris.parseId(groupUri)
		}

		return groupId
	}

	private fun getContactsIdsWithinGroup(groupTitle: String, context: Context?): List<Long> {
		val contentResolver = context?.contentResolver ?: return emptyList()
		val groupId = getOrCreateContactsGroup(groupTitle, context)
				?: return emptyList()

		val projection = arrayOf(
				ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
				ContactsContract.Data.DISPLAY_NAME
		)

		val whereGroupClause = "${ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID}=$groupId " +
				"AND ${ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE}='${ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}'"

		val contactsIds: ArrayList<Long> = ArrayList()
		contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, whereGroupClause, null, null)?.let { groupCursor ->
			while (groupCursor.moveToNext()) {
				contactsIds.add(groupCursor.getLong(groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID)))
			}
			groupCursor.close()
		}

		return contactsIds
	}

}