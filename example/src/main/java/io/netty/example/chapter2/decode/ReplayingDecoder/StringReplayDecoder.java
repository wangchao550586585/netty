package io.netty.example.chapter2.decode.ReplayingDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @Classname StringReplayDecoder
 * @Description TODO
 * @Date 2021/5/27 20:19
 * @Created by wangchao
 */
public class StringReplayDecoder extends ReplayingDecoder<StringReplayDecoder.Status> {
    private int length;
    private byte[] data;

    enum Status {
        PARSE_1, PARSE_2
    }

    public StringReplayDecoder() {
        super(Status.PARSE_1);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case PARSE_1:
                //第一步，从装饰器ByteBuf 中读取长度
                length = in.readInt();
                // 进入第二步，读取内容
                data = new byte[length];
                // 并且设置“读指针断点”为当前的读取位置
                checkpoint(Status.PARSE_2);
                break;
            case PARSE_2:
                //第二步，从装饰器ByteBuf 中读取内容数组
                in.readBytes(data);
                out.add(new String(data, "UTF-8"));
                checkpoint(Status.PARSE_1);
                break;
            default:
                break;

        }
    }


}
