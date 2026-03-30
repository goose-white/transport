package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Recognition;

import com.example.demo.service.IRecognitionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hello
 * @since 2025-12-23
 */
@RestController
@RequestMapping("/recognition")
public class RecognitionController {
    @Resource
    private IRecognitionService recognitionService;

    @PostMapping("/save")
    public Result saveData(@RequestBody Recognition recognition){
        return Result.success(recognitionService.saveOrUpdate(recognition));
    }

    @GetMapping("/getAllData")
    public Result getALL(){
        return Result.success(recognitionService.list());
    }


}
