package io.netty.example.chapter2.decode.ReplayingDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @Classname IntegerAddDecoder
 * @Description TODO
 * @Date 2021/5/27 18:06
 * @Created by wangchao
 */
//不能线程共享.
public class IntegerAddDecoder extends ReplayingDecoder<IntegerAddDecoder.Status> {
    private int first;
    private int second;

    enum Status {
        PARSE_1, PARSE_2
    }

    public IntegerAddDecoder() {
        //构造函数中，需要初始化父类的state 属性，表示当前阶段
        super(Status.PARSE_1);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case PARSE_1:
                first = in.readInt();
                checkpoint(Status.PARSE_2);
                break;
            case PARSE_2:
                //从装饰器ByteBuf 中读取数据
                second = in.readInt();
                out.add(first + second);
                //第一步解析成功，
                // 进入第二步，并且设置“读指针断点”为当前的读取位置
                checkpoint(Status.PARSE_1);
                break;
            default:
                break;
        }
    }


}
