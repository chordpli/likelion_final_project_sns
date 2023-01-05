package com.likelion.finalproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/")
@Slf4j
@ApiIgnore
public class IndexController {

    @GetMapping()
    public String index() {
        log.info("main index");
        return "index";
    }
}
