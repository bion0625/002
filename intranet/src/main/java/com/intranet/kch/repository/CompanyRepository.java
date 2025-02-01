package com.intranet.kch.repository;

import com.intranet.kch.model.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    List<CompanyEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    Page<CompanyEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    Optional<CompanyEntity> findByCompanyTitleAndDeletedAtIsNull(String companyTitle);
}
