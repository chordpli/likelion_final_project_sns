package com.likelion.finalproject.controller.restcontroller;

import com.likelion.finalproject.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/")
@RestController
@RequiredArgsConstructor
public class HelloRestController {

    private final HelloService helloService;

    @GetMapping("/hello")
    public ResponseEntity<String> index(){
        return ResponseEntity.ok().body("김준호");
    }

    @GetMapping("/hello/{num}")
    public int index(@PathVariable int num){
        return helloService.sumOfDigit(num);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().body("test2");
    }
}
