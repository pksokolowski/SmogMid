package com.github.pksokolowski.smogmid.api.models

data class AQIModel(
        val stIndexLevel: IndexLevel?,
        val so2IndexLevel: IndexLevel?,
        val no2IndexLevel: IndexLevel?,
        val coIndexLevel: IndexLevel?,
        val pm10IndexLevel: IndexLevel?,
        val pm25IndexLevel: IndexLevel?,
        val o3IndexLevel: IndexLevel?,
        val c6h6IndexLevel: IndexLevel?
) {
    data class IndexLevel(val id: Int)
}