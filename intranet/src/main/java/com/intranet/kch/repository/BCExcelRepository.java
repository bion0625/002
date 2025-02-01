package com.intranet.kch.repository;

import com.intranet.kch.model.entity.BCExcelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BCExcelRepository extends JpaRepository<BCExcelEntity, Long> {
    Page<BCExcelEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    Optional<BCExcelEntity> findByTitleAndDeletedAtIsNull(String title);
    @Query("SELECT b FROM BCExcelEntity b WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.propertyName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.guestName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.apartmentType) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.apartmentAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.koreanAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.ofGuests) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.bookedBy) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.bookingRequestCompany) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.extensionOfLease) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.notice) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.remarks01) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.remarks02) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND b.deletedAt IS NULL")
    Page<BCExcelEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
