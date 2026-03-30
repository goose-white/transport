package com.example.demo.controller;

import com.example.demo.common.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @GetMapping("/statistics")
    public Result getStatistics() {
        Map<String, Integer> data = new HashMap<>();
        data.put("droneCount", 5);
        data.put("todoTaskCount", 57);
        data.put("logCount", 1000);
        data.put("errorDroneCount", 2);
        return Result.success(data);
    }
}