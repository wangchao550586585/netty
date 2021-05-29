package io.netty.example.chapter2.protocol;

import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 **/
@Data
public class JsonMsg {
    //id Field(域)
    private int id;
    //content Field(域)
    private String content = "哈哈";

    //在通用方法中，使用阿里FastJson转成Java对象
    public static JsonMsg parseFromJson(String json) {
        return JsonUtil.jsonToPojo(json, JsonMsg.class);
    }

    //在通用方法中，使用谷歌Gson转成字符串
    public String convertToJson() {
        return JsonUtil.pojoToJson(this);
    }
    public JsonMsg(int id) {
        this.id = id;
    }

    public JsonMsg() {
        this.id = Math.abs(ThreadLocalRandom.current().nextInt(100)) + 1;
    }

}
