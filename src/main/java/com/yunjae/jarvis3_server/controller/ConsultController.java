package com.yunjae.jarvis3_server.controller;

import com.yunjae.jarvis3_server.service.ConsultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/consult")
@RequiredArgsConstructor
public class ConsultController {

    private final ConsultService consultService;

    @GetMapping("/ask")
    public String ask(@RequestParam String query) {
        return consultService.askJarvis(query);
    }
}