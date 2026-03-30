package com.example.demo.service.impl;

import com.example.demo.entity.Car;
import com.example.demo.mapper.CarMapper;
import com.example.demo.service.ICarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hello
 * @since 2024-12-11
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {

}
