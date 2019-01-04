package com.github.pksokolowski.smogmid.utils

import com.github.pksokolowski.smogmid.api.models.AQIModel
import com.github.pksokolowski.smogmid.api.models.StationModel
import com.github.pksokolowski.smogmid.db.AirQualityLog
import com.github.pksokolowski.smogmid.db.PollutionDetails

class AQIDataConverter {
    companion object {
        fun toAirQualityLog(model: AQIModel, station: StationModel): AirQualityLog {
            val details = PollutionDetails(
                    model.pm10IndexLevel?.id ?: -1,
                    model.pm25IndexLevel?.id ?: -1,
                    model.o3IndexLevel?.id ?: -1,
                    model.no2IndexLevel?.id ?: -1,
                    model.so2IndexLevel?.id ?: -1,
                    model.c6h6IndexLevel?.id ?: -1,
                    model.coIndexLevel?.id ?: -1
            )

            val id = station.id.toLong()
            val indexLevel = model.stIndexLevel?.id ?: -1
            val location = LatLng(station.gegrLat.toDouble(), station.gegrLon.toDouble())

            return AirQualityLog(id, indexLevel, details, location)
        }
    }
}