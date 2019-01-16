package se.frost.contactsgenerator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import se.frost.contactsgenerator.extensions.FragmentTransition
import se.frost.contactsgenerator.extensions.initFragment

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initFragments()
	}

	private fun initFragments() {
		initFragment(ContactsFragment.newInstance(), ContactsFragment.TAG, FragmentTransition.FRAGMENT_REPLACE, R.id.content_frame)
	}
}
