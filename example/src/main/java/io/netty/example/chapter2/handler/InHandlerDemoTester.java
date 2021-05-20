package io.netty.example.chapter2.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @Classname InHandlerDemoTester
 * @Description TODO
 * @Date 2021/5/19 21:36
 * @Created by wangchao
 */
public class InHandlerDemoTester {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        InHandlerDemo inHandler = new InHandlerDemo();
        ChannelInitializer i = new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(inHandler);
            }
        };
        //创建嵌入式通道,主要用来模拟入站,出站操作.底层不进行实际传输
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(i);
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(1);

        System.out.println("--------------------------");
        // 模拟入站
        //入站数据,写入通道
        embeddedChannel.writeInbound(buffer);
        embeddedChannel.flush();

        System.out.println("---------------------------");
        //模拟入栈
        embeddedChannel.writeInbound(buffer);
        embeddedChannel.flush();

        System.out.println("--------------------------");
        //关闭通道
        embeddedChannel.close();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
