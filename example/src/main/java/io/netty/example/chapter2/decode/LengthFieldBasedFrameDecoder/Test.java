package io.netty.example.chapter2.decode.LengthFieldBasedFrameDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.example.chapter2.decode.StringProcessHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Classname Test
 * @Description TODO
 * @Date 2021/5/29 10:27
 * @Created by wangchao
 */
public class Test {
    static String content = "疯狂创客圈：高性能学习社群!";

    public static void main(String[] args) {
//        testLengthFieldBasedFrameDecoder1();

        //多字段Head-Content协议数据帧解析
//        testLengthFieldBasedFrameDecoder2();
        //添加魔数
        testLengthFieldBasedFrameDecoder3();
    }

    private static void testLengthFieldBasedFrameDecoder1() {
        try {

            /**
             * 数据包最大长度1024
             * 字段偏移量为0,表示字段长度处于数据包起始位置
             * 4:长度字段4字节
             * 内容字段偏移量-长度字段偏移量-长度字段字节数=4-0-4=0
             * 4:获取最终内容抛弃前面4字节
             */
            final LengthFieldBasedFrameDecoder spliter =
                    new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4);
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                protected void initChannel(EmbeddedChannel ch) {
                    ch.pipeline().addLast(spliter);
                    ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);

            for (int j = 0; j < 100; j++) {
                //1-3之间的随机数
                int random =  Math.abs(ThreadLocalRandom.current().nextInt(3)) + 1;
                ByteBuf buf = Unpooled.buffer();
                byte[] bytes = content.getBytes("UTF-8");
                buf.writeInt(bytes.length * random);
                for (int k = 0; k < random; k++) {
                    buf.writeBytes(bytes);
                }
                channel.writeInbound(buf);
            }


            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static final int VERSION = 100;
    public static  void testLengthFieldBasedFrameDecoder2() {
        try {
            /**
             *第 4 个参数 lengthAdjustment 为 2 ，长度调整值的计算方法为：内容字段偏移量 - 长度字段偏移里 - 长度字段的长度= 6 - 0 - 4 = 2
             * lengthAdjustment 就是夹在内容字段和长度字段中的部分 ― 版本号的长度。
             */
            final LengthFieldBasedFrameDecoder spliter =
                    new LengthFieldBasedFrameDecoder(1024, 0, 4, 2, 6);
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                protected void initChannel(EmbeddedChannel ch) {
                    ch.pipeline().addLast(spliter);
                    ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);

            for (int j = 1; j <= 100; j++) {
                ByteBuf buf = Unpooled.buffer();
                String s = j + "次发送->" + content;
                byte[] bytes = s.getBytes("UTF-8");
                buf.writeInt(bytes.length);
                buf.writeChar(VERSION);
                buf.writeBytes(bytes);
                channel.writeInbound(buf);
            }

            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static final int MAGICCODE = 9999;
    public static void testLengthFieldBasedFrameDecoder3() {
        try {

            final LengthFieldBasedFrameDecoder spliter =
                    new LengthFieldBasedFrameDecoder(1024, 2, 4, 4, 10);
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                protected void initChannel(EmbeddedChannel ch) {
                    ch.pipeline().addLast(spliter);
                    ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);

            for (int j = 1; j <= 100; j++) {
                ByteBuf buf = Unpooled.buffer();
                String s = j + "次发送->" + content;
                byte[] bytes = s.getBytes("UTF-8");
                buf.writeChar(VERSION);
                buf.writeInt(bytes.length);
                buf.writeInt(MAGICCODE);
                buf.writeBytes(bytes);
                channel.writeInbound(buf);
            }

            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
