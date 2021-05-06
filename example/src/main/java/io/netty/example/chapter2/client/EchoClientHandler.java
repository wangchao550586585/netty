package io.netty.example.chapter2.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 代码清单 2-3 客户端的 ChannelHandler
 *
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */


/*
SimpleChannelInboundHandler 与ChannelInboundHandler
        你可能会想：为什么我们在客户端使用的是SimpleChannelInboundHandler，而不是在Echo-
        ServerHandler 中所使用的ChannelInboundHandlerAdapter 呢？这和两个因素的相互作用有
        关：业务逻辑如何处理消息以及Netty 如何管理资源。
        在客户端，当channelRead0()方法完成时，你已经有了传入消息，并且已经处理完它了。当该方
        法返回时，SimpleChannelInboundHandler 负责释放指向保存该消息的ByteBuf 的内存引用。
        在EchoServerHandler 中，你仍然需要将传入消息回送给发送者，而write()操作是异步的，直
        到channelRead()方法返回后可能仍然没有完成（如代码清单2-1 所示）。为此，EchoServerHandler
        扩展了ChannelInboundHandlerAdapter，其在这个时间点上不会释放消息。
        消息在EchoServerHandler 的channelReadComplete()方法中，当writeAndFlush()方
        法被调用时被释放
*/

@Sharable
//标记该类的实例可以被多个 Channel 共享
public class EchoClientHandler
    extends SimpleChannelInboundHandler<ByteBuf> {
    /**
     * 在到服务器的连接已经建立之后将被调用；
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //当被通知 Channel是活跃的时候，发送一条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!",
                CharsetUtil.UTF_8));
    }

    /**
     * 当从服务器接收到一条消息时被调用
     * 由服务区发送的消息可能被分块接收,也就是说该方法可能被调用多次
     * @param ctx
     * @param in
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, ByteBuf in) {
        //记录已接收消息的转储
        System.out.println(
                "Client received: " + in.toString(CharsetUtil.UTF_8));
    }

    @Override
    //在发生异常时，记录错误并关闭Channel
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
