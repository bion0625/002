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
import org.apache.poi.ss.usermodel.Cell;
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

    public byte[] generateBCExcel(BookingConfirmationDto dto) {
        try (InputStream templateStream = new ClassPathResource("templates/excelFile/BookingConfirmation.xlsx").getInputStream();
             Workbook workbook = new XSSFWorkbook(templateStream);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 템플릿의 첫 번째 시트를 가져오기
            Sheet sheet = workbook.getSheetAt(0);

            // 특정 셀에 값 설정
            createSheetForBookingConfirmation(sheet, dto);

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
            createSheetForBookingConfirmation(sheet, dto);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel file from template", e);
        }
    }

    private void createSheetForBookingConfirmation(Sheet sheet, BookingConfirmationDto dto) {
        createCell(sheet, 14, 3, dto.getPropertyName());
        createCell(sheet, 17, 3, dto.getGuestName());
        createCell(sheet, 18, 3, dto.getCheckIn());
        createCell(sheet, 19, 3, dto.getCheckOut());
        createCell(sheet, 20, 3, dto.getApartmentType());
        createCell(sheet, 20, 3, dto.getApartmentType());
        createCell(sheet, 21, 3, dto.getApartmentAddress());
        createCell(sheet, 22, 3, dto.getKoreanAddress());
        createCell(sheet, 23, 3, dto.getTotalRent());
        createCell(sheet, 24, 3,  "(USD" + dto.getPrice() + " X " + dto.getTotalNights() + "night)");
        createCell(sheet, 26, 3,  dto.getOfGuests());
        createCell(sheet, 27, 3,  dto.getBookedBy());
        createCell(sheet, 28, 3,  dto.getBookingRequestCompany());
        createCell(sheet, 32, 3,  dto.getExtensionOfLease());
        createCell(sheet, 33, 3,  dto.getNotice());
        createCell(sheet, 48, 3,  dto.getPropertyName());
        createCell(sheet, 56, 3,  "Signing Date: " + dto.getSignedDate());
    }

    private void createSheetForBookingConfirmation(Sheet sheet, InVoiceDto dto) {
        createCell(sheet, 4, 3, dto.getInvoiceDate());
        createCell(sheet, 6, 3, dto.getName());
        createCell(sheet, 9, 7, dto.getCompanyName());
        createCell(sheet, 10, 7, dto.getCompanyAddr());
        createCell(sheet, 14, 1, dto.getService());
        createCell(sheet, 14, 4, dto.getStartDate());
        createCell(sheet, 14, 6, dto.getEndDate());
        createCell(sheet, 14, 8, dto.getNights());
        createCell(sheet, 14, 9, String.format("%,.2f", Double.parseDouble(dto.getTotalPrice())));
        createCell(sheet, 25, 9, String.format("%,.2f", Double.parseDouble(dto.getTotalPrice())));
        createCell(sheet, 35, 1, dto.getRemarks01());
        createCell(sheet, 36, 1, dto.getRemarks02());
    }

    private void createCell(Sheet sheet, int r, int c, String value) {
        Row row = sheet.getRow(r); // 행
        Cell cell = row.getCell(c);
        cell.setCellValue(value);
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
