package com.intranet.kch.controller.bookingConfirmation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/BookingConfirmation")
public class BookingConfirmationController {
    @GetMapping
    public String bookingConfirmation() {
        return "BookingConfirmation/main";
    }
}
