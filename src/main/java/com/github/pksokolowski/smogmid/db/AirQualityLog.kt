package com.github.pksokolowski.smogmid.db

import com.github.pksokolowski.smogmid.utils.LatLng

class AirQualityLog(val stationId: Long, val details: PollutionDetails, val location: LatLng)