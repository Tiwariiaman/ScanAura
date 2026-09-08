package com.scanaura.qr.repository;

import com.scanaura.business.entity.Business;
import com.scanaura.qr.entity.QrScanDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface QrScanDailyRepository
        extends JpaRepository<QrScanDaily, UUID> {

    Optional<QrScanDaily> findByBusinessAndScanDate(
            Business business,
            LocalDate scanDate
    );

    @Modifying
    @Query(value = """
        INSERT INTO qr_scan_daily (
            id,
            business_id,
            scan_date,
            scan_count,
            created_at,
            updated_at
        )
        VALUES (
            :id,
            :businessId,
            :scanDate,
            1,
            CURRENT_TIMESTAMP,
            CURRENT_TIMESTAMP
        )
        ON CONFLICT (business_id, scan_date)
        DO UPDATE SET
            scan_count = qr_scan_daily.scan_count + 1,
            updated_at = CURRENT_TIMESTAMP
        """, nativeQuery = true)
    void incrementScan(
            @Param("id") UUID id,
            @Param("businessId") UUID businessId,
            @Param("scanDate") LocalDate scanDate
    );

    @Query("""
            SELECT COALESCE(SUM(q.scanCount), 0)
            FROM QrScanDaily q
            WHERE q.business = :business
            """)
    Long getTotalScansByBusiness(
            @Param("business") Business business
    );

    @Query("""
            SELECT COALESCE(SUM(q.scanCount), 0)
            FROM QrScanDaily q
            WHERE q.business = :business
            AND q.scanDate BETWEEN :startDate AND :endDate
            """)
    Long getTotalScansByBusinessAndDateRange(
            @Param("business") Business business,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(q.scanCount), 0)
            FROM QrScanDaily q
            WHERE q.business = :business
            AND q.scanDate = :scanDate
            """)
    Long getTotalScansByBusinessAndDate(
            @Param("business") Business business,
            @Param("scanDate") LocalDate scanDate
    );
}