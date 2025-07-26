package com.example.samplemicro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/exp")

public class SampleController {

    @GetMapping("/getmsg")
    public Map<String,String> getMessage(){
        return Map.of("message","Hi This is a sample message from a microservice , U did well");
    }
}
