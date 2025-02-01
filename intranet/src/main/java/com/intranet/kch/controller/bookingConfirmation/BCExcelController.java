package com.intranet.kch.controller.bookingConfirmation;

import com.intranet.kch.model.dto.excel.BookingConfirmationDto;
import com.intranet.kch.model.dto.excel.InVoiceDto;
import com.intranet.kch.model.vo.BCExcelVo;
import com.intranet.kch.model.vo.CompanyVo;
import com.intranet.kch.service.BCExcelService;
import com.intranet.kch.service.CompanyService;
import com.intranet.kch.util.ExcelPoiUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/BookingConfirmation/excel")
@Slf4j
public class BCExcelController {

    private final CompanyService companyService;
    private final BCExcelService bcExcelService;

    public BCExcelController(CompanyService companyService, BCExcelService bcExcelService) {
        this.companyService = companyService;
        this.bcExcelService = bcExcelService;
    }

    @ModelAttribute
    public void getExcel(Model model) {
        model.addAttribute("excel", new BCExcelVo());
    }
    @GetMapping
    public String excel(Model model) {
        model.addAttribute("items", bcExcelService.getAll());
        return "BookingConfirmation/excel/list";
    }
    @GetMapping("/insert")
    public String detail(Model model) throws Exception {
        List<CompanyVo> companies = companyService.getAll();
        if (companies.isEmpty()) throw new Exception("Company registration is required!");
        model.addAttribute("companies", companies);
        return "BookingConfirmation/excel/detail";
    }
    @PostMapping("/insert")
    public void save(@ModelAttribute("excel") BCExcelVo bcExcelVo, HttpServletResponse response, SessionStatus status) {
        BookingConfirmationDto bookingConfirmationDto = bcExcelService.saveOrUpdate(bcExcelVo);
        try (ServletOutputStream outputStream = response.getOutputStream();
             ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(zipOutputStream)) {

            // 엑셀 파일 생성 및 추가
            byte[] bcExcelData = ExcelPoiUtil.generateExcel(bookingConfirmationDto, "BookingConfirmation", false);
            addFileToZip(bcExcelVo.getTitle() + "_booking_confirmation.xlsx", bcExcelData, zos);

            for (InVoiceDto inVoiceDto : bookingConfirmationDto.getInVoiceDtos()) {
                byte[] ivExcelData = ExcelPoiUtil.generateExcel(inVoiceDto, "InVoice", true);
                addFileToZip(inVoiceDto.getName() + ".xlsx", ivExcelData, zos);
            }

            // ZIP 파일 다운로드 설정
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"excel_files.zip\"");

            zos.finish();
            outputStream.write(zipOutputStream.toByteArray());
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate ZIP file", e);
        } finally {
            status.setComplete();
        }
    }
    private void addFileToZip(String fileName, byte[] fileData, ZipOutputStream zos) throws IOException {
        ZipEntry zipEntry = new ZipEntry(fileName);
        zos.putNextEntry(zipEntry);
        zos.write(fileData);
        zos.closeEntry();
    }
    @GetMapping("/{id}")
    public String excelDetail(@PathVariable Long id, Model model) {
        model.addAttribute("excel", bcExcelService.getById(id));
        return "BookingConfirmation/excel/detail";
    }
    @PostMapping("/{id}")
    public String companyDelete(@PathVariable Long id) {
        bcExcelService.deleteById(id);
        return "redirect:/BookingConfirmation/excel";
    }
}
