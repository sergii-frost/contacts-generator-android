package se.frost.contactsgenerator.core

import android.content.Context
import com.google.gson.Gson
import java.io.FileNotFoundException


object PhoneNumberFaker {

	private val DIGITS = Array(10) { "$it" }
	private val REGIONS_PATH = "regions"
	private val JSON_EXTENSION = ".json"

	@Throws(IllegalArgumentException::class)
	fun generateNumbers(region: String, amount: Int, context: Context): Array<String> {
		try {
			val regionData = Gson().fromJson(readJSONFromAsset(assetName(region), context), RegionDataModel::class.java)
			return Array(amount) {
				randomPhoneNumber(regionData.phones.random())
			}
		} catch (e: FileNotFoundException) {
			throw IllegalArgumentException("Region $region is not supported", e)
		}
	}

	private fun randomPhoneNumber(input: String): String {
		return input.replace("#".toRegex()) { DIGITS.random() }
	}

	private fun assetName(region: String): String {
		return "$REGIONS_PATH/${region.toLowerCase()}$JSON_EXTENSION"
	}

	@Throws(FileNotFoundException::class)
	fun readJSONFromAsset(filename: String, context: Context): String? {
        context.assets.open(filename).bufferedReader().use {
            val json = it.readText()
            it.close()
            return json
        }
    }

	fun getSupportedRegions(context: Context?): List<String>? {
		return context?.assets?.list(REGIONS_PATH)?.filter {
			it.endsWith(JSON_EXTENSION)
		}?.mapNotNull {
			it.substringBefore(JSON_EXTENSION)
		}
	}

}