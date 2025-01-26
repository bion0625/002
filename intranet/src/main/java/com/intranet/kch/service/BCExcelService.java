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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        List<InVoiceDto> inVoiceDtos = saveInVoiceList(vo.getIvExcelVos(), vo.getTitle(), vo.getPrice(), savedEntity.getId(), vo.getTotalNights(), vo.getCheckIn(), vo.getSignedDate()).stream()
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

    private List<InVoiceDto> saveInVoiceList(List<IVExcelVo> ivExcelVos, String title, Long price, Long bcId, Long totalNights, String start, String signedDate) {
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
        return ivExcelRepository.saveAll(list).stream().map(InVoiceDto::fromEntity).toList();
    }

    // TODO
    public byte[] generateBCExcel(BookingConfirmationDto dto) {
        try (InputStream templateStream = new ClassPathResource("templates/excelFile/BookingConfirmation.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 템플릿의 첫 번째 시트를 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 특정 셀에 값 설정
            Row row1 = sheet.getRow(1); // 두 번째 행
            if (row1 == null) row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue(dto.getPropertyName());

            Row row2 = sheet.getRow(2); // 세 번째 행
            if (row2 == null) row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue(dto.getGuestName());

            Row row3 = sheet.getRow(3);
            if (row3 == null) row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue(dto.getCheckIn());

            Row row4 = sheet.getRow(4);
            if (row4 == null) row4 = sheet.createRow(4);
            row4.createCell(0).setCellValue(dto.getCheckOut());

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file from template", e);
        }
    }

    public byte[] generateIVExcel(InVoiceDto dto) {
        try (InputStream templateStream = new ClassPathResource("templates/excelFile/InVoice.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 템플릿의 첫 번째 시트를 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 특정 셀에 값 설정
            Row row1 = sheet.getRow(2); // 세 번째 행
            if (row1 == null) row1 = sheet.createRow(3);

            row1.createCell(0).setCellValue(dto.getName());
            row1.createCell(1).setCellValue(dto.getInvoiceDate());
            row1.createCell(2).setCellValue(dto.getStartDate());
            row1.createCell(3).setCellValue(dto.getEndDate());

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file from template", e);
        }
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
