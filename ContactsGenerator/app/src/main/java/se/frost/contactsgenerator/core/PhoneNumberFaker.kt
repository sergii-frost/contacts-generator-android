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