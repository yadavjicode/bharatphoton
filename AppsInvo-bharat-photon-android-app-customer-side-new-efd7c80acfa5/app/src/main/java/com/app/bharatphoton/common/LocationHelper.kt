@file:Suppress("DEPRECATION")

package com.app.bharatphoton.common

/*
class LocationHelper(private val activity: Activity) {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationManager: LocationManager
    private lateinit var locationRequest: LocationRequest
    private lateinit var settingsRequest: LocationSettingsRequest
    private lateinit var alertDialog: AlertDialog
    private var listener: LocationListener? = null

    init {
        initProperties()
    }

    @SuppressLint("MissingPermission")
    fun checkAndEnableLocation() {
        val settingsClient = LocationServices.getSettingsClient(activity)
        settingsClient.checkLocationSettings(settingsRequest).addOnSuccessListener @androidx.annotation.RequiresPermission(anyOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            getLastKnownLocation()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity.startActivity(intent)
                }
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnCompleteListener(activity) { task ->
            val location: Location? = task.result
            if (location == null) {
                requestLocationUpdates()
            } else {
                listener?.onLocationFound(location)
            }
        }
    }

    fun hasLocationPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    fun locationEnabled(): Boolean {
        return isLocationEnabled(locationManager)
    }

    fun setListener(listener: LocationListener) {
        this.listener = listener
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun initProperties() {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        settingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    listener?.onLocationFound(location)
                    stopLocationUpdates()
                }
            }
        }
        alertDialog = AlertDialog.Builder(activity).apply {
            setTitle("Permission Required")
            setCancelable(false)
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setPositiveButton("Setting") { dialog, _ ->
                redirectToAppSettings(activity)
                dialog.dismiss()
            }
        }.create()
    }

    fun showSettingPopup(message: String) {
        try {
            alertDialog.setMessage(message)
            if (!alertDialog.isShowing) {
                alertDialog.show()
            }
        } catch (e: Exception) {
            //
        }
    }

    fun dismissSettingPopup() {
        try {
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        } catch (e: Exception) {
            //
        }
    }

    private fun redirectToAppSettings(activity: Activity) {
        val uri = Uri.fromParts("package", activity.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = uri }
        activity.startActivity(intent)
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): AddressResult? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                AddressResult(
                    fullAddress = address.getAddressLine(0),
                    city = address.locality ?: "",
                    state = address.adminArea ?: "",
                    country = address.countryName ?: "",
                    postalCode = address.postalCode ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationUtils", "Error getting address", e)
            null
        }
    }

    interface LocationListener {
        fun onLocationFound(location: Location?)
    }

    data class AddressResult(
        val fullAddress: String,
        val city: String,
        val state: String,
        val country: String,
        val postalCode: String
    )

}*/
