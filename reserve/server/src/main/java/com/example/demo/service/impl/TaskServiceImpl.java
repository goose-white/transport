package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.Task;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.service.ITaskService;
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
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {
    // 实现多条件模糊查询
    @Override
    public List<Task> listByQueryKey(String queryKey) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        // 如果查询关键词不为空，就模糊匹配“编号/姓名/年龄”
        if (queryKey != null && !queryKey.trim().isEmpty()) {
            wrapper.like(Task::getId, queryKey)  // 匹配编号
                    .or()
                    .like(Task::getName, queryKey) // 匹配姓名
                    .or()
                    .like(Task::getType, queryKey)
                    .or()
                    .like(Task::getDeviceId, queryKey)
                    .or()
                    .like(Task::getStartTime, queryKey)
                    .or()
                    .like(Task::getEndTime, queryKey)
                    .or()
                    .like(Task::getStatus, queryKey); // 匹配年龄
        }
        // 关键词为空时，查询全部数据
        return this.list(wrapper);
    }
}
