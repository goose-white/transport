package com.example.demo.controller;
// 替换为实际包名

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @GetMapping("/members")
    public List<Integer> getMembers() {
        // 返回前端需要的数组格式数据
        return Arrays.asList(80, 120, 90, 150);
    }
}