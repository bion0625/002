package com.intranet.kch.service;

import com.intranet.kch.model.dto.excel.BookingConfirmationDto;
import com.intranet.kch.model.dto.excel.InVoiceDto;
import com.intranet.kch.model.entity.BCExcelEntity;
import com.intranet.kch.model.entity.IVExcelEntity;
import com.intranet.kch.model.vo.BCExcelVo;
import com.intranet.kch.model.vo.CompanyVo;
import com.intranet.kch.model.vo.IVExcelVo;
import com.intranet.kch.repository.BCExcelRepository;
import com.intranet.kch.repository.IVExcelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final CompanyService companyService;

    public BCExcelService(BCExcelRepository companyRepository, IVExcelRepository ivExcelRepository, CompanyService companyService) {
        this.bcExcelRepository = companyRepository;
        this.ivExcelRepository = ivExcelRepository;
        this.companyService = companyService;
    }

    @Transactional
    public BookingConfirmationDto saveOrUpdate(BCExcelVo vo) {
        BCExcelEntity savedEntity = bcExcelRepository.findByTitleAndDeletedAtIsNull(vo.getTitle())
                .map(entity -> {
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

                    return entity;
                }
                )
                .orElseGet(() -> bcExcelRepository.save(vo.toEntity()));

        BookingConfirmationDto bookingConfirmationDto = BookingConfirmationDto.fromEntity(savedEntity);

        CompanyVo company = companyService.getById(savedEntity.getCompanyId());
        List<InVoiceDto> inVoiceDtos = saveInVoiceList(vo.getIvExcelVos(), vo.getTitle(), vo.getPrice(), savedEntity.getId(), vo.getTotalNights(), vo.getCheckIn(), vo.getCheckOut(), vo.getSignedDate()).stream()
                .peek(ivDto -> {
                    ivDto.setService(savedEntity.getService());
                    ivDto.setRemarks01(savedEntity.getRemarks01());
                    ivDto.setRemarks02(savedEntity.getRemarks02());
                    ivDto.setCompanyName(company.getCompanyName());
                    ivDto.setCompanyAddr(company.getCompanyAddr());
                }).toList();

        bookingConfirmationDto.setInVoiceDtos(inVoiceDtos);

        return bookingConfirmationDto;
    }

    private List<InVoiceDto> saveInVoiceList(List<IVExcelVo> ivExcelVos, String title, Double price, Long bcId, Long totalNights, String start, String end, String signedDate) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        List<IVExcelEntity> list = ivExcelVos.stream()
                .sorted(Comparator.comparing(IVExcelVo::getName))
                .map(iv -> iv.toEntity(price, bcId, title))
                .toList();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStartDate(startDate);
            LocalDateTime endDate = startDate.plusDays(list.get(i).getNights());

            if (i == list.size()-1) endDate = LocalDateTime.parse(end); // 마지막 날 checkout 일정시간만 계산 외로 적용

            list.get(i).setEndDate(endDate);

            if (i == 0) {
                LocalDate invoiceDate = LocalDate.parse(signedDate);
                list.get(i).setInvoiceDate(invoiceDate);
            } else list.get(i).setInvoiceDate(startDate.toLocalDate().withDayOfMonth(1));

            totalNights -= list.get(i).getNights();
            startDate = endDate;
        }
        return ivExcelRepository.saveAll(list).stream().map(InVoiceDto::fromEntity).toList();
    }

    public Page<BCExcelVo> getAll(Pageable pageable) {
        return bcExcelRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)
                .map(BCExcelVo::fromEntity);
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
