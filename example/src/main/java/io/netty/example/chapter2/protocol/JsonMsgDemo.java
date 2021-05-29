package io.netty.example.chapter2.protocol;


import java.io.IOException;

public class JsonMsgDemo {
    public static void main(String[] args) throws IOException {
        serAndDesr();
    }

    //构建Json对象
    public static JsonMsg buildMsg() {
        JsonMsg user = new JsonMsg();
        user.setId(1000);
        user.setContent("xxx");
        return user;
    }

    //序列化 serialization & 反序列化 Deserialization
    public static void serAndDesr() throws IOException {
        JsonMsg message = buildMsg();
        //将POJO对象，序列化成字符串
        String json = message.convertToJson();
        //可以用于网络传输,保存到内存或外存
        System.out.println("json:=" + json);

        //JSON 字符串,反序列化成对象POJO
        JsonMsg inMsg = JsonMsg.parseFromJson(json);
        System.out.println("id:=" + inMsg.getId());
        System.out.println("content:=" + inMsg.getContent());
    }
}
