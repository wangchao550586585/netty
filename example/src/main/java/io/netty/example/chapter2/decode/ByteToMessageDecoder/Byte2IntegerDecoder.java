package io.netty.example.chapter2.decode.ByteToMessageDecoder;

/**
 *
 **/

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 存在问题:需要对ByteBuf长度检查.如果有足够字节才进行整数的读取.
 * 使用ReplayingDecoder,作用:在读取ByteBuf缓冲区之前,检查缓冲区是否足够字节
 */
public class Byte2IntegerDecoder extends ByteToMessageDecoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in,
                       List<Object> out) {
        while (in.readableBytes() >= 4) {
            int i = in.readInt();
            System.out.println("解码出一个整数: " + i);
            out.add(i);
        }
    }
}