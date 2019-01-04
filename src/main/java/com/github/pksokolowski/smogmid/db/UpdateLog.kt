package com.github.pksokolowski.smogmid.db

import javax.persistence.GenerationType.AUTO
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * Contains data about the procedure of smog data update. The data is downloaded from a public API and saved for reuse.
 * timeStamp of the moment of update is kept as well as the duration of the update procedure - how long it took to
 * download all the data.
 * The timeStamp is meant to be used in order to display the dateTime of update to users as well as to decide whether
 * or not to download new data on startup (when starting with old data, new data will be acquired.
 */
@Entity
data class UpdateLog @JvmOverloads constructor(
        val timeStamp: Long, val duration: Long, @Id @GeneratedValue(strategy = AUTO) val id: Long = 0
)