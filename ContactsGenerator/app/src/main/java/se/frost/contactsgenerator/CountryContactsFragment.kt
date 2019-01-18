package se.frost.contactsgenerator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_country_contacts.*
import se.frost.contactsgenerator.models.ContactModel
import se.frost.contactsgenerator.models.CountryContactsModel
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