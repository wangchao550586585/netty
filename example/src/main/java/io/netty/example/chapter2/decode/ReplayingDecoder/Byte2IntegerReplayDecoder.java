package io.netty.example.chapter2.decode.ReplayingDecoder;

/**
 *
 **/

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 使用ReplayingDecoder,该类装饰了buffer,在缓冲区真的读数据时,会判断长度是否够,不够异常.然后捕获,等下次IO事件到时读取.
 * 作用:在读取ByteBuf缓冲区之前,检查缓冲区是否足够字节
 */
public class Byte2IntegerReplayDecoder extends ReplayingDecoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
                       List<Object> out) {
        int i = in.readInt();
        System.out.println("解码出一个整数: " + i);
        out.add(i);
    }
}