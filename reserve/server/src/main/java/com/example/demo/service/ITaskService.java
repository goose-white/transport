package com.example.demo.service;

import com.example.demo.entity.Task;
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
public interface ITaskService extends IService<Task> {
    // 新增：多条件查询方法
    List<Task> listByQueryKey(String queryKey);
}
