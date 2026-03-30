package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * <p>
 *
 * </p>
 *
 * @author hello
 * @since 2025-12-21
 */
@Getter
@Setter
@ApiModel(value = "Task对象", description = "")
public class Task implements Serializable {

  private static final long serialVersionUID = 1L;

  // 核心新增：添加主键自增注解，指定使用数据库自增规则
  @TableId(type = IdType.AUTO)
  @ApiModelProperty("任务唯一标识")
  private Integer id;

  @ApiModelProperty("任务名称")
  private String name;

  @ApiModelProperty("任务类型（0 - 道路巡航、1 - 水域巡逻、2 - 事故取证、3 - 隐患排查）")
  private Byte type;

  @ApiModelProperty("关联drone_id(执行设备)")
  private Integer deviceId;

  @ApiModelProperty("任务开始时间")
  private LocalDateTime startTime;

  @ApiModelProperty("任务结束时间")
  private LocalDateTime endTime;

  @ApiModelProperty("任务状态（0-未开始，1-执行中，2-已完成,3-已取消）")
  private Byte status;
}