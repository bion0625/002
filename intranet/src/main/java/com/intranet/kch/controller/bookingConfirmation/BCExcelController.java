package com.intranet.kch.controller.bookingConfirmation;

import com.intranet.kch.model.vo.BCExcelVo;
import com.intranet.kch.service.BCExcelService;
import com.intranet.kch.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

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
    public String detail(Model model) {
        model.addAttribute("companies", companyService.getAll());
        return "BookingConfirmation/excel/detail";
    }
    @PostMapping("/insert")
    public String save(@ModelAttribute("excel") BCExcelVo bcExcelVo, SessionStatus status) {
        bcExcelService.saveOrUpdate(bcExcelVo);
        status.setComplete();
        return "redirect:/BookingConfirmation/excel";
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
