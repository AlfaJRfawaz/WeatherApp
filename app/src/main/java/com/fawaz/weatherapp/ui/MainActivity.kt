package com.fawaz.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.fawaz.weatherapp.BuildConfig
import com.fawaz.weatherapp.databinding.ActivityMainBinding
import com.fawaz.weatherapp.utils.HelperFunction.formattedDegree
import com.fawaz.weatherapp.utils.LOCATION_PERMISSION_REQ_CODE
import com.fawaz.weatherapp.utils.iconSizeWeather4x
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModel? = null
    private val viewModel get() = _viewModel as MainViewModel

    private var _weatherAdapter: WeatherAdapter? = null
    private val weatherAdapter get() = _weatherAdapter as WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        searchByCity()

        viewModel.getWeatherByCity().observe(this) {
            binding.tvCity.text = it.name
            binding.tvDegree.text = formattedDegree(it.main?.temp)

            val icon = it.weather?.get(0)?.icon
            val iconUrl = BuildConfig.IMAGE_URL + icon + iconSizeWeather4x
            Glide.with(this).load(iconUrl)
                .into(binding.imgIcWeather)
        }

        _weatherAdapter = WeatherAdapter()
        viewModel.getForecastByCity().observe(this) {
            weatherAdapter.setData(it.list)
            binding.rvForecastWeather.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = weatherAdapter
            }
        }

        getWeatherCurrentLocation()
    }

    private fun getWeatherCurrentLocation() {
        val fusedLocationClient : FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQ_CODE
                )
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            val lat = it.altitude
            val lon = it.altitude

            viewModel.weatherByCurrentLocation(lat, lon)
        }
            .addOnFailureListener{
                Log.e("MainActivity", "FusedLocationError: Failed getting current location.")
            }

        viewModel.getWeatherByCurrentLocation().observe(this) {
            binding.tvDegree.text = formattedDegree(it.main?.temp)
            binding.tvCity.text = it.name

            val icon = it.weather?.get(0)?.icon
            val iconUrl = BuildConfig.IMAGE_URL + icon + iconSizeWeather4x
            Glide.with(this).load(iconUrl)
                .into(binding.imgIcWeather)
        }
    }

    private fun searchByCity(){
        binding.edtSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.weatherByCity(it)
                        viewModel.forecastByCity(it)
                    }
                    try {
                        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "hideSoftWindow: $e", )
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            }
        )
    }
}