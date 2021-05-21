package io.netty.example.chapter2.bytebuf;

import io.netty.buffer.*;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @Classname WriteReadTest
 * @Description TODO
 * @Date 2021/5/20 21:26
 * @Created by wangchao
 */
public class WriteReadTest {
    public static void main(String[] args) {
//        fun();
//        testRealease();
//        testAllocator();
//        testHeapBuffer();
//        testDirectBuffer();
//        testByteBufComposite();

        //byteBuf.hasArray()若复合内存只有一个缓冲区，则返回缓冲区的hashArray.否则直接false
        testByteBufCompositeToNio();
    }

    private static void testByteBufCompositeToNio() {
        CompositeByteBuf byteBufs = Unpooled.compositeBuffer(3);
        byteBufs.addComponent(Unpooled.wrappedBuffer(new byte[]{1, 2, 3}));
        byteBufs.addComponent(Unpooled.wrappedBuffer(new byte[]{4}));
        byteBufs.addComponent(Unpooled.wrappedBuffer(new byte[]{5,6}));

        //合并成一个单独的java-nio缓冲区
        ByteBuffer byteBuffer = byteBufs.nioBuffer(0, 6);
        byte[] array = byteBuffer.array();
        for (byte b : array) {
            System.out.println(b);
        }

        byteBufs.release();
    }

    private static void testByteBufComposite() {
        CompositeByteBuf byteBufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        ByteBuf head = Unpooled.copiedBuffer("请求头", UTF_8);
        ByteBuf body = Unpooled.copiedBuffer("请求体", UTF_8);
        byteBufs.addComponents(head, body);
        sendMsg(byteBufs);
        head.retain();
        byteBufs.release();

        byteBufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        //复用请求头
        body = Unpooled.copiedBuffer("请求体2", UTF_8);
        byteBufs.addComponents(head, body);
        sendMsg(byteBufs);
        byteBufs.release();
    }

    private static void sendMsg(CompositeByteBuf byteBufs) {
        byteBufs.forEach(k -> {
            int length = k.readableBytes();
            byte[] bytes = new byte[length];
            k.getBytes(k.readerIndex(), bytes);
            System.out.print(new String(bytes, UTF_8));
        });
        System.out.println();
    }

    private static void testDirectBuffer() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer();
        byteBuf.writeBytes("呵呵".getBytes(UTF_8));
        if (!byteBuf.hasArray()) {
            int length = byteBuf.readableBytes();
            byte[] bytes = new byte[length];
            //直接内存不能直接读取内部数组
            byteBuf.getBytes(byteBuf.readerIndex(), bytes);
            System.out.println(new String(bytes, UTF_8));
        }
        byteBuf.release();
    }

    private static void testHeapBuffer() {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        byteBuf.writeBytes("呵呵".getBytes(UTF_8));
        //是否是堆内存缓冲区,false则说明是直接内存或复合内存
        if (byteBuf.hasArray()) {
            byte[] array = byteBuf.array();
            int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
            int length = byteBuf.readableBytes();
            System.out.println(new String(array, offset, length, UTF_8));
        }
        byteBuf.release();
    }

    private static void testAllocator() {
        //默认分配器,初始容量9,最大容量100的缓冲区
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        //方法二：默认分配器，分配初始为256，最大容量Integer.MAX_VALUE 的缓冲
        buffer = ByteBufAllocator.DEFAULT.buffer();
        //方法三：非池化分配器，分配基于Java的堆内存缓冲区
        buffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
        //方法四：池化分配器，分配基于操作系统的管理的直接内存缓冲区
        buffer = PooledByteBufAllocator.DEFAULT.directBuffer();
    }

    private static void testRealease() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("after create:" + buffer.refCnt());
        buffer.retain();
        System.out.println("after create:" + buffer.refCnt());
        buffer.release();
        System.out.println("after create:" + buffer.refCnt());
        buffer.release();
        System.out.println("after create:" + buffer.refCnt());
//        buffer.retain(); 引用计数=0，抛出异常
        System.out.println("after create:" + buffer.refCnt());

    }

    private static void fun() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        print("动作：分配 ByteBuf(9, 100)", buffer);
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        print("动作：写入4个字节 (1,2,3,4)", buffer);
        System.out.println("start==========:get==========");
        getByteBuf(buffer);
        print("动作：取数据 ByteBuf", buffer);
        System.out.println("start==========:read==========");
        readByteBuf(buffer);
        print("动作：读完 ByteBuf", buffer);
    }

    private static void readByteBuf(ByteBuf buffer) {
        while (buffer.isReadable()) {
            System.out.println("读取一个字节:" + buffer.readByte());
        }
    }

    private static void getByteBuf(ByteBuf buffer) {
        for (int i = 0; i < buffer.readableBytes(); i++) {
            System.out.println("读取一个字节:" + buffer.getByte(i));
        }
    }

    public static void print(String action, ByteBuf b) {
        System.out.println("after ===========" + action + "============");
        System.out.println("1.0 isReadable(): " + b.isReadable());
        System.out.println("1.1 readerIndex(): " + b.readerIndex());
        System.out.println("1.2 readableBytes(): " + b.readableBytes());
        System.out.println("2.0 isWritable(): " + b.isWritable());
        System.out.println("2.1 writerIndex(): " + b.writerIndex());
        System.out.println("2.2 writableBytes(): " + b.writableBytes());
        System.out.println("3.0 capacity(): " + b.capacity());
        System.out.println("3.1 maxCapacity(): " + b.maxCapacity());
        System.out.println("3.2 maxWritableBytes(): " + b.maxWritableBytes());
    }
}
