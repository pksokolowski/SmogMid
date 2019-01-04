package com.github.pksokolowski.smogmid.db

import com.github.pksokolowski.smogmid.utils.LatLng
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class AirQualityLog(@Id val stationId: Long, val indexLevel: Int, val details: PollutionDetails, val location: LatLng)
