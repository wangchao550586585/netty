package io.netty.example.chapter2.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

//服务器端业务处理器
public class JsonMsgHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String json = (String) msg;
        JsonMsg jsonMsg = JsonMsg.parseFromJson(json);
        System.out.println("收到一个 Json 数据包 =》" + jsonMsg);

    }
}