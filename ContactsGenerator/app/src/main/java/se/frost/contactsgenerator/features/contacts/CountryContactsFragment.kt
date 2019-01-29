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

package se.frost.contactsgenerator.features.contacts

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_country_contacts.*
import se.frost.contactsgenerator.R
import se.frost.contactsgenerator.core.ContactModel
import se.frost.contactsgenerator.features.countries.CountryContactsModel
import smartadapter.SmartRecyclerAdapter

class CountryContactsFragment: Fragment() {

	private var countryContactsModel: CountryContactsModel? = null

	companion object {
		val TAG = CountryContactsFragment::class.java.canonicalName
		private val MODEL_ARG = "CountryContactsModel"

		fun newInstance(model: CountryContactsModel): CountryContactsFragment {
			val args = Bundle()
			args.putSerializable(MODEL_ARG, model)

			return CountryContactsFragment().apply {
				arguments = args
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.fragment_country_contacts, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initFromArguments()
		initRecyclerView()
	}

	private fun initFromArguments() {
		countryContactsModel = arguments?.getSerializable(MODEL_ARG) as? CountryContactsModel
	}

	private fun initRecyclerView() {
		val viewManager = LinearLayoutManager(context)
		countryContactsRecyclerView.layoutManager = viewManager
		countryContactsModel?.let {
			SmartRecyclerAdapter
					.items(it.contacts)
					.map(ContactModel::class.java, ContactViewHolder::class.java)
					.into(countryContactsRecyclerView)
		}
	}
}