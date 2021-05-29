package io.netty.example.chapter2.codec;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.example.chapter2.decode.ByteToMessageDecoder.Byte2IntegerDecoder;
import io.netty.example.chapter2.encode.Integer2ByteEncoder;

/**
 *
 **/
public class IntegerDuplexHandler extends CombinedChannelDuplexHandler<
        Byte2IntegerDecoder,
        Integer2ByteEncoder>
{
    public IntegerDuplexHandler() {
        super(new Byte2IntegerDecoder(), new Integer2ByteEncoder());
    }
}
