package com.intranet.kch.repository;

import com.intranet.kch.model.entity.BCExcelEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BCExcelRepository extends JpaRepository<BCExcelEntity, Long> {
    Page<BCExcelEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);
    Optional<BCExcelEntity> findByTitleAndDeletedAtIsNull(String title);
}
