package com.hyh.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class R {

    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<>();

    private R(){}

    /**
     * 响应成功结果
     * @return
     */
    public static R ok(){
        R r = setResult(ResponseEnum.SUCCESS);
        return r;

    }

    public static R error(){
        R r = setResult(ResponseEnum.REEOR);
        return r;
    }

    /**
     * 自定义响应结果
     * @param responseEnum
     * @return
     */
    public static R setResult(ResponseEnum responseEnum){
        R r = new R();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;
    }

    /**
     * 自定义响应数据
     * @param key
     * @param value
     * @return
     */
    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public R data(Map<String,Object> map){
        this.setData(map);
        return this;
    }

    /**
     * 自定义响应信息
     * @param message
     * @return
     */
    public R message(String message){
        this.setMessage(message);
        return this;
    }

    /**
     * 自定义响应状态码
     * @param code
     * @return
     */
    public R code(Integer code){
        this.setCode(code);
        return this;
    }
}
