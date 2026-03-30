package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
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
 * @since 2025-12-21
 */
@Getter
@Setter
  @ApiModel(value = "Drone对象", description = "")
public class Drone implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("唯一标识（主键）")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("设备类型")
      private String model;

      @ApiModelProperty("设备状态 (0 - 待命，1 - 飞行，2 - 维修)")
      private Boolean status;

      @ApiModelProperty("电池电量")
      private Byte battery;

      @ApiModelProperty("最快飞行速度")
      private BigDecimal maxSpeed;

      @ApiModelProperty("最大续航时间（分钟）")
      private Integer maxFlightTime;
}
