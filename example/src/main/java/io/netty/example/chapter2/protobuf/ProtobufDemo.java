package io.netty.example.chapter2.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @Classname ProtobufDemo
 * @Description TODO
 * @Date 2021/5/29 21:49
 * @Created by wangchao
 */
public class ProtobufDemo {
    public static MsgProtos.Msg buildMsg() {
        MsgProtos.Msg.Builder personBuilder = MsgProtos.Msg.newBuilder();
        personBuilder.setId(1000);
        personBuilder.setContent("疯狂创客圈:高性能学习社群");
        MsgProtos.Msg message = personBuilder.build();
        return message;
    }

    public static MsgProtos.Msg buildMsg(int id, String content) {
        MsgProtos.Msg.Builder personBuilder = MsgProtos.Msg.newBuilder();
        personBuilder.setId(id);
        personBuilder.setContent(content);
        MsgProtos.Msg message = personBuilder.build();
        return message;
    }

    //第1种方式:序列化 serialization & 反序列化 Deserialization
    public void serAndDesr1() throws IOException {
        MsgProtos.Msg message = buildMsg(1, "疯狂创客圈-高并发发烧友圈子");
        //将Protobuf对象，序列化成二进制字节数组
        byte[] data = message.toByteArray();
        //可以用于网络传输,保存到内存或外存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(data);
        data = outputStream.toByteArray();
        //二进制字节数组,反序列化成Protobuf 对象
        MsgProtos.Msg inMsg = MsgProtos.Msg.parseFrom(data);
        System.out.println("id:=" + inMsg.getId());
        System.out.println("content:=" + inMsg.getContent());
    }

    //第2种方式:序列化 serialization & 反序列化 Deserialization
    public void serAndDesr2() throws IOException {
        MsgProtos.Msg message = buildMsg();
        //序列化到二进制流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        //从二进流,反序列化成Protobuf 对象
        //在OIO没问题,在NIO存在粘包/半包问题
        MsgProtos.Msg inMsg = MsgProtos.Msg.parseFrom(inputStream);
        System.out.println("id:=" + inMsg.getId());
        System.out.println("content:=" + inMsg.getContent());
    }

    //第3种方式:序列化 serialization & 反序列化 Deserialization
    //带字节长度：[字节长度][字节数据],解决粘包/半包问题,可适用nio场景
    public void serAndDesr3() throws IOException {
        MsgProtos.Msg message = buildMsg();
        //序列化到二进制流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //序列化字节码之前添加了字节数组长度
        message.writeDelimitedTo(outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        //从二进流,反序列化成Protobuf 对象
        MsgProtos.Msg inMsg = MsgProtos.Msg.parseDelimitedFrom(inputStream);
        System.out.println("id:=" + inMsg.getId());
        System.out.println("content:=" + inMsg.getContent());


    }

}
