package com.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DockerComposePageController {
	@GetMapping
	public String restratForm(Model model) {
		model.addAttribute("message", "go try!");
		return "docker-compose/restart";
	}

}
