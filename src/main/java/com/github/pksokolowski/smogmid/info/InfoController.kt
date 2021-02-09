package com.github.pksokolowski.smogmid.info

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class InfoController(private val infoService: InfoService) {
    @GetMapping("/info")
    fun getInfoView(model: Model): String {
        val info = infoService.info
        model["logsCount"] = info.logsCount
        model["logsWithIndexes"] = info.logsWithIndexes
        model["updateTime"] = info.updateTime
        model["updateDuration"] = info.updateDuration
        model["indexLevelDistribution"] = info.indexLevelDistribution
        model["keyPollutants"] = info.keyPollutants
        model["details"] = info.details
        return "info"
    }
}