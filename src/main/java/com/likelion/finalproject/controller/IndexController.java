package com.likelion.finalproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/index")
@RestController
public class IndexController {

    @GetMapping
    public ResponseEntity<String> index(){
        return ResponseEntity.ok().body("Index");
    }
}
