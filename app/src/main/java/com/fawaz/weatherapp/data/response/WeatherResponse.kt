package com.fawaz.weatherapp.data.response

import com.google.gson.annotations.SerializedName


data class WeatherResponse(
    //cara memanggil tipe data

    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("id")
    val id: Int? = null,

    //cara memanggil object data
    @field:SerializedName("coord")
    val coordinate: Coord? = null,

    //cara memanggil array data
    @field:SerializedName("weather")
    val weather: List<WeatherItem?>? = null,

    @field:SerializedName("main")
    val main: Main? = null

)

data class Coord(
    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)

data class WeatherItem(
    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("main")
    val main: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("icon")
    val icon: String? = null
)