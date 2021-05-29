package io.netty.example.chapter2.decode.ByteToMessageDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Classname StringHeaderDecoder
 * @Description TODO
 * @Date 2021/5/27 21:17
 * @Created by wangchao
 *
 * 自定义分包解码器。
 *
 * 线程不安全,因为它是有状态的.
 * 在内部有一个cumulation,用来保存没有解析完的二进制内容.
 */
public class StringHeaderDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //4字节长度记录content-length
        if (in.readableBytes() < 4) {
            return;
        }

        //防止读取错误,设置回滚点
        in.markReaderIndex();
        int length = in.readInt();
        //从buffer中读出头的大小，这会使得readIndex前移
        //剩余长度不够body体，reset 读指针
        if (in.readableBytes() < length) {
            //读指针回滚到header的readIndex位置处，没进行状态的保存
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data, 0, length);
        out.add(new String(data, "UTF-8"));


    }
}
