package io.netty.example.chapter2.decode.MessageToMessageDecoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Classname Integer2StringDecoder
 * @Description TODO
 * @Date 2021/5/27 22:03
 * @Created by wangchao
 */
public class Integer2StringDecoder extends MessageToMessageDecoder<Integer> {
    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        //子类执行完后,将遍历out所有元素.逐个发送给下站Inbound入站处理器
        out.add(String.valueOf(msg));
    }
}
