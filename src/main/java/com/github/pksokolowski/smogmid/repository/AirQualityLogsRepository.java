package com.github.pksokolowski.smogmid.repository;

import com.github.pksokolowski.smogmid.db.AirQualityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface AirQualityLogsRepository extends JpaRepository<AirQualityLog, Long> {
}