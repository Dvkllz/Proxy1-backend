package com.trabajo.monitoring_proxy.repository;

import com.trabajo.monitoring_proxy.model.LogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, String> {

    @Query("SELECT l FROM LogEntity l WHERE " +
            "(:service IS NULL OR :service = '' OR l.serviceId = :service) AND " +
            "(:status IS NULL OR :status = '' OR l.status = :status) AND " +
            "(:fromDate IS NULL OR l.timestamp >= :fromDate) AND " +
            "(:toDate IS NULL OR l.timestamp <= :toDate)")
    Page<LogEntity> findFilteredLogs(
            @Param("service") String service,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}