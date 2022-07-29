package com.smartpixels.dragMapLocationPicker.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.smartpixels.dragMapLocationPicker.R
import com.smartpixels.dragMapLocationPicker.utility.MapUtility
import com.smartpixels.dragMapLocationPicker.utility.SmoothProgressDrawable
import java.util.*
import kotlinx.android.synthetic.main.fragment_map_location_selector.*

class MapLocationSelectorFragment : Fragment(), View.OnClickListener,
    OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveCanceledListener,
    GoogleMap.OnCameraIdleListener {

    private val REQUEST_CHECK_SETTINGS: Int = 101
    private lateinit var mMap: GoogleMap
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var locationRequest: LocationRequest
    val mainThread = Handler(Looper.getMainLooper())

    private var primaryAddress = ""
    private var secondaryAddress = ""

    companion object {
        const val KEY_LATITUDE = "Latitude"
        const val KEY_LONGITUDE = "Longitude"
        var mLatitude = 0.0
        var mLongitude = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_location_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLottie()
        setupMap(view)
    }

    fun startLottie() {
        (lottieLoading as LottieAnimationView).playAnimation()
    }

    fun setupMap(view: View) {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        findViews()
        startProgress()
    }

    private fun startProgress() {
        findAddressProgress!!.setIndeterminateDrawable(
            SmoothProgressDrawable.Builder(requireActivity()).interpolator(AccelerateInterpolator())
                .build()
        )
    }

    private fun findViews() {
        startProgressFindAddress()
        stopRippleProgress()
        btnConfirmLocation!!.setOnClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMapToolbarEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
                checkLocationService()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                )
            }
        } else {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMapToolbarEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
            checkLocationService()
        }

        mMap.setOnCameraMoveStartedListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveListener(this)
    }

    fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location

                // for set current location
                val currentLatLng = LatLng(location.latitude, location.longitude)

                // for set user location
                // val currentLatLng = LatLng(mLatitude, mLongitude)

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f)) // Default
                stopProgressFindAddress()
            }
        }
    }

    fun checkLocationService() {

        locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10 * 1000)
        locationRequest.setFastestInterval(2 * 1000)

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        // builder.setAlwaysShow(true)
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(requireActivity()) { it ->
            it.locationSettingsStates
            fetchCurrentLocation()
        }

        task.addOnFailureListener(requireActivity()) { e ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data!!.getStringExtra("result")
                fetchCurrentLocation()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } /*else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                startProgressFindAddress()
                val place = Autocomplete.getPlaceFromIntent(data!!)
                mLatitude = place.latLng!!.latitude
                mLongitude = place.latLng!!.longitude
                val currentLatLng = LatLng(mLatitude, mLongitude)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        14f
                    )
                ) // 12f Default
                stopProgressFindAddress()
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                MapUtility.showToast(requireActivity(), "The user canceled the operation")
            }
        }*/
    }

    override fun onCameraMoveStarted(p0: Int) {
        mMap.clear()
        startProgressFindAddress()
    }

    override fun onCameraMove() {
    }

    override fun onCameraMoveCanceled() {
    }

    override fun onCameraIdle() {
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(requireActivity(), Locale.getDefault())

            addresses = geocoder.getFromLocation(
                mMap.cameraPosition.target.latitude,
                mMap.cameraPosition.target.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && addresses.size > 0) {
                val address =
                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                val address_: Address = addresses.get(0)
                var societyName = ""
                val parts: List<String> = address_.getAddressLine(0).split(",")
                if (parts.size > 0) {
                    if (parts.size > 1) {
                        val first = parts[0]
                    }
                    if (parts.size > 2) {
                        val first = parts[0]
                        val second = parts[1]
                        societyName = second
                    }
                    if (parts.size > 3) {
                        val first = parts[0]
                        val second = parts[1]
                        val third = parts[2]
                        //societyName = third
                    }
                    if (parts.size > 4) {
                        val first = parts[0]
                        val second = parts[1]
                        val third = parts[2]
                        val fourth = parts[3]
                        //societyName = third
                    }
                }
                mainThread.post {

                    mLatitude = mMap.cameraPosition.target.latitude
                    mLongitude = mMap.cameraPosition.target.longitude

                    primaryAddress = societyName
                    secondaryAddress = address_.getAddressLine(0)

                    tvPrimaryAddress!!.setText("" + societyName)
                    tvPrimaryAddress!!.isSelected = true
                    tvSecondaryAddress!!.setText("" + address_.getAddressLine(0))
                }
            } else {

            }
        } catch (e: Exception) {
            MapUtility.showToast(requireActivity(), "Failed to get location, Try again")
        }

        stopProgressFindAddress()
    }

    private fun startRippleProgress() {
        mainThread.post {
            if (ripplePulseLayout != null) {
                ripplePulseLayout!!.startRippleAnimation()
            }
        }
    }

    private fun stopRippleProgress() {
        mainThread.post {
            if (ripplePulseLayout != null) {
                ripplePulseLayout!!.stopRippleAnimation()
            }
        }
    }

    private fun startProgressFindAddress() {
        stopRippleProgress()
        mainThread.post {
            mLatitude = 0.0
            mLongitude = 0.0
            if (findAddressProgress != null) {
                findAddressProgress!!.visibility = View.VISIBLE
                btnConfirmLocation!!.isEnabled = false
                btnConfirmLocation!!.alpha = 0.8f
            }
        }
    }

    private fun stopProgressFindAddress() {
        mainThread.post {
            startRippleProgress()
            if (findAddressProgress != null) {
                findAddressProgress!!.visibility = View.GONE
                btnConfirmLocation!!.isEnabled = true
                btnConfirmLocation!!.alpha = 1.0f
            }
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            when (view.id) {
                R.id.btnConfirmLocation -> {
                    if (mLatitude != 0.0 && mLongitude != 0.0) {

                        val intent = Intent()
                        intent.putExtra(MapUtility.KEY_LATITUDE, mLatitude)
                        intent.putExtra(MapUtility.KEY_LONGITUDE, mLongitude)
                        intent.putExtra(MapUtility.KEY_ADDRESS_LINE_1, primaryAddress)
                        intent.putExtra(MapUtility.KEY_ADDRESS_LINE_2, secondaryAddress)

                        Log.e("LocationDetails", "latitude ____________________ " + mLatitude)
                        Log.e("LocationDetails", "longitude ___________________ " + mLongitude)
                        Log.e("LocationDetails", "primaryAddress ______________ " + primaryAddress)
                        Log.e(
                            "LocationDetails",
                            "secondaryAddress ____________ " + secondaryAddress
                        )
                        requireActivity().setResult(Activity.RESULT_OK, intent)
                        requireActivity().finish()
                    } else {
                        MapUtility.showToast(requireActivity(), "Location not found")
                    }
                }
                else -> {

                }
            }
        }
    }
}