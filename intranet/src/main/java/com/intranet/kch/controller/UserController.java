package com.intranet.kch.controller;

import com.intranet.kch.model.vo.UserVo;
import com.intranet.kch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }
    @ModelAttribute
    public void getUser(Model model) {
        model.addAttribute("user", new UserVo());
    }
    @GetMapping("/join")
    public String joinForm() {
        return "user/join";
    }
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("user") UserVo user, BindingResult result, SessionStatus status) {
        if (userService.getUserByLoginId(user.getUsername()).isPresent()) {
            result.rejectValue(
                    "loginId",
                    "duplicate.userForm.loginId",
                    "이미 사용 중인 아이디입니다.");
            return "user/join";
        }
        if (result.hasErrors()) return "user/join";
        userService.join(user);
        status.setComplete();
        return "user/login";
    }
}
