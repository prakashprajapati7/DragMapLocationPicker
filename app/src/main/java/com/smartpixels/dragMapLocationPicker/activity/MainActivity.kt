package com.smartpixels.dragMapLocationPicker.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.smartpixels.dragMapLocationPicker.R
import com.smartpixels.dragMapLocationPicker.fragment.MapLocationSelectorFragment
import com.smartpixels.dragMapLocationPicker.utility.MapUtility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val REQUEST_CODE = 120
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPickLocation.setOnClickListener(this)
    }

    private fun setupMapLocationSelectorFragment() {
        replaceFragmentInContainer(
            MapLocationSelectorFragment(),
            fragmentContainerView.id,
            supportFragmentManager
        )
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

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnPickLocation -> {
                askLocationService()
            }
        }
    }

    fun startLocationPick() {
        val intentstartLocationPick = Intent(this@MainActivity, LocationPickActivity::class.java)
        startActivityForResult(intentstartLocationPick, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(
            "LocationDetails",
            "MainActivity (onActivityResult) data = ____________________ " + data
        )
        Log.e(
            "LocationDetails",
            "MainActivity (onActivityResult) requestCode = _____________ " + requestCode
        )
        if (requestCode == REQUEST_CODE && data != null && resultCode == RESULT_OK) {
            setLocationData(data)
        } else {
            MapUtility.showToast(this@MainActivity, "No valid data founds, Pleasse try again")
        }
    }

    fun setLocationData(data: Intent?) {
        data?.let { it ->
            val latitude = it.getDoubleExtra(MapUtility.KEY_LATITUDE, 0.0)
            val longitude = it.getDoubleExtra(MapUtility.KEY_LONGITUDE, 0.0)
            val addressLine1 = it.getStringExtra(MapUtility.KEY_ADDRESS_LINE_1)
            val addressLine2 = it.getStringExtra(MapUtility.KEY_ADDRESS_LINE_2)

            tvLatitude.text = "Latitude : "+ latitude
            tvLongitude.text = "Longitude : "+ longitude
            tvAddressLine1.text = addressLine1
            tvAddressLine2.text = addressLine2
        }
    }

    fun askLocationService()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                startLocationPick()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            startLocationPick()
        }
    }
}