package com.github.pksokolowski.smogmid.api

import com.github.pksokolowski.smogmid.api.models.AQIModel
import com.github.pksokolowski.smogmid.db.AirQualityLog
import com.github.pksokolowski.smogmid.utils.AQIDataConverter.Companion.toAirQualityLog
import com.github.pksokolowski.smogmid.utils.ScopesProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

/**
 * This class is meant to consume the API of the Polish Inspectorate of Environmental Protection.
 * It downloads indexes for all existing stations, the stations are provided by a dependency.
 */
@Component
class AQIDownloader(
    private val restTemplate: RestTemplate,
    private val stationsDownloader: StationsDownloader,
    private val scopesProvider: ScopesProvider
) {
    suspend fun getAirQualityLogs() = withContext(scopesProvider.aqUpdatesDispatcher) {
        val stations = stationsDownloader.allStations

        stations.map { station ->
            async {
                val aqi = restTemplate.getForObject(
                    "https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/{stationId}",
                    AQIModel::class.java,
                    station.id
                )
                toAirQualityLog(aqi, station)
            }
        }.awaitAll()
    }
}