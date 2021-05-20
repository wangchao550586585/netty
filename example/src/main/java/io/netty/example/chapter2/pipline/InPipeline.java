package io.netty.example.chapter2.pipline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @Classname InPipeline
 * @Description TODO
 * @Date 2021/5/19 22:51
 * @Created by wangchao
 */
public class InPipeline {
    static class InHandlerA extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("A被回调");
            super.channelRead(ctx, msg);
            //测试热插拔
            ctx.pipeline().remove(this);
        }
    }

    static class InHandlerB extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("B被回调");
            // 不调用父类方法后，将跳过后面的handler
//            super.channelRead(ctx, msg);
            //实际父类调用这个方法,调用后面的handler
            ctx.fireChannelRead(msg);


        }
    }

    static class InHandlerC extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("C被回调");
            super.channelRead(ctx, msg);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        fun1();
//        fun2();
    }

    private static void fun1() {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new InHandlerA());
                ch.pipeline().addLast(new InHandlerB());
                ch.pipeline().addLast(new InHandlerC());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(1);
        channel.writeInbound(buffer);
        channel.writeInbound(buffer);
        channel.writeInbound(buffer);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void fun2() {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new OutHandlerA());
                ch.pipeline().addLast(new OutHandlerB());
                ch.pipeline().addLast(new OutHandlerC());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(1);
        //向通道写个出站报文
        channel.writeOutbound(buffer);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    static class OutHandlerA extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("A被回调");
            super.write(ctx, msg, promise);
        }

    }

    static class OutHandlerB extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("B被回调");
            //不调用父类方法后，将跳过后面的handler
            super.write(ctx, msg, promise);
        }
    }

    static class OutHandlerC extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("C被回调");
            super.write(ctx, msg, promise);//不调用父类方法后，将跳过后面的handler
        }
    }
}
