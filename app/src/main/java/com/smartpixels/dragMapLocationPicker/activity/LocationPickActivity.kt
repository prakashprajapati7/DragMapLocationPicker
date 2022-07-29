package com.smartpixels.dragMapLocationPicker.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smartpixels.dragMapLocationPicker.R
import com.smartpixels.dragMapLocationPicker.fragment.MapLocationSelectorFragment
import kotlinx.android.synthetic.main.activity_location_pick.*

class LocationPickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_pick)
        setupMapLocationSelectorFragment()
    }

    private fun setupMapLocationSelectorFragment() {
        replaceFragmentInContainer(MapLocationSelectorFragment(), fragmentContainerView.id, supportFragmentManager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceFragmentInContainer(
        DestinationFragment: Fragment,
        containerResourceID: Int,
        fragmentManager: FragmentManager
    ) {
        // replace your desired fragment directly
        val ft = fragmentManager.beginTransaction()
        ft.replace(containerResourceID, DestinationFragment)
        ft.commit()
    }
}