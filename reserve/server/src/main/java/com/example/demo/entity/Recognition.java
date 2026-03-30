package com.example.demo.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hello
 * @since 2025-12-23
 */
@Getter
@Setter
  @ApiModel(value = "Recognition对象", description = "")
public class Recognition implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("识别记录标识")
        private Integer id;

      @ApiModelProperty("识别时间")
      private LocalDateTime time;

      @ApiModelProperty("关联 drone_device.id（采集设备）")
      private Integer deviceId;

      @ApiModelProperty("关联 patrol_task.id（所属任务）")
      private Integer taskId;

      @ApiModelProperty("识别结果详情")
      private String content;

      @TableField("url")
      @ApiModelProperty("取证图片 / 视频存储路径")
      private String url;
}
