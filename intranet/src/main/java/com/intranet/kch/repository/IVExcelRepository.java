package com.intranet.kch.repository;

import com.intranet.kch.model.entity.IVExcelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVExcelRepository extends JpaRepository<IVExcelEntity, Long> {
    List<IVExcelEntity> findByBcIdAndDeletedAtIsNull(Long bcId);
}
