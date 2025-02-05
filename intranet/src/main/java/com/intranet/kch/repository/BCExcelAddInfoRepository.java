package com.intranet.kch.repository;

import com.intranet.kch.model.entity.BCExcelAddInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BCExcelAddInfoRepository extends JpaRepository<BCExcelAddInfoEntity, Long> {
    Optional<BCExcelAddInfoEntity> findByBcExcelIdAndDeletedAtIsNull(Long bcExcelId);
}
