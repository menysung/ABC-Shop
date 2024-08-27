package org.zerock.b02.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    //기본 페이지
    @GetMapping("/")
    public String home() {
        return "home";
    }



}
