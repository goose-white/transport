package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Drone;
import com.example.demo.mapper.DroneMapper;
import com.example.demo.service.IDroneService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hello
 * @since 2025-12-21
 */
@Service
public class DroneServiceImpl extends ServiceImpl<DroneMapper, Drone> implements IDroneService {
    // 实现多条件模糊查询
    @Override
    public List<Drone> listByQueryKey(String queryKey) {
        LambdaQueryWrapper<Drone> wrapper = new LambdaQueryWrapper<>();
        // 如果查询关键词不为空，就模糊匹配“编号/姓名/年龄”
        if (queryKey != null && !queryKey.trim().isEmpty()) {
            wrapper.like(Drone::getId, queryKey)  // 匹配编号
                    .or()
                    .like(Drone::getModel, queryKey) // 匹配姓名
                    .or()
                    .like(Drone::getStatus, queryKey)
                    .or()
                    .like(Drone::getBattery, queryKey)
                    .or()
                    .like(Drone::getMaxSpeed, queryKey)
                    .or()
                    .like(Drone::getMaxFlightTime, queryKey); // 匹配年龄
        }
        // 关键词为空时，查询全部数据
        return this.list(wrapper);
    }
}
