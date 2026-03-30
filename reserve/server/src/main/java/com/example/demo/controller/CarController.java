package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.ICarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hello
 * @since 2024-12-11
 */
@RestController
@RequestMapping("/car")
public class CarController {
    @Resource
    private ICarService iCarService;

    @GetMapping("/get")
    private Result getData(){
        return Result.success(iCarService.list());
    }

}
