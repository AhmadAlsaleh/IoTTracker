package com.crazy_iter.iottracker.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val REP = 1000L
    private var isFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadPins()

    }

    private fun loadPins() {
        val getSharedPreferences = getSharedPreferences("tracker", Context.MODE_PRIVATE)
        val getAPI = URLs.realTime + getSharedPreferences.getString("id", "")
        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(getAPI, {

            mapLoadingRL.visibility = View.GONE
            if (it.length() == 0) {
                return@JsonArrayRequest
            }

            mMap.clear()
            val cols = ArrayList<Float>()
            cols.add(BitmapDescriptorFactory.HUE_CYAN)
            cols.add(BitmapDescriptorFactory.HUE_ORANGE)
            cols.add(BitmapDescriptorFactory.HUE_GREEN)
            cols.add(BitmapDescriptorFactory.HUE_MAGENTA)
            cols.add(BitmapDescriptorFactory.HUE_ROSE)
            cols.add(BitmapDescriptorFactory.HUE_YELLOW)

            var latLng: LatLng? = LatLng(0.0, 0.0)
            for (i in 0 until it.length()) {
                val jsonObject = it.getJSONObject(i)
                latLng = LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("long"))
                val col = if (i >= 6) {
                    cols[i%6]
                } else {
                    cols[i]
                }

                val marker = mMap.addMarker(MarkerOptions()
                        .position(latLng)
                        .title(jsonObject.getString("name"))
                        .snippet(jsonObject.getString("date") + " - " + jsonObject.getString("time"))
                        .icon(BitmapDescriptorFactory.defaultMarker(col)))
                if (i == 0) {
                    marker.showInfoWindow()
                }
            }
            if (isFirst) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F))
                isFirst = false
            }

            Handler().postDelayed({
                loadPins()
            }, REP)

        }, {
            Log.e("map error", "error")

            Handler().postDelayed({
                loadPins()
            }, REP)

        })
        queue.add(jsonArrayRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.mapLayers -> {
                if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                } else {
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            }
            R.id.mapExit -> {
                AlertDialog.Builder(this)
                        .setMessage("Sure to logout and exit?")
                        .setNegativeButton("No",null)
                        .setPositiveButton("Yes") { _,_ ->
                            val getSharedPreferences = getSharedPreferences("tracker", Context.MODE_PRIVATE).edit()
                            getSharedPreferences.putString("id", "")
                            getSharedPreferences.apply()

                            finish()
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        .create().show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
