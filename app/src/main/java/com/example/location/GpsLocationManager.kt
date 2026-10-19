package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.model.UserGpsLocation
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class GpsLocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _currentLocation = MutableStateFlow<UserGpsLocation?>(null)
    val currentLocation: StateFlow<UserGpsLocation?> = _currentLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    private val _hasPermission = MutableStateFlow(hasLocationPermission())
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    fun updatePermissionStatus() {
        _hasPermission.value = hasLocationPermission()
    }

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun requestGpsLocation(onSuccess: ((UserGpsLocation) -> Unit)? = null) {
        updatePermissionStatus()
        if (!hasLocationPermission()) {
            return
        }

        _isLocating.value = true

        try {
            // First check last known location for immediate response
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    val resolved = processLocation(loc)
                    _currentLocation.value = resolved
                    _isLocating.value = false
                    onSuccess?.invoke(resolved)
                } else {
                    // If last location is null, request fresh high accuracy update
                    requestFreshLocation(onSuccess)
                }
            }.addOnFailureListener {
                // Fallback to system LocationManager if fused client fails
                fallbackSystemLocation(onSuccess)
            }
        } catch (e: Exception) {
            fallbackSystemLocation(onSuccess)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(onSuccess: ((UserGpsLocation) -> Unit)? = null) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                val last = result.lastLocation
                if (last != null) {
                    val resolved = processLocation(last)
                    _currentLocation.value = resolved
                    _isLocating.value = false
                    onSuccess?.invoke(resolved)
                } else {
                    fallbackSystemLocation(onSuccess)
                }
            }

            override fun onLocationAvailability(avail: LocationAvailability) {
                if (!avail.isLocationAvailable) {
                    _isLocating.value = false
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            fallbackSystemLocation(onSuccess)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fallbackSystemLocation(onSuccess: ((UserGpsLocation) -> Unit)? = null) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val gpsLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val best = gpsLoc ?: netLoc

            if (best != null) {
                val resolved = processLocation(best)
                _currentLocation.value = resolved
                onSuccess?.invoke(resolved)
            }
        } catch (e: Exception) {
            // Silent error
        } finally {
            _isLocating.value = false
        }
    }

    private fun processLocation(loc: Location): UserGpsLocation {
        val neighborhoodName = reverseGeocode(loc.latitude, loc.longitude)
        return UserGpsLocation(
            latitude = loc.latitude,
            longitude = loc.longitude,
            accuracyMeters = loc.accuracy,
            neighborhoodName = neighborhoodName,
            isRealGpsFix = true,
            timestamp = loc.time
        )
    }

    private fun reverseGeocode(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val subLocality = addr.subLocality
                val locality = addr.locality
                val thoroughfare = addr.thoroughfare
                when {
                    !subLocality.isNullOrBlank() -> subLocality
                    !locality.isNullOrBlank() -> locality
                    !thoroughfare.isNullOrBlank() -> thoroughfare
                    else -> "${String.format(Locale.US, "%.3f", lat)}, ${String.format(Locale.US, "%.3f", lng)}"
                }
            } else {
                "Near ${String.format(Locale.US, "%.3f", lat)}, ${String.format(Locale.US, "%.3f", lng)}"
            }
        } catch (e: Exception) {
            "GPS Neighborhood"
        }
    }

    companion object {
        fun calculateDistanceKm(
            startLat: Double,
            startLng: Double,
            endLat: Double,
            endLng: Double
        ): Double {
            val results = FloatArray(1)
            Location.distanceBetween(startLat, startLng, endLat, endLng, results)
            return (results[0] / 1000.0)
        }
    }
}
