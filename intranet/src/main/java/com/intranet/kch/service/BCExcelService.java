package com.intranet.kch.service;

import com.intranet.kch.model.entity.BCExcelEntity;
import com.intranet.kch.model.vo.BCExcelVo;
import com.intranet.kch.model.vo.IVExcelVo;
import com.intranet.kch.repository.BCExcelRepository;
import com.intranet.kch.repository.IVExcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class BCExcelService {
    private final BCExcelRepository bcExcelRepository;
    private final IVExcelRepository ivExcelRepository;

    public BCExcelService(BCExcelRepository companyRepository, IVExcelRepository ivExcelRepository) {
        this.bcExcelRepository = companyRepository;
        this.ivExcelRepository = ivExcelRepository;
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

                            deleteInVoiceListByBCId(entity.getId());

                            saveInVoiceList(vo.getIvExcelVos(), vo.getPrice(), entity.getId());
                        },
                        () -> {
                            BCExcelEntity bcEntity = bcExcelRepository.save(vo.toEntity());
                            saveInVoiceList(vo.getIvExcelVos(), vo.getPrice(), bcEntity.getId());
                        }
                );
    }

    private void saveInVoiceList(List<IVExcelVo> ivExcelVos, Long price, Long bcId) {
        ivExcelRepository.saveAll(ivExcelVos.stream().map(iv -> iv.toEntity(price, bcId)).toList());
    }

    public List<BCExcelVo> getAll() {
        return bcExcelRepository.findAllByDeletedAtIsNull().stream()
                .map(BCExcelVo::fromEntity)
                .toList();
    }

    public BCExcelVo getById(Long id) {
        return bcExcelRepository.findById(id)
                .filter(entity -> entity.getDeletedAt() == null)
                .map(bc -> {
                    BCExcelVo vo = BCExcelVo.fromEntity(bc);
                    vo.setIvExcelVos(
                            ivExcelRepository.findByBcIdAndDeletedAtIsNull(bc.getId()).stream()
                                    .map(IVExcelVo::fromEntity)
                                    .toList()
                    );
                    return vo;
                })
                .orElseThrow(() -> new RuntimeException(id + " is not found"));
    }

    @Transactional
    public void deleteById(Long id) {
        bcExcelRepository.findById(id).map(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            return entity;
        });
        deleteInVoiceListByBCId(id);
    }

    private void deleteInVoiceListByBCId(Long bcId) {
        ivExcelRepository.findByBcIdAndDeletedAtIsNull(bcId)
                .forEach(iv -> iv.setDeletedAt(LocalDateTime.now()));
    }
}
