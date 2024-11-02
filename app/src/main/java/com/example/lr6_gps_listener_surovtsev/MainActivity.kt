package com.example.lr6_gps_listener_surovtsev

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var gpsSwitch: SwitchCompat
    private var isGpsListening = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsSwitch = findViewById(R.id.gpsSwitch)

        // Check and request permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        gpsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startGpsListening()
            } else {
                stopGpsListening()
            }
        }
    }

    private fun startGpsListening() {
        // Check permissions again before starting GPS listening
        if (isGpsListening) return
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
                isGpsListening = true
                Toast.makeText(this, "GPS Listening Started", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopGpsListening() {
        if (!isGpsListening) return
        locationManager.removeUpdates(locationListener)
        isGpsListening = false
        Toast.makeText(this, "GPS Listening Stopped", Toast.LENGTH_SHORT).show()
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Handle location updates here, e.g., updating UI
            Toast.makeText(this@MainActivity, "Location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Log the status for debugging purposes
            Toast.makeText(this@MainActivity, "Status changed: $status", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            Toast.makeText(this@MainActivity, "$provider Enabled", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(this@MainActivity, "$provider Disabled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopGpsListening()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
