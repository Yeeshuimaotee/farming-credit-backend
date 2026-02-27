package com.example.farmingcreditbackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyController {
    @RequestMapping("/hello")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/user/login")
    public String testlogin () throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "登录成功");

        Map<String, String> data = new HashMap<>();
        data.put("token", "token-admin");
        response.put("data", data);

        return mapper.writeValueAsString(response);
    }
}
