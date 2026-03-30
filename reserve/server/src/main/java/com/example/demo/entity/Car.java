package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
 * @since 2024-12-11
 */
@Getter
@Setter
  @ApiModel(value = "Car对象", description = "")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

      @ApiModelProperty("识别ID")
        @TableId(value = "id", type = IdType.AUTO)
      private Integer id;

      @ApiModelProperty("车牌")
      private String license;

      @ApiModelProperty("车辆类型")
      private String type;

      @ApiModelProperty("车速")
      private Object speed;

      @ApiModelProperty("时间")
      private Date createtime;
}
