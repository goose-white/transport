package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.entity.Task;
import com.example.demo.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hello
 * @since 2025-12-21
 */
@RestController
@RequestMapping("/task")
public class TaskController {
    // 新增日志，方便定位问题
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Resource
    private ITaskService taskService;

    @PostMapping("/save")
    public Result saveData(@RequestBody Task task) {
        // 1. 补全必要字段（前端未传时赋值）
        if (task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.now());
        }
        if (task.getEndTime() == null) {
            task.setEndTime(null);
        }

        // 2. 打印接收的参数，方便调试
        log.info("接收的任务参数：{}", task);

        // 3. 手动处理新增/更新逻辑（绕过saveOrUpdate的坑）
        boolean operationResult;
        String message;

        if (task.getId() != null) {
            // 编辑逻辑：先检查ID是否存在
            Task existTask = taskService.getById(task.getId());
            if (existTask == null) {
                // ID不存在，直接返回失败提示
                log.error("编辑失败：任务ID={} 不存在", task.getId());
                return Result.error("编辑失败：任务ID=" + task.getId() + "不存在");
            }
            // 强制更新所有字段（避免字段缺失）
            existTask.setName(task.getName());
            existTask.setType(task.getType());
            existTask.setDeviceId(task.getDeviceId());
            existTask.setStatus(task.getStatus());
            existTask.setStartTime(task.getStartTime());
            existTask.setEndTime(task.getEndTime());
            operationResult = taskService.updateById(existTask);
            message = operationResult ? "编辑任务成功" : "编辑任务失败：数据库更新失败";
        } else {
            // 新增逻辑
            operationResult = taskService.save(task);
            message = operationResult ? "新增任务成功" : "新增任务失败：数据库插入失败";
        }

        // 4. 正确返回结果：成功/失败对应不同的Result状态
        log.info("操作结果：{}，消息：{}", operationResult, message);
        if (operationResult) {
            return Result.success(message); // 成功返回success+提示语
        } else {
            return Result.error(message);   // 失败返回error+提示语
        }
    }

    // 核心修改：适配前端四个独立查询条件
    @GetMapping("/getAllData")
    public Result getALL(
            @RequestParam(required = false) Integer id,        // 任务ID（精确匹配）
            @RequestParam(required = false) String name,       // 任务名称（模糊匹配）
            @RequestParam(required = false) Byte type,         // 任务类型（精确匹配）
            @RequestParam(required = false) Integer deviceId   // 执行无人机ID（精确匹配）
    ) {
        // 打印所有查询参数，方便调试
        log.info("查询参数：id={}, name={}, type={}, deviceId={}", id, name, type, deviceId);

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();

        // 任务ID：非空则精确匹配
        if (id != null) {
            wrapper.eq(Task::getId, id);
        }

        // 任务名称：非空则模糊匹配
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Task::getName, name.trim());
        }

        // 任务类型：非空则精确匹配
        if (type != null) {
            wrapper.eq(Task::getType, type);
        }

        // 执行无人机ID：非空则精确匹配
        if (deviceId != null) {
            wrapper.eq(Task::getDeviceId, deviceId);
        }

        // 执行查询
        List<Task> taskList = taskService.list(wrapper);
        log.info("查询到任务数量：{}", taskList.size());
        return Result.success(taskList);
    }

    // 保留原有删除接口（已适配前端参数传递方式）
    @PostMapping("/delete")
    public Result deleteData(@RequestParam Integer id) {
        log.info("删除任务ID：{}", id);
        boolean result = taskService.removeById(id);
        if (result) {
            return Result.success("删除任务成功");
        } else {
            return Result.error("删除失败：任务ID=" + id + "不存在");
        }
    }
}