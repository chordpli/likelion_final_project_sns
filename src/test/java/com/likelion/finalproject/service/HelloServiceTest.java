package com.likelion.finalproject.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Annotation 없이 제작.
class HelloServiceTest {

    // Spring을 안쓰고 테스트 진행, new을 이용해 초기화
    // Pojo방식 활용
    HelloService helloService = new HelloService();

    @Test
    @DisplayName("자릿수 합")
    void sumofDigit(){
        assertEquals(21, helloService.sumOfDigit(687));
        assertEquals(22, helloService.sumOfDigit(787));
        assertEquals(0, helloService.sumOfDigit(0));
        assertEquals(5, helloService.sumOfDigit(11111));
    }

}