package com.dongky.runner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION_ACCESS_FINE_LOCATION: Int = 5001
    }
    val TAG: String = MainActivity.javaClass.simpleName


    private val locationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private var isRunning: Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fabPlay.setOnClickListener { view ->
            isRunning = !isRunning

            updateFabPlayColor(isRunning)
            updateFabPlayImage(isRunning)

            Snackbar.make(view, if (isRunning) "Start running" else "Stop running", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        setupLocationService()
    }

    private fun setupLocationService() {
        if (!checkPermission()) return

        Log.d(TAG, "LocationService granted!")
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        Log.d(TAG, "isGpsEnable : $isGpsEnable")
        Log.d(TAG, "isNetworkEnable : $isNetworkEnable")

        if (isGpsEnable) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100f, locationListener)
        if (isNetworkEnable) locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 100f, locationListener)
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            tvGpsInfo.text = "LATITUDE : ${location.latitude}\nLONGITUDE : ${location.longitude}\nSPEED : ${location.speed}"
        }

        override fun onStatusChanged(provider: String, status: Int, extra: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun checkPermission(): Boolean {
        // Permission
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder(this)
                    .setTitle("권한 요청")
                    .setMessage("Runner의 기능을 사용하기 위해서는 기본적으로 위치 권한이 필요합니다.")
                    .setPositiveButton("확인", null)
                    .show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_ACCESS_FINE_LOCATION)
            }
            return false
        }
        return true
    }


    private fun updateFabPlayColor(isRunning: Boolean) {
        fabPlay.backgroundTintList = ColorStateList.valueOf(
            resources.getColor(
                if (isRunning) android.R.color.darker_gray else android.R.color.holo_blue_dark,
                null)
        )
    }

    private fun updateFabPlayImage(isRunning: Boolean) {
        fabPlay.setImageDrawable(
            resources.getDrawable(
                if (isRunning) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                null
            ))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION_ACCESS_FINE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupLocationService()
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("권한을 거부하면 기능을 사용할 수 없습니다.\n설정에서 승인을 해주세요.")
                        .setPositiveButton("확인", null)
                        .show()
                }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
