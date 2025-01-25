package com.intranet.kch.service;

import com.intranet.kch.model.entity.BCExcelEntity;
import com.intranet.kch.model.entity.IVExcelEntity;
import com.intranet.kch.model.vo.BCExcelVo;
import com.intranet.kch.model.vo.IVExcelVo;
import com.intranet.kch.repository.BCExcelRepository;
import com.intranet.kch.repository.IVExcelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
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

                            saveInVoiceList(vo.getIvExcelVos(), vo.getTitle(), vo.getPrice(), entity.getId(), vo.getTotalNights(), vo.getCheckIn(), vo.getSignedDate());
                        },
                        () -> {
                            BCExcelEntity bcEntity = bcExcelRepository.save(vo.toEntity());
                            saveInVoiceList(vo.getIvExcelVos(), vo.getTitle(), vo.getPrice(), bcEntity.getId(), vo.getTotalNights(), vo.getCheckIn(), vo.getSignedDate());
                        }
                );
    }

    private void saveInVoiceList(List<IVExcelVo> ivExcelVos, String title, Long price, Long bcId, Long totalNights, String start, String signedDate) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        List<IVExcelEntity> list = ivExcelVos.stream()
                .sorted(Comparator.comparing(IVExcelVo::getName))
                .map(iv -> iv.toEntity(price, bcId, title))
                .toList();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStartDate(startDate);
            LocalDateTime endDate = startDate.plusDays(list.get(i).getNights());
            list.get(i).setEndDate(endDate);

            if (i == 0) {
                LocalDate invoiceDate = LocalDate.parse(signedDate);
                list.get(i).setInvoiceDate(invoiceDate);
            } else list.get(i).setInvoiceDate(startDate.toLocalDate().withDayOfMonth(1));

            totalNights -= list.get(i).getNights();
            startDate = endDate;
        }
        ivExcelRepository.saveAll(list);
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
