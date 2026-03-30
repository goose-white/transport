package com.example.demo.service;

import com.example.demo.entity.Drone;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hello
 * @since 2025-12-21
 */
public interface IDroneService extends IService<Drone> {
    // 新增：多条件查询方法
    List<Drone> listByQueryKey(String queryKey);
}
