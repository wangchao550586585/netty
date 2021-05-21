package io.netty.example.chapter2.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 代码清单 2-2 EchoServer 类
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
            throws Exception {
        int port = 8080;
        //调用服务器的 start()方法
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //(1) 创建EventLoopGroup,来接受和处理新的连接,以进行事件的处理，如接受新连接以及读/写数据
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //(2) 创建ServerBootstrap,以引导和绑定服务器
            ServerBootstrap b = new ServerBootstrap();
            //设置反应堆线程组,boss用于处理连接监听事件,worker用于处理传输数据事件
            b.group(boosGroup,workerGroup)
                    //(3) 指定所使用的 NIO 传输 Channel
                    .channel(NioServerSocketChannel.class)
                    //(4) 使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(port))
                    //是否开启TCP底层心跳机制，默认2小时,默认关闭
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    //设置buf分配器,默认池化buf分配器
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    //true：立即发送数据(默认true). false:将小的碎片数据连接成更大的报文来最小化所发送报文的数量(相当于开启Nagle算法)
                    .option(ChannelOption.TCP_NODELAY, true)
                    //(5) 添加一个EchoServerHandler到于Channel的 ChannelPipeline  ,配置子通道channel,也就是accept的channel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            //EchoServerHandler 被标注为@Shareable，所以我们可以总是使用同样的实例
                            //这里对于所有的客户端连接来说，都会使用同一个 EchoServerHandler，因为其被标注为@Sharable
                            //ChannelInitializer执行完initChannel,会将自己删除,因为一条通道只需要初始化一次
                            ch.pipeline().addLast(serverHandler);//使用一个EchoServerHandler 的实例初始化每一个新的Channel
                        }
                    });
            //(6) 异步地绑定服务器；调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() +
                    " started and listening for connections on " + f.channel().localAddress());
            //(7) 获取 Channel 的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            //(8) 关闭 EventLoopGroup，释放所有的资源
            //关闭包括内部selector选择器,内部轮询线程以及负责查询的子通道。
            workerGroup.shutdownGracefully().sync();
            boosGroup.shutdownGracefully().sync();
        }
    }
}
