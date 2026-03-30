package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.Drone;
import com.example.demo.service.IDroneService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hello
 * @since 2025-12-21
 */
@RestController
@RequestMapping("/drone")
public class DroneController {

    @PostMapping("/save")
    public Result saveData(@RequestBody Drone drone){
        return Result.success(droneService.saveOrUpdate(drone));
    }

    @Resource
    private IDroneService droneService;

    @GetMapping("/getAllData")
    public Result getALL(@RequestParam(required = false) String queryKey) {
        // 调用新增的查询方法
        return Result.success(droneService.listByQueryKey(queryKey));
    }

}



