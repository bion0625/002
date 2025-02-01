package com.intranet.kch.repository;

import com.intranet.kch.model.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    List<CompanyEntity> findAllByDeletedAtIsNullOrderByCompanyTitle();
    Page<CompanyEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    Optional<CompanyEntity> findByCompanyTitleAndDeletedAtIsNull(String companyTitle);
    Optional<CompanyEntity> findByCompanyInvoiceAcronymAndDeletedAtIsNull(String invoiceAcronym);
    @Query("SELECT c FROM CompanyEntity c WHERE (LOWER(c.companyTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND c.deletedAt IS NULL")
    Page<CompanyEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
