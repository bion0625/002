package com.intranet.kch.service;

import com.intranet.kch.model.vo.BCExcelVo;
import com.intranet.kch.repository.BCExcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class BCExcelService {
    private final BCExcelRepository bcExcelRepository;

    public BCExcelService(BCExcelRepository companyRepository) {
        this.bcExcelRepository = companyRepository;
    }

    @Transactional
    public void saveOrUpdate(BCExcelVo vo) {
        bcExcelRepository.findByTitleAndDeletedAtIsNull(vo.getTitle())
                .ifPresentOrElse(
                        entity -> {
                            entity.setPropertyName(vo.getPropertyName());
                            entity.setGuestName(vo.getGuestName());
                            entity.setCheckIn(vo.getCheckIn());
                            entity.setCheckOut(vo.getCheckOut());
                            entity.setApartmentType(vo.getApartmentType());
                            entity.setApartmentAddress(vo.getApartmentAddress());
                            entity.setKoreanAddress(vo.getKoreanAddress());
                            entity.setTotalRent(vo.getTotalRent());
                            entity.setPrice(vo.getPrice());
                            entity.setTotalNights(vo.getTotalNights());
                            entity.setOfGuests(vo.getOfGuests());
                            entity.setBookedBy(vo.getBookedBy());
                            entity.setBookingRequestCompany(vo.getBookingRequestCompany());
                            entity.setExtensionOfLease(vo.getExtensionOfLease());
                            entity.setNotice(vo.getNotice());
                            entity.setSignedDate(vo.getSignedDate());
                            entity.setService(vo.getService());
                            entity.setRemarks01(vo.getRemarks01());
                            entity.setRemarks02(vo.getRemarks02());
                            entity.setUpdatedAt(LocalDateTime.now());
                        },
                        () -> bcExcelRepository.save(vo.toEntity()));
    }

    public List<BCExcelVo> getAll() {
        return bcExcelRepository.findAllByDeletedAtIsNull().stream()
                .map(BCExcelVo::fromEntity)
                .toList();
    }

    public BCExcelVo getById(Long id) {
        return bcExcelRepository.findById(id)
                .filter(entity -> entity.getDeletedAt() == null)
                .map(BCExcelVo::fromEntity)
                .orElseThrow(() -> new RuntimeException(id + " is not found"));
    }

    @Transactional
    public void deleteById(Long id) {
        bcExcelRepository.findById(id).map(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            return entity;
        });
    }
}
