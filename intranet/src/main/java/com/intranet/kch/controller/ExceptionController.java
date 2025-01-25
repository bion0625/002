package com.intranet.kch.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        // 예외 메시지 모델에 추가
        model.addAttribute("errorMessage",
                Optional.ofNullable(ex.getMessage())
                        .orElse("An unknown error has occurred!"));
        return "error"; // 에러 페이지로 이동
    }
}
