package com.fawaz.weatherapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fawaz.weatherapp.BuildConfig
import com.fawaz.weatherapp.data.response.ForecastResponse
import com.fawaz.weatherapp.data.response.ListItem
import com.fawaz.weatherapp.databinding.RowItemWeatherBinding
import com.fawaz.weatherapp.utils.HelperFunction.formattedDegree
import com.fawaz.weatherapp.utils.iconSizeWeather2x
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>() {

    private var listWeather = ArrayList<ListItem>()

    class MyViewHolder(val binding: RowItemWeatherBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder =  MyViewHolder(
        RowItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = listWeather[position]
        holder.binding.apply {
            val maxDegree = "Max: " + formattedDegree(data.main?.tempMax)
            tvMaxDegree.text = maxDegree

            val minDegree = "Min: " + formattedDegree(data.main?.tempMin)

            val date = data.dtTxt?.take(10)
            val time  = data.dtTxt?.takeLast(8)
            val dateArray = date?.split("-")?.toTypedArray()
            val timeArray = time?.split(":")?.toTypedArray()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray?.get(0) as String))
            calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) -1)
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]))

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray?.get(0) as String))
            calendar.set(Calendar.MINUTE, 0)

            val dateFormat = SimpleDateFormat("EEE, MMM,d", Locale.getDefault())
                .format(calendar.time).toString()
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                .format(calendar.time).toString()

            tvItemDate.text = dateFormat
            tvItemTime.text = timeFormat

            val icon = data.weather?.get(0)?.icon
            val iconUrl = BuildConfig.IMAGE_URL + icon + iconSizeWeather2x
            Glide.with(imgItemWeather.context).load(iconUrl)
                .into(imgItemWeather)
        }
    }

    override fun getItemCount() = listWeather.size

    fun setData(data: List<ListItem>?) {
        if (data != null) {
            listWeather.clear()
            listWeather.addAll(data)
        }
    }
}