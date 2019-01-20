package com.github.pksokolowski.smogmid.info

class Info(
        val logsCount: Int,
        val logsWithIndexes: Int,
        val updateTime: String,
        val updateDuration: Int,
        val indexLevelDistribution: List<PercentageInfo>,
        val keyPollutants: List<KeyPollutantsInfo>,
        val details: List<DetailsInfo>
) {
    class PercentageInfo(val index: Int, val percentage: Int)
    class KeyPollutantsInfo(val name: String, val count: Int, val percentage: Int)
    class DetailsInfo(val info: String, val url: String)
}