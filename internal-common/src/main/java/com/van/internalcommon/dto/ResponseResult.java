package com.van.internalcommon.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.van.internalcommon.constant.ResponseStatusEnum;

import java.io.Serializable;

public class ResponseResult<E> implements Serializable {
    private int code;
    private String message;
    private E data;

    /**
     * 成功
     *
     * @param data 返回的参数body
     * @param <E>  泛型对象
     * @return
     */
    public static <E> ResponseResult success(E data) {
        ResponseResult<E> result = new ResponseResult<>();
        result.setCode(ResponseStatusEnum.SUCCESS.getCode());
        result.setMessage(ResponseStatusEnum.SUCCESS.getValue());
        result.setData(data);
        return result;
    }

    public static <E> ResponseResult success() {
        ResponseResult<E> result = new ResponseResult<>();
        result.setCode(ResponseStatusEnum.SUCCESS.getCode());
        result.setMessage(ResponseStatusEnum.SUCCESS.getValue());
        return result;
    }

    /**
     * 500
     *
     * @param data
     * @param <E>
     * @return
     */
    public static <E> ResponseResult internalException(E data) {
        ResponseResult<E> result = new ResponseResult<>();
        result.setCode(ResponseStatusEnum.INTERNAL_SERVER_EXCEPTION.getCode());
        result.setMessage(ResponseStatusEnum.INTERNAL_SERVER_EXCEPTION.getValue());
        result.setData(data);
        return result;
    }

    public static ResponseResult<?> error(String message) {
        ResponseResult<?> result = new ResponseResult<>();
        result.setCode(ResponseStatusEnum.FAIL.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 自定义异常
     *
     * @param code
     * @param message
     * @return
     */
    public static ResponseResult<?> error(int code, String message) {
        ResponseResult<?> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 对象类型反转
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T parseDataToObject(Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(data);
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json转换失败");
        }
    }

    /**
     * 是否返回正确的状态码
     * @return
     */
    public boolean isSuccess() {
        return code == ResponseStatusEnum.SUCCESS.getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
