package com.github.pksokolowski.smogmid.repository;

import com.github.pksokolowski.smogmid.db.UpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateLogsRepository extends JpaRepository<UpdateLog, Long> {

    UpdateLog findTopByOrderByTimeStampDesc();
}
