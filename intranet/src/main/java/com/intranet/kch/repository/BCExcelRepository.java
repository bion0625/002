package com.intranet.kch.repository;

import com.intranet.kch.model.entity.BCExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BCExcelRepository extends JpaRepository<BCExcelEntity, Long> {
    List<BCExcelEntity> findAllByDeletedAtIsNullOrderByCreatedAtDesc();
    Optional<BCExcelEntity> findByTitleAndDeletedAtIsNull(String title);
}
