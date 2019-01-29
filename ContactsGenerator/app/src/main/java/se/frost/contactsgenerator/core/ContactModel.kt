package se.frost.contactsgenerator.core

import java.io.Serializable

data class ContactModel(val rawContactId: Long? = null, val name: String, val phone: String, val isGenerated: Boolean = false): Serializable