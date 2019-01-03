package com.github.pksokolowski.smogmid

data class AQResponse(
        val airQualityIndexLevel: Int,
        val pm10IndexLevel: Int,
        val pm25IndexLevel: Int,
        val o3IndexLevel: Int,
        val no2IndexLevel: Int,
        val so2IndexLevel: Int,
        val c6h6IndexLevel: Int,
        val coIndexLevel: Int
)