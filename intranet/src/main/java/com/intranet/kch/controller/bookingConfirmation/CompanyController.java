package com.intranet.kch.controller.bookingConfirmation;

import com.intranet.kch.model.vo.CompanyVo;
import com.intranet.kch.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Optional;

@Controller
@RequestMapping("/BookingConfirmation/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @ModelAttribute
    public void getCompany(Model model) {
        model.addAttribute("company", new CompanyVo());
    }
    @GetMapping
    public String company(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<CompanyVo> items = Optional.ofNullable(keyword)
                .filter(k -> !k.trim().isEmpty())
                .map(k -> {
                    model.addAttribute("keyword", k);
                    return companyService.search(k, pageable);
                })
                .orElseGet(() -> companyService.getAll(pageable));
        model.addAttribute("items", items);
        return "BookingConfirmation/company/list";
    }
    @GetMapping("/insert")
    public String detail() {
        return "BookingConfirmation/company/insert";
    }
    @PostMapping("/insert")
    public String save(@Valid @ModelAttribute("company") CompanyVo companyVo, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) return "/BookingConfirmation/company/detail";
        if (companyService.existByTitle(companyVo.getCompanyTitle())) {
            result.rejectValue(
                    "companyTitle",
                    "duplicate.companyForm.companyTitle",
                    "이미 사용 중인 제목입니다.");
            return "/BookingConfirmation/company/insert";
        }

        if (companyService.existByInvoiceAcronym(companyVo)) {
            result.rejectValue(
                    "companyInvoiceAcronym",
                    "duplicate.companyForm.companyInvoiceAcronym",
                    "이미 사용 중인 약어입니다.");
            return "/BookingConfirmation/company/insert";
        }
        companyService.save(companyVo);
        status.setComplete();
        return "redirect:/BookingConfirmation/company";
    }
    @PostMapping("/update")
    public String Update(@Valid @ModelAttribute("company") CompanyVo companyVo, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) return "/BookingConfirmation/company/detail";
        if (companyService.existByInvoiceAcronym(companyVo)) {
            result.rejectValue(
                    "companyInvoiceAcronym",
                    "duplicate.companyForm.companyInvoiceAcronym",
                    "이미 사용 중인 약어입니다.");
            return "/BookingConfirmation/company/detail";
        }
        companyService.update(companyVo);
        status.setComplete();
        return "redirect:/BookingConfirmation/company";
    }
    @GetMapping("/{id}")
    public String companyDetail(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyService.getById(id));
        return "BookingConfirmation/company/detail";
    }
    @PostMapping("/{id}")
    public String companyDelete(@PathVariable Long id) {
        companyService.deleteById(id);
        return "redirect:/BookingConfirmation/company";
    }
}
