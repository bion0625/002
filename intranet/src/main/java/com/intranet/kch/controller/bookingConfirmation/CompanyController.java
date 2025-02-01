package com.intranet.kch.controller.bookingConfirmation;

import com.intranet.kch.model.vo.CompanyVo;
import com.intranet.kch.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

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
                          Model model) {
        model.addAttribute("items", companyService.getAll(
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending())));
        return "BookingConfirmation/company/list";
    }
    @GetMapping("/insert")
    public String detail() {
        return "BookingConfirmation/company/detail";
    }
    @PostMapping("/insert")
    public String saveOrUpdate(@Valid @ModelAttribute("company") CompanyVo companyVo, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) return "/BookingConfirmation/company/detail";
        companyService.saveOrUpdate(companyVo);
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
