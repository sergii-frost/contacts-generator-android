package se.frost.contactsgenerator.features.countries

import se.frost.contactsgenerator.extensions.toFlagEmoji
import se.frost.contactsgenerator.features.contacts.ContactModel
import java.io.Serializable
import java.util.*

data class CountryContactsModel(val countryCode: String, val contacts: List<ContactModel>): Serializable {

	fun countryName(): String {
		return "${countryCode.toFlagEmoji()} ${Locale("", countryCode).displayCountry}"
	}
}