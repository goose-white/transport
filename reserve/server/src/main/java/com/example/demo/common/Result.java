package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接口统一返回包装类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String code;
    private String msg;
    private Object data;

    // ========== 成功响应 ==========
    // 无数据、无提示的成功响应
    public static Result success() {
        return new Result(Constants.CODE_200, "", null);
    }

    // 带数据的成功响应
    public static Result success(Object data) {
        return new Result(Constants.CODE_200, "", data);
    }

    // 带数据+自定义提示的成功响应（可选，提升灵活性）
    public static Result success(String msg, Object data) {
        return new Result(Constants.CODE_200, msg, data);
    }

    // ========== 错误响应 ==========
    // 系统默认错误（500）
    public static Result error() {
        return new Result(Constants.CODE_500, "系统错误", null);
    }

    // 自定义错误码+错误信息（原有方法，保留）
    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    // 新增：仅自定义错误信息（使用默认500错误码）
    public static Result error(String msg) {
        return new Result(Constants.CODE_500, msg, null);
    }
}